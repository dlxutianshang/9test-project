package com.example.usermanagement.mapper;

import com.example.usermanagement.dto.RoleQueryDTO;
import com.example.usermanagement.entity.Role;

import java.util.List;

public interface RoleMapper {

    Role selectById(Long id);

    Role selectByRoleCode(String roleCode);

    List<Role> selectList(RoleQueryDTO queryDTO);

    Long selectCount(RoleQueryDTO queryDTO);

    List<Role> selectByUserId(Long userId);

    int insert(Role role);

    int update(Role role);

    int deleteById(Long id);
}
