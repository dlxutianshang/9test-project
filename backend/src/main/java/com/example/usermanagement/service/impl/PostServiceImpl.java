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
        Post exist = postMapper.selectByPostCode(post.getPostCode());
        if (exist != null) {
            throw new BusinessException("岗位编码已存在");
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
        if (post.getPostCode() != null && !post.getPostCode().equals(exist.getPostCode())) {
            Post duplicate = postMapper.selectByPostCode(post.getPostCode());
            if (duplicate != null && !duplicate.getId().equals(post.getId())) {
                throw new BusinessException("岗位编码已存在");
            }
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
            throw new BusinessException("该岗位已分配用户，无法删除");
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
            throw new BusinessException("选中的岗位中存在已分配用户的岗位，无法删除");
        }
        postMapper.deleteByIds(ids);
    }
}
