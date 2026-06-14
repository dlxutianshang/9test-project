package com.example.usermanagement.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserPostMapper {

    int countByPostId(Long postId);

    int countByPostIds(@Param("postIds") List<Long> postIds);

    int deleteByPostId(Long postId);

    int deleteByPostIds(@Param("postIds") List<Long> postIds);

    int batchInsert(@Param("userId") Long userId, @Param("postIds") List<Long> postIds);

    List<Long> selectPostIdsByUserId(Long userId);
}
