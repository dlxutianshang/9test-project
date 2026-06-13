package com.example.usermanagement.controller;

import com.example.usermanagement.annotation.OperationLogAnnotation;
import com.example.usermanagement.common.Result;
import com.example.usermanagement.dto.*;
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
import java.util.HashMap;
import java.util.Map;

@Api(tags = "认证接口")
@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Resource
    private UserService userService;

    @ApiOperation("用户登录")
    @PostMapping("/login")
    @OperationLogAnnotation(operation = "用户登录")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO loginDTO) {
        Map<String, Object> result = userService.login(loginDTO);
        return Result.success("登录成功", result);
    }

    @ApiOperation("用户注册")
    @PostMapping("/register")
    @OperationLogAnnotation(operation = "用户注册")
    public Result<User> register(@Valid @RequestBody RegisterDTO registerDTO) {
        User user = userService.register(registerDTO);
        return Result.success("注册成功", user);
    }

    @ApiOperation("获取当前用户信息")
    @GetMapping("/me")
    public Result<Map<String, Object>> getCurrentUser() {
        User user = userService.getCurrentUser();
        if (user == null) {
            return Result.error(401, "未登录");
        }
        user.setPassword(null);
        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("roles", userService.getUserRoles(user.getId()));
        result.put("permissions", userService.getUserPermissions(user.getId()));
        return Result.success(result);
    }

    @ApiOperation("修改密码")
    @PostMapping("/change-password")
    @OperationLogAnnotation(operation = "修改密码")
    public Result<Void> changePassword(@Valid @RequestBody PasswordUpdateDTO dto) {
        User user = userService.getCurrentUser();
        if (user == null) {
            return Result.error(401, "未登录");
        }
        userService.changePassword(user.getId(), dto);
        return Result.success("密码修改成功", null);
    }

    @ApiOperation("申请密码重置（发送重置链接）")
    @PostMapping("/forgot-password")
    @OperationLogAnnotation(operation = "申请密码重置")
    public Result<Void> forgotPassword(@Valid @RequestBody PasswordResetDTO dto) {
        userService.forgotPassword(dto);
        return Result.success("重置链接已生成，请检查系统日志获取token", null);
    }

    @ApiOperation("通过token重置密码")
    @PostMapping("/reset-password")
    @OperationLogAnnotation(operation = "重置密码")
    public Result<Void> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        userService.resetPasswordByToken(token, newPassword);
        return Result.success("密码重置成功", null);
    }

    @ApiOperation("退出登录")
    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success("退出成功", null);
    }
}
