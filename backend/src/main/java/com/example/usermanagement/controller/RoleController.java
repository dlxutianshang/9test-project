package com.example.usermanagement.controller;

import com.example.usermanagement.annotation.OperationLogAnnotation;
import com.example.usermanagement.common.PageResult;
import com.example.usermanagement.common.Result;
import com.example.usermanagement.dto.AssignPermissionDTO;
import com.example.usermanagement.dto.RoleQueryDTO;
import com.example.usermanagement.entity.Permission;
import com.example.usermanagement.entity.Role;
import com.example.usermanagement.service.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@Api(tags = "角色管理接口")
@RestController
@RequestMapping("/api/roles")
@CrossOrigin
public class RoleController {

    @Resource
    private RoleService roleService;

    @ApiOperation("分页查询角色列表")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('role:list')")
    public Result<PageResult<Role>> getRolePage(RoleQueryDTO queryDTO) {
        return Result.success(roleService.getRolePage(queryDTO));
    }

    @ApiOperation("获取所有角色列表")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('role:list')")
    public Result<List<Role>> getAllRoles() {
        return Result.success(roleService.getAllRoles());
    }

    @ApiOperation("根据ID获取角色详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('role:list')")
    public Result<Role> getRoleById(@PathVariable Long id) {
        return Result.success(roleService.getById(id));
    }

    @ApiOperation("新增角色")
    @PostMapping
    @PreAuthorize("hasAuthority('role:add')")
    @OperationLogAnnotation(operation = "新增角色")
    public Result<Role> createRole(@Valid @RequestBody Role role) {
        return Result.success("创建成功", roleService.createRole(role));
    }

    @ApiOperation("编辑角色")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('role:edit')")
    @OperationLogAnnotation(operation = "编辑角色")
    public Result<Role> updateRole(@PathVariable Long id, @RequestBody Role role) {
        role.setId(id);
        return Result.success("更新成功", roleService.updateRole(role));
    }

    @ApiOperation("删除角色")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('role:delete')")
    @OperationLogAnnotation(operation = "删除角色")
    public Result<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return Result.success("删除成功", null);
    }

    @ApiOperation("获取角色拥有的权限")
    @GetMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('role:list')")
    public Result<List<Permission>> getRolePermissions(@PathVariable Long id) {
        return Result.success(roleService.getRolePermissions(id));
    }

    @ApiOperation("为角色分配权限")
    @PostMapping("/assign-permissions")
    @PreAuthorize("hasAuthority('role:assignPermission')")
    @OperationLogAnnotation(operation = "分配角色权限")
    public Result<Void> assignPermissions(@RequestBody AssignPermissionDTO dto) {
        roleService.assignPermissions(dto);
        return Result.success("权限分配成功", null);
    }
}
