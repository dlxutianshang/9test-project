package com.example.usermanagement.controller;

import com.example.usermanagement.annotation.OperationLogAnnotation;
import com.example.usermanagement.common.PageResult;
import com.example.usermanagement.common.Result;
import com.example.usermanagement.dto.AnnouncementQueryDTO;
import com.example.usermanagement.entity.Announcement;
import com.example.usermanagement.service.AnnouncementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@Api(tags = "通知公告管理接口")
@RestController
@RequestMapping("/api/announcements")
@CrossOrigin
public class AnnouncementController {

    @Resource
    private AnnouncementService announcementService;

    @ApiOperation("分页查询公告列表")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('announcement:list')")
    public Result<PageResult<Announcement>> getAnnouncementPage(AnnouncementQueryDTO queryDTO) {
        return Result.success(announcementService.getAnnouncementPage(queryDTO));
    }

    @ApiOperation("根据ID获取公告详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('announcement:list')")
    public Result<Announcement> getAnnouncementById(@PathVariable Long id) {
        return Result.success(announcementService.getById(id));
    }

    @ApiOperation("获取已阅读用户列表")
    @GetMapping("/{id}/read-users")
    @PreAuthorize("hasAuthority('announcement:list')")
    public Result<List<String>> getReadUsers(@PathVariable Long id) {
        return Result.success(announcementService.getReadUsers(id));
    }

    @ApiOperation("检查公告是否已被阅读")
    @GetMapping("/{id}/has-read-users")
    @PreAuthorize("hasAuthority('announcement:list')")
    public Result<Boolean> hasReadUsers(@PathVariable Long id) {
        return Result.success(announcementService.hasReadUsers(id));
    }

    @ApiOperation("新增公告")
    @PostMapping
    @PreAuthorize("hasAuthority('announcement:add')")
    @OperationLogAnnotation(operation = "新增公告")
    public Result<Announcement> createAnnouncement(@Valid @RequestBody Announcement announcement) {
        return Result.success("创建成功", announcementService.createAnnouncement(announcement));
    }

    @ApiOperation("编辑公告")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('announcement:edit')")
    @OperationLogAnnotation(operation = "编辑公告")
    public Result<Announcement> updateAnnouncement(@PathVariable Long id, @RequestBody Announcement announcement) {
        announcement.setId(id);
        return Result.success("更新成功", announcementService.updateAnnouncement(announcement));
    }

    @ApiOperation("删除公告")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('announcement:delete')")
    @OperationLogAnnotation(operation = "删除公告")
    public Result<Void> deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return Result.success("删除成功", null);
    }

    @ApiOperation("批量删除公告")
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('announcement:delete')")
    @OperationLogAnnotation(operation = "批量删除公告")
    public Result<Void> deleteAnnouncements(@RequestBody List<Long> ids) {
        announcementService.deleteAnnouncements(ids);
        return Result.success("删除成功", null);
    }
}
