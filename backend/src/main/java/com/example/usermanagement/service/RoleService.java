package com.example.usermanagement.service;

import com.example.usermanagement.common.PageResult;
import com.example.usermanagement.dto.AssignPermissionDTO;
import com.example.usermanagement.dto.RoleQueryDTO;
import com.example.usermanagement.entity.Permission;
import com.example.usermanagement.entity.Role;

import java.util.List;

public interface RoleService {

    Role getById(Long id);

    Role getByRoleCode(String roleCode);

    PageResult<Role> getRolePage(RoleQueryDTO queryDTO);

    List<Role> getAllRoles();

    Role createRole(Role role);

    Role updateRole(Role role);

    void deleteRole(Long id);

    List<Permission> getRolePermissions(Long roleId);

    void assignPermissions(AssignPermissionDTO dto);
}
