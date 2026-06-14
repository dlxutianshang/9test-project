package com.example.usermanagement.controller;

import com.example.usermanagement.annotation.OperationLogAnnotation;
import com.example.usermanagement.common.PageResult;
import com.example.usermanagement.common.Result;
import com.example.usermanagement.dto.PostQueryDTO;
import com.example.usermanagement.entity.Post;
import com.example.usermanagement.service.PostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@Api(tags = "岗位管理接口")
@RestController
@RequestMapping("/api/posts")
@CrossOrigin
public class PostController {

    @Resource
    private PostService postService;

    @ApiOperation("分页查询岗位列表")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('post:list')")
    public Result<PageResult<Post>> getPostPage(PostQueryDTO queryDTO) {
        return Result.success(postService.getPostPage(queryDTO));
    }

    @ApiOperation("获取所有岗位列表")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('post:list')")
    public Result<List<Post>> getAllPosts() {
        return Result.success(postService.getAllPosts());
    }

    @ApiOperation("根据ID获取岗位详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('post:list')")
    public Result<Post> getPostById(@PathVariable Long id) {
        return Result.success(postService.getById(id));
    }

    @ApiOperation("新增岗位")
    @PostMapping
    @PreAuthorize("hasAuthority('post:add')")
    @OperationLogAnnotation(operation = "新增岗位")
    public Result<Post> createPost(@Valid @RequestBody Post post) {
        return Result.success("创建成功", postService.createPost(post));
    }

    @ApiOperation("编辑岗位")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('post:edit')")
    @OperationLogAnnotation(operation = "编辑岗位")
    public Result<Post> updatePost(@PathVariable Long id, @RequestBody Post post) {
        post.setId(id);
        return Result.success("更新成功", postService.updatePost(post));
    }

    @ApiOperation("删除岗位")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('post:delete')")
    @OperationLogAnnotation(operation = "删除岗位")
    public Result<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return Result.success("删除成功", null);
    }

    @ApiOperation("批量删除岗位")
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('post:delete')")
    @OperationLogAnnotation(operation = "批量删除岗位")
    public Result<Void> deletePosts(@RequestBody List<Long> ids) {
        postService.deletePosts(ids);
        return Result.success("删除成功", null);
    }

    @ApiOperation("检查岗位是否已分配用户")
    @GetMapping("/{id}/has-users")
    @PreAuthorize("hasAuthority('post:list')")
    public Result<Boolean> checkPostHasUsers(@PathVariable Long id) {
        return Result.success(postService.checkPostHasUsers(id));
    }

    @ApiOperation("导出岗位数据")
    @GetMapping("/export")
    @PreAuthorize("hasAuthority('post:list')")
    @OperationLogAnnotation(operation = "导出岗位数据")
    public void exportPosts(PostQueryDTO queryDTO, HttpServletResponse response) throws Exception {
        byte[] data = postService.exportPosts(queryDTO);
        String fileName = "岗位管理_" + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1"));
        response.getOutputStream().write(data);
        response.getOutputStream().flush();
    }
}
