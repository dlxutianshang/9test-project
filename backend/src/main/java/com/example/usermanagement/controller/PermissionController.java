package com.example.usermanagement.controller;

import com.example.usermanagement.annotation.OperationLogAnnotation;
import com.example.usermanagement.common.Result;
import com.example.usermanagement.entity.Permission;
import com.example.usermanagement.service.PermissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@Api(tags = "权限管理接口")
@RestController
@RequestMapping("/api/permissions")
@CrossOrigin
public class PermissionController {

    @Resource
    private PermissionService permissionService;

    @ApiOperation("获取所有权限（树形结构）")
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('permission:list')")
    public Result<List<Permission>> getPermissionTree() {
        return Result.success(permissionService.getPermissionTree());
    }

    @ApiOperation("获取所有权限列表")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('permission:list')")
    public Result<List<Permission>> getAllPermissions() {
        return Result.success(permissionService.getAllPermissions());
    }

    @ApiOperation("根据ID获取权限详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:list')")
    public Result<Permission> getPermissionById(@PathVariable Long id) {
        return Result.success(permissionService.getById(id));
    }

    @ApiOperation("获取子权限")
    @GetMapping("/parent/{parentId}")
    @PreAuthorize("hasAuthority('permission:list')")
    public Result<List<Permission>> getByParentId(@PathVariable Long parentId) {
        return Result.success(permissionService.getByParentId(parentId));
    }

    @ApiOperation("新增权限")
    @PostMapping
    @PreAuthorize("hasAuthority('permission:add')")
    @OperationLogAnnotation(operation = "新增权限")
    public Result<Permission> createPermission(@Valid @RequestBody Permission permission) {
        return Result.success("创建成功", permissionService.createPermission(permission));
    }

    @ApiOperation("编辑权限")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:edit')")
    @OperationLogAnnotation(operation = "编辑权限")
    public Result<Permission> updatePermission(@PathVariable Long id, @RequestBody Permission permission) {
        permission.setId(id);
        return Result.success("更新成功", permissionService.updatePermission(permission));
    }

    @ApiOperation("删除权限")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:delete')")
    @OperationLogAnnotation(operation = "删除权限")
    public Result<Void> deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return Result.success("删除成功", null);
    }
}
