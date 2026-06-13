package com.example.usermanagement.mapper;

import com.example.usermanagement.entity.PasswordReset;

public interface PasswordResetMapper {

    PasswordReset selectById(Long id);

    PasswordReset selectByResetToken(String resetToken);

    int insert(PasswordReset passwordReset);

    int update(PasswordReset passwordReset);

    int markAsUsed(Long id);

    int deleteByUserId(Long userId);
}
