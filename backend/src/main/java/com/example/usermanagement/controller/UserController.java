package com.example.usermanagement.controller;

import com.example.usermanagement.annotation.OperationLogAnnotation;
import com.example.usermanagement.common.PageResult;
import com.example.usermanagement.common.Result;
import com.example.usermanagement.dto.AssignRoleDTO;
import com.example.usermanagement.dto.UserQueryDTO;
import com.example.usermanagement.entity.Permission;
import com.example.usermanagement.entity.Role;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Api(tags = "用户管理接口")
@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {

    @Resource
    private UserService userService;

    @ApiOperation("分页查询用户列表")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('user:list')")
    public Result<PageResult<User>> getUserPage(UserQueryDTO queryDTO) {
        return Result.success(userService.getUserPage(queryDTO));
    }

    @ApiOperation("根据ID获取用户详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('user:list')")
    public Result<User> getUserById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    @ApiOperation("新增用户")
    @PostMapping
    @PreAuthorize("hasAuthority('user:add')")
    @OperationLogAnnotation(operation = "新增用户")
    public Result<User> createUser(@Valid @RequestBody User user) {
        return Result.success("创建成功", userService.createUser(user));
    }

    @ApiOperation("编辑用户")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('user:edit')")
    @OperationLogAnnotation(operation = "编辑用户")
    public Result<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        return Result.success("更新成功", userService.updateUser(user));
    }

    @ApiOperation("删除用户")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('user:delete')")
    @OperationLogAnnotation(operation = "删除用户")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success("删除成功", null);
    }

    @ApiOperation("启用/禁用用户")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('user:status')")
    @OperationLogAnnotation(operation = "修改用户状态")
    public Result<Void> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.updateUserStatus(id, status);
        return Result.success("状态更新成功", null);
    }

    @ApiOperation("重置用户密码")
    @PutMapping("/{id}/reset-password")
    @PreAuthorize("hasAuthority('user:resetPassword')")
    @OperationLogAnnotation(operation = "重置用户密码")
    public Result<Void> resetPassword(@PathVariable Long id,
                                       @RequestParam(required = false, defaultValue = "123456") String newPassword) {
        userService.resetPassword(id, newPassword);
        return Result.success("密码重置成功", null);
    }

    @ApiOperation("获取用户角色")
    @GetMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('user:list')")
    public Result<List<Role>> getUserRoles(@PathVariable Long id) {
        return Result.success(userService.getUserRoles(id));
    }

    @ApiOperation("获取用户权限")
    @GetMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('user:list')")
    public Result<List<Permission>> getUserPermissions(@PathVariable Long id) {
        return Result.success(userService.getUserPermissions(id));
    }

    @ApiOperation("为用户分配角色")
    @PostMapping("/assign-roles")
    @PreAuthorize("hasAuthority('user:edit')")
    @OperationLogAnnotation(operation = "分配用户角色")
    public Result<Void> assignRoles(@RequestBody AssignRoleDTO dto) {
        userService.assignRoles(dto);
        return Result.success("角色分配成功", null);
    }

    @ApiOperation("导出用户数据")
    @GetMapping("/export")
    @PreAuthorize("hasAuthority('export:data')")
    @OperationLogAnnotation(operation = "导出用户数据")
    public void exportUsers(UserQueryDTO queryDTO, HttpServletResponse response) {
        userService.exportUsers(queryDTO, response);
    }

    @ApiOperation("导入用户数据")
    @PostMapping("/import")
    @PreAuthorize("hasAuthority('import:data')")
    @OperationLogAnnotation(operation = "导入用户数据")
    public Result<Void> importUsers(@RequestParam("file") MultipartFile file) {
        userService.importUsers(file);
        return Result.success("导入成功", null);
    }
}
