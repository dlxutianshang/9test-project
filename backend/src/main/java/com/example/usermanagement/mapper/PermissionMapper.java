package com.example.usermanagement.mapper;

import com.example.usermanagement.entity.Permission;

import java.util.List;

public interface PermissionMapper {

    Permission selectById(Long id);

    Permission selectByPermissionCode(String permissionCode);

    List<Permission> selectAll();

    List<Permission> selectByRoleId(Long roleId);

    List<Permission> selectByUserId(Long userId);

    List<Permission> selectByParentId(Long parentId);

    int insert(Permission permission);

    int update(Permission permission);

    int deleteById(Long id);
}
