package ccsah.nezha.web.controller;

import ccsah.nezha.web.domain.dto.user.UserDto;
import ccsah.nezha.web.domain.entity.User;
import ccsah.nezha.web.domain.knowledge.RoleTypes;
import ccsah.nezha.web.service.UserService;
import ccsfr.core.domain.PageOffsetRequest;
import ccsfr.core.web.BaseApiController;
import ccsfr.core.web.PageData;
import ccsfr.core.web.ResponseData;
import com.wordnik.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * Created by Rexxar.Zh on 19/10/31
 */
@Api(value = "/user", description = "用户模块")
@RequestMapping("user")
@RestController
public class UserController extends BaseApiController {

    @Autowired
    UserService userService;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ResponseData<UserDto> login(@RequestParam("userinfo") String userInfo,
                                       @RequestParam("password") String password) {

        return ResponseData.ok(userService.login(userInfo, password));
    }

    @RequestMapping(value = "/list_department", method = RequestMethod.GET)
    public ResponseData<List<String>> getUserList(@RequestParam("operator_token") String operatorToken) {

        return ResponseData.ok(userService.listDepartment());
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseData<PageData<UserDto>> getUserList(@RequestParam("operator_token") String operatorToken,
                                                       @RequestParam(value = "username", required = false) String userName,
                                                       @RequestParam(value = "department", required = false) String department,
                                                       @RequestParam(value = "start_register_time", required = false, defaultValue = "0") long startRegisterTime,
                                                       @RequestParam(value = "end_register_time", required = false, defaultValue = "0") long endRegisterTime,
                                                       @RequestParam(value = "role_type", required = false, defaultValue = "NONE") RoleTypes roleType,
                                                       @RequestParam("start_index") int startIndex,
                                                       @RequestParam("end_index") int endIndex) {
        Pageable pageRequest = PageOffsetRequest.getPageableByIndex(
                startIndex,
                endIndex,
                new Sort(Sort.Direction.ASC, "usernumber"));
        PageData<UserDto> page = userService.listUser(
                operatorToken,
                userName,
                department,
                startRegisterTime,
                endRegisterTime,
                roleType,
                pageRequest);
        return ResponseData.ok(page);
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseData<UserDto> register(@RequestParam("operator_token") String operatorToken,
                                          @RequestParam("username") String username,
                                          @RequestParam(value = "contact", required = false) String contact,
                                          @RequestParam(value = "email", required = false) String email,
                                          @RequestParam("password") String password,
                                          @RequestParam("department") String department,
                                          @RequestParam("role") RoleTypes roleType
    ) {
        UserDto userDto = userService.register(username, password, contact, email, department, roleType);
        return ResponseData.ok(userDto);
    }

    @RequestMapping(value = "/get_user_by_id", method = RequestMethod.GET)
    public ResponseData<UserDto> getUserInfoById(@RequestParam("id") String id) {
        return ResponseData.ok(userService.getUserInfoById(id));
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResponseData<UserDto> update(@RequestParam("operator_token") String operatorToken,
                                        @RequestParam("user_id") String userId,
                                        @RequestParam("username") String username,
                                        @RequestParam(value = "contact", required = false) String contact,
                                        @RequestParam(value = "email", required = false) String email,
                                        @RequestParam(value = "password", required = false) String password,
                                        @RequestParam("department") String department,
                                        @RequestParam("role") RoleTypes roleType
    ) {
        return ResponseData.ok(userService.update(userId, username, password, contact, email, department, roleType));
    }

    @RequestMapping(value = "/update_personal", method = RequestMethod.POST)
    public ResponseData<UserDto> updatePersonal(@RequestParam("operator_token") String operatorToken,
                                                @RequestParam("user_id") String userId,
                                                @RequestParam(value = "contact", required = false) String contact,
                                                @RequestParam(value = "email", required = false) String email
    ) {
        return ResponseData.ok(userService.updatePersonal(userId, contact, email));
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseData<String> delete(@RequestParam("operator_token") String operatorToken,
                                       @RequestParam("target_id") String targetId) {
        userService.deleteUser(targetId, operatorToken);
        return ResponseData.ok("OK");
    }

    @RequestMapping(value = "/delete_multiple", method = RequestMethod.DELETE)
    public ResponseData<String> deleteMultiple(@RequestParam("operator_token") String operatorToken,
                                               @RequestParam("target_id_list") List<String> targetIdList) {
        userService.deleteMultipleUser(targetIdList, operatorToken);
        return ResponseData.ok("OK");
    }

}
