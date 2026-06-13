package com.example.usermanagement.service.impl;

import com.example.usermanagement.entity.Permission;
import com.example.usermanagement.exception.BusinessException;
import com.example.usermanagement.mapper.PermissionMapper;
import com.example.usermanagement.mapper.RolePermissionMapper;
import com.example.usermanagement.service.PermissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Resource
    private PermissionMapper permissionMapper;
    @Resource
    private RolePermissionMapper rolePermissionMapper;

    @Override
    public Permission getById(Long id) {
        return permissionMapper.selectById(id);
    }

    @Override
    public Permission getByPermissionCode(String permissionCode) {
        return permissionMapper.selectByPermissionCode(permissionCode);
    }

    @Override
    public List<Permission> getAllPermissions() {
        return permissionMapper.selectAll();
    }

    @Override
    public List<Permission> getPermissionTree() {
        List<Permission> all = permissionMapper.selectAll();
        Map<Long, List<Permission>> childrenMap = all.stream()
                .collect(Collectors.groupingBy(p -> p.getParentId() != null ? p.getParentId() : 0L));

        List<Permission> roots = all.stream()
                .filter(p -> p.getParentId() == null || p.getParentId() == 0)
                .collect(Collectors.toList());

        fillChildren(roots, childrenMap);
        return roots;
    }

    private void fillChildren(List<Permission> nodes, Map<Long, List<Permission>> childrenMap) {
        for (Permission node : nodes) {
            List<Permission> children = childrenMap.get(node.getId());
            if (children != null && !children.isEmpty()) {
                node.setChildren(children);
                fillChildren(children, childrenMap);
            } else {
                node.setChildren(new ArrayList<>());
            }
        }
    }

    @Override
    public List<Permission> getByParentId(Long parentId) {
        return permissionMapper.selectByParentId(parentId);
    }

    @Override
    public List<Permission> getByRoleId(Long roleId) {
        return permissionMapper.selectByRoleId(roleId);
    }

    @Override
    public List<Permission> getByUserId(Long userId) {
        return permissionMapper.selectByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Permission createPermission(Permission permission) {
        Permission exist = permissionMapper.selectByPermissionCode(permission.getPermissionCode());
        if (exist != null) {
            throw new BusinessException("权限编码已存在");
        }
        if (permission.getStatus() == null) {
            permission.setStatus(1);
        }
        if (permission.getType() == null) {
            permission.setType(2);
        }
        if (permission.getParentId() == null) {
            permission.setParentId(0L);
        }
        if (permission.getSortOrder() == null) {
            permission.setSortOrder(0);
        }
        permissionMapper.insert(permission);
        return permission;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Permission updatePermission(Permission permission) {
        Permission exist = permissionMapper.selectById(permission.getId());
        if (exist == null) {
            throw new BusinessException("权限不存在");
        }
        permissionMapper.update(permission);
        return permissionMapper.selectById(permission.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePermission(Long id) {
        rolePermissionMapper.deleteByPermissionId(id);
        permissionMapper.deleteById(id);
    }
}
