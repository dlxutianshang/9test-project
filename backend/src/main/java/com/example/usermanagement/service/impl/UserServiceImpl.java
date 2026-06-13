package com.example.usermanagement.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.example.usermanagement.common.PageResult;
import com.example.usermanagement.common.StatusCode;
import com.example.usermanagement.config.JwtConfig;
import com.example.usermanagement.dto.*;
import com.example.usermanagement.entity.*;
import com.example.usermanagement.exception.BusinessException;
import com.example.usermanagement.mapper.*;
import com.example.usermanagement.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private PermissionMapper permissionMapper;
    @Resource
    private UserRoleMapper userRoleMapper;
    @Resource
    private PasswordResetMapper passwordResetMapper;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private JwtConfig jwtConfig;

    @Override
    public User getById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public User getByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public Map<String, Object> login(LoginDTO loginDTO) {
        User user = userMapper.selectByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new BusinessException(StatusCode.USER_NOT_FOUND, "用户不存在");
        }
        if (user.getStatus() == 0) {
            throw new BusinessException(StatusCode.USER_DISABLED, "用户已被禁用，请联系管理员");
        }
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(StatusCode.PASSWORD_ERROR, "密码错误");
        }
        String token = jwtConfig.generateToken(user.getId(), user.getUsername());
        user.setPassword(null);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("tokenType", jwtConfig.getPrefix());
        result.put("expiresIn", jwtConfig.getExpiration());
        result.put("user", user);
        result.put("roles", getUserRoles(user.getId()));
        result.put("permissions", getUserPermissions(user.getId()));
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User register(RegisterDTO registerDTO) {
        User existUser = userMapper.selectByUsername(registerDTO.getUsername());
        if (existUser != null) {
            throw new BusinessException(StatusCode.USER_ALREADY_EXISTS, "用户名已存在");
        }
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setNickname(registerDTO.getNickname());
        user.setEmail(registerDTO.getEmail());
        user.setPhone(registerDTO.getPhone());
        user.setStatus(1);
        userMapper.insert(user);

        UserRole userRole = new UserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(2L);
        userRoleMapper.insert(userRole);

        user.setPassword(null);
        return user;
    }

    @Override
    public PageResult<User> getUserPage(UserQueryDTO queryDTO) {
        queryDTO.calcOffset();
        Long total = userMapper.selectCount(queryDTO);
        List<User> records = userMapper.selectList(queryDTO);
        for (User u : records) {
            u.setPassword(null);
        }
        return new PageResult<>(total, records, queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public List<User> getUserList(UserQueryDTO queryDTO) {
        return userMapper.selectList(queryDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User createUser(User user) {
        User existUser = userMapper.selectByUsername(user.getUsername());
        if (existUser != null) {
            throw new BusinessException(StatusCode.USER_ALREADY_EXISTS, "用户名已存在");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword() != null ? user.getPassword() : "123456"));
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        userMapper.insert(user);

        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            List<Long> roleIds = user.getRoles().stream().map(Role::getId).collect(Collectors.toList());
            userRoleMapper.batchInsert(user.getId(), roleIds);
        } else {
            UserRole userRole = new UserRole();
            userRole.setUserId(user.getId());
            userRole.setRoleId(2L);
            userRoleMapper.insert(userRole);
        }

        user.setPassword(null);
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User updateUser(User user) {
        User existUser = userMapper.selectById(user.getId());
        if (existUser == null) {
            throw new BusinessException(StatusCode.USER_NOT_FOUND, "用户不存在");
        }
        userMapper.update(user);
        return userMapper.selectById(user.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        userRoleMapper.deleteByUserId(id);
        userMapper.deleteById(id);
    }

    @Override
    public void updateUserStatus(Long id, Integer status) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(StatusCode.USER_NOT_FOUND, "用户不存在");
        }
        userMapper.updateStatus(id, status);
    }

    @Override
    public void resetPassword(Long id, String newPassword) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(StatusCode.USER_NOT_FOUND, "用户不存在");
        }
        userMapper.updatePassword(id, passwordEncoder.encode(newPassword != null ? newPassword : "123456"));
    }

    @Override
    public void changePassword(Long userId, PasswordUpdateDTO dto) {
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(StatusCode.USER_NOT_FOUND, "用户不存在");
        }
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException(StatusCode.PASSWORD_ERROR, "原密码错误");
        }
        userMapper.updatePassword(userId, passwordEncoder.encode(dto.getNewPassword()));
    }

    @Override
    public List<Role> getUserRoles(Long userId) {
        return roleMapper.selectByUserId(userId);
    }

    @Override
    public List<Permission> getUserPermissions(Long userId) {
        return permissionMapper.selectByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(AssignRoleDTO dto) {
        userRoleMapper.deleteByUserId(dto.getUserId());
        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
            userRoleMapper.batchInsert(dto.getUserId(), dto.getRoleIds());
        }
    }

    @Override
    public void forgotPassword(PasswordResetDTO dto) {
        User user = userMapper.selectByEmail(dto.getEmail());
        if (user == null) {
            throw new BusinessException(StatusCode.USER_NOT_FOUND, "该邮箱未注册");
        }
        String token = IdUtil.simpleUUID();
        PasswordReset passwordReset = new PasswordReset();
        passwordReset.setUserId(user.getId());
        passwordReset.setResetToken(token);
        passwordReset.setExpireTime(LocalDateTime.now().plusHours(1));
        passwordReset.setUsed(0);
        passwordResetMapper.insert(passwordReset);
        log.info("密码重置令牌已生成，用户: {}, Token: {}", user.getUsername(), token);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPasswordByToken(String token, String newPassword) {
        PasswordReset passwordReset = passwordResetMapper.selectByResetToken(token);
        if (passwordReset == null) {
            throw new BusinessException("重置链接无效");
        }
        if (passwordReset.getUsed() == 1) {
            throw new BusinessException("重置链接已使用");
        }
        if (passwordReset.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("重置链接已过期");
        }
        userMapper.updatePassword(passwordReset.getUserId(), passwordEncoder.encode(newPassword));
        passwordResetMapper.markAsUsed(passwordReset.getId());
    }

    @Override
    public void exportUsers(UserQueryDTO queryDTO, HttpServletResponse response) {
        List<User> users = userMapper.selectList(queryDTO);
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("用户列表");
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "用户名", "昵称", "邮箱", "手机号", "状态", "创建时间"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }
            for (int i = 0; i < users.size(); i++) {
                User user = users.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(user.getId() != null ? user.getId() : 0);
                row.createCell(1).setCellValue(user.getUsername() != null ? user.getUsername() : "");
                row.createCell(2).setCellValue(user.getNickname() != null ? user.getNickname() : "");
                row.createCell(3).setCellValue(user.getEmail() != null ? user.getEmail() : "");
                row.createCell(4).setCellValue(user.getPhone() != null ? user.getPhone() : "");
                row.createCell(5).setCellValue(user.getStatus() != null && user.getStatus() == 1 ? "启用" : "禁用");
                row.createCell(6).setCellValue(user.getCreateTime() != null ? user.getCreateTime().toString() : "");
            }
            String fileName = URLEncoder.encode("用户列表.xlsx", StandardCharsets.UTF_8.name());
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment;filename*=UTF-8''" + fileName);
            OutputStream out = response.getOutputStream();
            workbook.write(out);
            out.flush();
        } catch (Exception e) {
            log.error("导出用户失败", e);
            throw new BusinessException("导出失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importUsers(MultipartFile file) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                String username = getCellValue(row, 1);
                if (StrUtil.isBlank(username)) continue;
                User exist = userMapper.selectByUsername(username);
                if (exist != null) continue;
                User user = new User();
                user.setUsername(username);
                user.setNickname(getCellValue(row, 2));
                user.setEmail(getCellValue(row, 3));
                user.setPhone(getCellValue(row, 4));
                String status = getCellValue(row, 5);
                user.setStatus("禁用".equals(status) ? 0 : 1);
                user.setPassword(passwordEncoder.encode("123456"));
                userMapper.insert(user);

                UserRole userRole = new UserRole();
                userRole.setUserId(user.getId());
                userRole.setRoleId(2L);
                userRoleMapper.insert(userRole);
            }
        } catch (Exception e) {
            log.error("导入用户失败", e);
            throw new BusinessException("导入失败: " + e.getMessage());
        }
    }

    private String getCellValue(Row row, int cellNum) {
        if (row.getCell(cellNum) == null) return "";
        try {
            return row.getCell(cellNum).getStringCellValue();
        } catch (Exception e) {
            try {
                return String.valueOf((long) row.getCell(cellNum).getNumericCellValue());
            } catch (Exception ex) {
                return "";
            }
        }
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }
}
