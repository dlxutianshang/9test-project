package com.example.usermanagement.mapper;

import com.example.usermanagement.entity.RolePermission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RolePermissionMapper {

    List<RolePermission> selectByRoleId(Long roleId);

    List<RolePermission> selectByPermissionId(Long permissionId);

    int insert(RolePermission rolePermission);

    int batchInsert(@Param("roleId") Long roleId, @Param("permissionIds") List<Long> permissionIds);

    int deleteByRoleId(Long roleId);

    int deleteByPermissionId(Long permissionId);

    int deleteByRoleIdAndPermissionIds(@Param("roleId") Long roleId, @Param("permissionIds") List<Long> permissionIds);
}
