package com.example.usermanagement.mapper;

import com.example.usermanagement.dto.UserQueryDTO;
import com.example.usermanagement.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {

    User selectById(Long id);

    User selectByUsername(String username);

    User selectByEmail(String email);

    List<User> selectList(UserQueryDTO queryDTO);

    Long selectCount(UserQueryDTO queryDTO);

    int insert(User user);

    int update(User user);

    int deleteById(Long id);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    int updatePassword(@Param("id") Long id, @Param("password") String password);
}
