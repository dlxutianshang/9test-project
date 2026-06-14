package com.example.usermanagement.service.impl;

import com.example.usermanagement.common.PageResult;
import com.example.usermanagement.dto.PostQueryDTO;
import com.example.usermanagement.entity.Post;
import com.example.usermanagement.exception.BusinessException;
import com.example.usermanagement.mapper.PostMapper;
import com.example.usermanagement.mapper.UserPostMapper;
import com.example.usermanagement.service.PostService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    @Resource
    private PostMapper postMapper;

    @Resource
    private UserPostMapper userPostMapper;

    @Override
    public Post getById(Long id) {
        return postMapper.selectById(id);
    }

    @Override
    public PageResult<Post> getPostPage(PostQueryDTO queryDTO) {
        queryDTO.calcOffset();
        Long total = postMapper.selectCount(queryDTO);
        List<Post> records = postMapper.selectList(queryDTO);
        return new PageResult<>(total, records, queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public List<Post> getAllPosts() {
        return postMapper.selectAll();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Post createPost(Post post) {
        if (post.getPostCode() == null || post.getPostCode().trim().isEmpty()) {
            throw new BusinessException("岗位编码不能为空");
        }
        if (post.getPostName() == null || post.getPostName().trim().isEmpty()) {
            throw new BusinessException("岗位名称不能为空");
        }
        if (post.getSortOrder() != null && post.getSortOrder() < 0) {
            throw new BusinessException("岗位排序必须为非负整数");
        }
        Post existCode = postMapper.selectByPostCode(post.getPostCode());
        if (existCode != null) {
            throw new BusinessException("岗位编码已存在");
        }
        Post existName = postMapper.selectByPostName(post.getPostName());
        if (existName != null) {
            throw new BusinessException("岗位名称已存在");
        }
        if (post.getSortOrder() == null) {
            post.setSortOrder(0);
        }
        if (post.getStatus() == null) {
            post.setStatus(1);
        }
        postMapper.insert(post);
        return post;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Post updatePost(Post post) {
        Post exist = postMapper.selectById(post.getId());
        if (exist == null) {
            throw new BusinessException("岗位不存在");
        }
        if (post.getPostCode() == null || post.getPostCode().trim().isEmpty()) {
            throw new BusinessException("岗位编码不能为空");
        }
        if (post.getPostName() == null || post.getPostName().trim().isEmpty()) {
            throw new BusinessException("岗位名称不能为空");
        }
        if (post.getSortOrder() != null && post.getSortOrder() < 0) {
            throw new BusinessException("岗位排序必须为非负整数");
        }
        Post duplicateCode = postMapper.selectByPostCodeExcludeId(post.getPostCode(), post.getId());
        if (duplicateCode != null) {
            throw new BusinessException("岗位编码已存在");
        }
        Post duplicateName = postMapper.selectByPostNameExcludeId(post.getPostName(), post.getId());
        if (duplicateName != null) {
            throw new BusinessException("岗位名称已存在");
        }
        postMapper.update(post);
        return postMapper.selectById(post.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePost(Long id) {
        Post exist = postMapper.selectById(id);
        if (exist == null) {
            throw new BusinessException("岗位不存在");
        }
        int userCount = userPostMapper.countByPostId(id);
        if (userCount > 0) {
            throw new BusinessException("该岗位已分配用户，无法删除，请先解除用户分配");
        }
        postMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePosts(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        int userCount = userPostMapper.countByPostIds(ids);
        if (userCount > 0) {
            throw new BusinessException("该岗位已分配用户，无法删除，请先解除用户分配");
        }
        postMapper.deleteByIds(ids);
    }

    @Override
    public boolean checkPostHasUsers(Long id) {
        return userPostMapper.countByPostId(id) > 0;
    }
}
