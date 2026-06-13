package com.example.usermanagement.service;

import com.example.usermanagement.entity.Permission;

import java.util.List;

public interface PermissionService {

    Permission getById(Long id);

    Permission getByPermissionCode(String permissionCode);

    List<Permission> getAllPermissions();

    List<Permission> getPermissionTree();

    List<Permission> getByParentId(Long parentId);

    List<Permission> getByRoleId(Long roleId);

    List<Permission> getByUserId(Long userId);

    Permission createPermission(Permission permission);

    Permission updatePermission(Permission permission);

    void deletePermission(Long id);
}
