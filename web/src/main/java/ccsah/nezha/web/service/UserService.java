package ccsah.nezha.web.service;

import ccsah.nezha.web.component.AuthorityManager;
import ccsah.nezha.web.domain.dao.DepartmentDao;
import ccsah.nezha.web.domain.dao.RoleDao;
import ccsah.nezha.web.domain.dao.UserDao;
import ccsah.nezha.web.domain.dao.UserDaoSpec;
import ccsah.nezha.web.domain.dto.user.UserDto;
import ccsah.nezha.web.domain.entity.Department;
import ccsah.nezha.web.domain.entity.Role;
import ccsah.nezha.web.domain.entity.User;
import ccsah.nezha.web.domain.knowledge.RoleTypes;
import ccsah.nezha.web.util.ValidateUserInfo;
import ccsfr.core.domain.EntityManagerHolder;
import ccsfr.core.domain.PageOffsetRequest;
import ccsfr.core.util.StringUtil;
import ccsfr.core.web.PageData;
import ccsfr.core.web.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Rexxar.Zh on 19/10/11
 */
@Service
@Slf4j
public class UserService {

    @Autowired
    RoleDao roleDao;

    @Autowired
    UserDao userDao;

    @Autowired
    DepartmentDao departmentDao;

    @Autowired
    AuthorityManager authorityManager;

    public UserDto login(String info, String password) {
        User user = findUserByUserInfo(info);
        if (user != null && user.validatePassword(password)) {
            user.updateTime();
            UserDto userInfo = getUserDto(user);
            String token = authorityManager.register(user);
            userInfo.setToken(token);
            return userInfo;
        } else throw new ServiceException(503, "用户不存在或密码错误");
    }

    @Transactional
    public UserDto register(String username, String password, String contact, String email, String department, RoleTypes roleType) {
        contact = validateContact(contact);
        email = validateEmail(email);
        Department departmentEntity = getDepartment(department);
        Role role = getRole(roleType);

        User user = User.create(username, password, contact, email, departmentEntity, role);
        EntityManagerHolder.getEntityManager().flush();
        EntityManagerHolder.getEntityManager().refresh(user);

        return getUserDto(user);
    }

    //TODO 权限控制
    @Transactional
    public UserDto deleteUser(String id, String operatorToken) {
        User user = getUserById(id);
        user.deleteLogical();
        return getUserDto(user);
    }

    @Transactional
    public void addRole(RoleTypes name) {
        Role role = roleDao.findByName(name);
        if (role == null) {
            Role.create(name);
        }
    }

    public List<String> listDepartment() {
        return departmentDao.findByIsDeletedOrderByOrderIndexAsc(false).stream()
                .map(Department::getName)
                .collect(Collectors.toList());
    }

    @Transactional
    public void addDepartment(String name, String code, int order) {
        Department department = departmentDao.findByName(name);
        if (department == null) {
            Department.create(name, code, order);
        }
    }


    public UserDto getUserInfoById(String userId) {
        User user = getUserById(userId);
        return getUserDto(user);
    }

    @Transactional
    public UserDto update(String userId, String username, String password, String contact, String email, String department, RoleTypes roleType) {
        User user = getUserById(userId);
        user.setUsername(username);
        if (!StringUtil.isNullOrEmpty(password)) {
            user.encodePassword(password);
        }
        if (!StringUtil.isNullOrEmpty(contact) && !contact.equals(user.getContact())) {
            user.setContact(validateContact(contact));
        }
        if (!StringUtil.isNullOrEmpty(email) && !email.equals(user.getContact())) {
            user.setEmail(validateEmail(email));
        }
        if (!StringUtil.isNullOrEmpty(department) && !department.equals(user.getDepartment().getName())) {
            user.setDepartment(getDepartment(department));
        }
        if (!roleType.equals(RoleTypes.NONE) && !roleType.name().equals(user.getRole().getName())) {
            user.setRole(getRole(roleType));
        }
        return getUserDto(user);
    }


    @Transactional
    public UserDto updatePersonal(String userId, String contact, String email) {
        User user = getUserById(userId);
        if (!StringUtil.isNullOrEmpty(contact) && !contact.equals(user.getContact())) {
            user.setContact(validateContact(contact));
        }
        if (!StringUtil.isNullOrEmpty(email) && !email.equals(user.getContact())) {
            user.setEmail(validateEmail(email));
        }
        return getUserDto(user);
    }

    public PageData<UserDto> listUser(String operatorToken, String userName, String department, long startRegisterTime, long endRegisterTime, RoleTypes roleType, Pageable pageRequest) {

        Department targetDepartment = getDepartment(department);
        Role targetRole = getRole(roleType);

        Specification<User> querySpec = UserDaoSpec.getVariableSpec(
                userName,
                targetDepartment,
                startRegisterTime,
                endRegisterTime,
                targetRole);

        Page<User> userList = userDao.findAll(querySpec, pageRequest);
        List<UserDto> userDtoList = userList.getContent().stream()
                .map(this::getUserDto)
                .collect(Collectors.toList());
        return new PageData<>(userDtoList, (int) userList.getTotalElements());
    }

    @Transactional
    public void deleteMultipleUser(List<String> targetIdList, String operatorToken) {
        targetIdList.forEach(id ->
                getUserById(id).deleteLogical());
    }

    private String validateEmail(String email) {
        if (!ValidateUserInfo.isEmail(email)) {
            email = "";
        } else if (userDao.findByEmailAndIsDeleted(email, false) != null) {
            throw new ServiceException(507, "邮箱已经注册");
        }
        return email;
    }

    private String validateContact(String contact) {
        if (!ValidateUserInfo.isMobileNumber(contact)) {
            contact = "";
        } else if (userDao.findByContactAndIsDeleted(contact, false) != null) {
            throw new ServiceException(506, "手机号已经注册");
        }
        return contact;
    }

    Department getDepartment(String department) {
        if (StringUtil.isNullOrEmpty(department)) {
            return null;
        }
        Department departmentEntity = departmentDao.findByName(department);
        if (departmentEntity == null) {
            throw new ServiceException(505, "错误的部门");
        }
        return departmentEntity;
    }

    private Role getRole(RoleTypes roleType) {
        if (roleType.equals(RoleTypes.NONE)) {
            return null;
        }
        return roleDao.findByName(roleType);
    }

    private UserDto getUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getDepartment().getName(),
                user.getDisplayUsernumber(),
                user.getContact(),
                user.getEmail(),
                user.getRole().getName(),
                user.getUtime().getTime(),
                user.getCtime().getTime());
    }

    private User findUserByUserInfo(String info) {
        if (ValidateUserInfo.isMobileNumber(info)) {
            return userDao.findByContactAndIsDeleted(info, false);
        }
        if (ValidateUserInfo.isEmail(info)) {
            return userDao.findByEmailAndIsDeleted(info, false);
        }
        return userDao.findByUsernumberAndIsDeleted(ValidateUserInfo.getInnerUsernumber(info), false);
    }

    private User getUserById(String id) {
        User user = userDao.findOne(id);
        if (user == null) {
            throw new ServiceException(502, "用户不存在");
        }
        return user;
    }

    public UserDto getUserInfoByInfo(String info) {
        return getUserDto(findUserByUserInfo(info));
    }
}
