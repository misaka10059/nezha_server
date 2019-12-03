package ccsah.nezha.web.domain.dao;

import ccsah.nezha.web.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Rexxar.Zh on 19/11/8
 */
public interface UserDao extends JpaRepository<User, String> {

    User findByContactAndIsDeleted(String contact, boolean isDeleted);

    User findByEmailAndIsDeleted(String email, boolean b);

    User findByUsernumberAndIsDeleted(int usernumber, boolean b);

    int countByIsDeleted(boolean b);

    List<User> findByIsDeleted(boolean b, Pageable pageRequest);

    Page<User> findAll(Specification<User> spec, Pageable pageRequest);

}
