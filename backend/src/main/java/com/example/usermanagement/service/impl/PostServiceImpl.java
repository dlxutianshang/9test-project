package com.example.usermanagement.service.impl;

import com.example.usermanagement.common.PageResult;
import com.example.usermanagement.dto.PostQueryDTO;
import com.example.usermanagement.entity.Post;
import com.example.usermanagement.exception.BusinessException;
import com.example.usermanagement.mapper.PostMapper;
import com.example.usermanagement.mapper.UserPostMapper;
import com.example.usermanagement.service.PostService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    @Resource
    private PostMapper postMapper;

    @Resource
    private UserPostMapper userPostMapper;

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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

    @Override
    public byte[] exportPosts(PostQueryDTO queryDTO) {
        List<Post> list = postMapper.selectList(queryDTO);
        try (Workbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("岗位数据");
            String[] headers = {"岗位编号", "岗位编码", "岗位名称", "岗位排序", "状态", "创建时间"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            int rowNum = 1;
            for (Post p : list) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(p.getId() == null ? "" : p.getId().toString());
                row.createCell(1).setCellValue(p.getPostCode() == null ? "" : p.getPostCode());
                row.createCell(2).setCellValue(p.getPostName() == null ? "" : p.getPostName());
                row.createCell(3).setCellValue(p.getSortOrder() == null ? 0 : p.getSortOrder());
                row.createCell(4).setCellValue(p.getStatus() != null && p.getStatus() == 1 ? "正常" : "停用");
                row.createCell(5).setCellValue(p.getCreateTime() == null ? "" : p.getCreateTime().format(DTF));
            }
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            wb.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new BusinessException("导出失败");
        }
    }
}
