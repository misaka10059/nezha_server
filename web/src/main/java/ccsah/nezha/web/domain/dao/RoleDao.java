package ccsah.nezha.web.domain.dao;

import ccsah.nezha.web.domain.entity.Role;
import ccsah.nezha.web.domain.knowledge.RoleTypes;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *  Created by Rexxar.Zh on 19/11/8
 */
public interface RoleDao extends JpaRepository<Role, String> {
    Role findByName(RoleTypes name);
}
