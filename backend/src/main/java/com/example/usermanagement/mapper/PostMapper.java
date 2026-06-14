package com.example.usermanagement.mapper;

import com.example.usermanagement.dto.PostQueryDTO;
import com.example.usermanagement.entity.Post;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PostMapper {

    Post selectById(Long id);

    Post selectByPostCode(String postCode);

    Post selectByPostName(String postName);

    Post selectByPostCodeExcludeId(@Param("postCode") String postCode, @Param("id") Long id);

    Post selectByPostNameExcludeId(@Param("postName") String postName, @Param("id") Long id);

    List<Post> selectList(PostQueryDTO queryDTO);

    Long selectCount(PostQueryDTO queryDTO);

    List<Post> selectAll();

    int insert(Post post);

    int update(Post post);

    int deleteById(Long id);

    int deleteByIds(@Param("ids") List<Long> ids);
}
