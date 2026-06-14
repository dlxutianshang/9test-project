package com.example.usermanagement.service;

import com.example.usermanagement.common.PageResult;
import com.example.usermanagement.dto.PostQueryDTO;
import com.example.usermanagement.entity.Post;

import java.util.List;

public interface PostService {

    Post getById(Long id);

    PageResult<Post> getPostPage(PostQueryDTO queryDTO);

    List<Post> getAllPosts();

    Post createPost(Post post);

    Post updatePost(Post post);

    void deletePost(Long id);

    void deletePosts(List<Long> ids);

    boolean checkPostHasUsers(Long id);

    byte[] exportPosts(PostQueryDTO queryDTO);
}
