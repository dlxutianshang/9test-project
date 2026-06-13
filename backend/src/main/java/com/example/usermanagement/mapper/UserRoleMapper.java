package com.example.usermanagement.mapper;

import com.example.usermanagement.entity.UserRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserRoleMapper {

    List<UserRole> selectByUserId(Long userId);

    List<UserRole> selectByRoleId(Long roleId);

    int insert(UserRole userRole);

    int batchInsert(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);

    int deleteByUserId(Long userId);

    int deleteByRoleId(Long roleId);

    int deleteByUserIdAndRoleIds(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);
}
