package com.example.usermanagement.service.impl;

import com.example.usermanagement.common.PageResult;
import com.example.usermanagement.dto.AssignPermissionDTO;
import com.example.usermanagement.dto.RoleQueryDTO;
import com.example.usermanagement.entity.Permission;
import com.example.usermanagement.entity.Role;
import com.example.usermanagement.entity.RolePermission;
import com.example.usermanagement.exception.BusinessException;
import com.example.usermanagement.mapper.PermissionMapper;
import com.example.usermanagement.mapper.RoleMapper;
import com.example.usermanagement.mapper.RolePermissionMapper;
import com.example.usermanagement.mapper.UserRoleMapper;
import com.example.usermanagement.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Resource
    private RoleMapper roleMapper;
    @Resource
    private PermissionMapper permissionMapper;
    @Resource
    private RolePermissionMapper rolePermissionMapper;
    @Resource
    private UserRoleMapper userRoleMapper;

    @Override
    public Role getById(Long id) {
        return roleMapper.selectById(id);
    }

    @Override
    public Role getByRoleCode(String roleCode) {
        return roleMapper.selectByRoleCode(roleCode);
    }

    @Override
    public PageResult<Role> getRolePage(RoleQueryDTO queryDTO) {
        queryDTO.calcOffset();
        Long total = roleMapper.selectCount(queryDTO);
        List<Role> records = roleMapper.selectList(queryDTO);
        return new PageResult<>(total, records, queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public List<Role> getAllRoles() {
        RoleQueryDTO dto = new RoleQueryDTO();
        dto.setPageNum(null);
        dto.setPageSize(null);
        return roleMapper.selectList(dto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Role createRole(Role role) {
        Role exist = roleMapper.selectByRoleCode(role.getRoleCode());
        if (exist != null) {
            throw new BusinessException("角色编码已存在");
        }
        if (role.getStatus() == null) {
            role.setStatus(1);
        }
        roleMapper.insert(role);
        return role;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Role updateRole(Role role) {
        Role exist = roleMapper.selectById(role.getId());
        if (exist == null) {
            throw new BusinessException("角色不存在");
        }
        roleMapper.update(role);
        return roleMapper.selectById(role.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long id) {
        userRoleMapper.deleteByRoleId(id);
        rolePermissionMapper.deleteByRoleId(id);
        roleMapper.deleteById(id);
    }

    @Override
    public List<Permission> getRolePermissions(Long roleId) {
        return permissionMapper.selectByRoleId(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(AssignPermissionDTO dto) {
        rolePermissionMapper.deleteByRoleId(dto.getRoleId());
        if (dto.getPermissionIds() != null && !dto.getPermissionIds().isEmpty()) {
            rolePermissionMapper.batchInsert(dto.getRoleId(), dto.getPermissionIds());
        }
    }
}
