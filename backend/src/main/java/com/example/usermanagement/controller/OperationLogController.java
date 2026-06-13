package com.example.usermanagement.controller;

import com.example.usermanagement.annotation.OperationLogAnnotation;
import com.example.usermanagement.common.PageResult;
import com.example.usermanagement.common.Result;
import com.example.usermanagement.entity.OperationLog;
import com.example.usermanagement.service.OperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "操作日志接口")
@RestController
@RequestMapping("/api/logs")
@CrossOrigin
public class OperationLogController {

    @Resource
    private OperationLogService operationLogService;

    @ApiOperation("分页查询操作日志")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('log:list')")
    public Result<PageResult<OperationLog>> getLogPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            OperationLog operationLog) {
        return Result.success(operationLogService.getLogPage(pageNum, pageSize, operationLog));
    }

    @ApiOperation("根据ID获取日志详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('log:list')")
    public Result<OperationLog> getLogById(@PathVariable Long id) {
        return Result.success(operationLogService.getById(id));
    }

    @ApiOperation("删除日志")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('log:list')")
    @OperationLogAnnotation(operation = "删除操作日志")
    public Result<Void> deleteLog(@PathVariable Long id) {
        operationLogService.deleteLog(id);
        return Result.success("删除成功", null);
    }

    @ApiOperation("清理过期日志")
    @DeleteMapping("/clean")
    @PreAuthorize("hasAuthority('log:list')")
    @OperationLogAnnotation(operation = "清理过期日志")
    public Result<Void> cleanOldLogs(@RequestParam String beforeDate) {
        operationLogService.cleanOldLogs(beforeDate);
        return Result.success("清理成功", null);
    }
}
