package com.example.usermanagement.service;

import com.example.usermanagement.common.PageResult;
import com.example.usermanagement.dto.*;
import com.example.usermanagement.entity.Permission;
import com.example.usermanagement.entity.Role;
import com.example.usermanagement.entity.User;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface UserService {

    User getById(Long id);

    User getByUsername(String username);

    Map<String, Object> login(LoginDTO loginDTO);

    User register(RegisterDTO registerDTO);

    PageResult<User> getUserPage(UserQueryDTO queryDTO);

    List<User> getUserList(UserQueryDTO queryDTO);

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(Long id);

    void updateUserStatus(Long id, Integer status);

    void resetPassword(Long id, String newPassword);

    void changePassword(Long userId, PasswordUpdateDTO dto);

    List<Role> getUserRoles(Long userId);

    List<Permission> getUserPermissions(Long userId);

    void assignRoles(AssignRoleDTO dto);

    void forgotPassword(PasswordResetDTO dto);

    void resetPasswordByToken(String token, String newPassword);

    void exportUsers(UserQueryDTO queryDTO, HttpServletResponse response);

    void importUsers(MultipartFile file);

    User getCurrentUser();
}
