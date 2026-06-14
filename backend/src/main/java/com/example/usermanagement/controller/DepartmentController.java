package com.example.usermanagement.controller;

import com.example.usermanagement.annotation.OperationLogAnnotation;
import com.example.usermanagement.common.Result;
import com.example.usermanagement.dto.DepartmentQueryDTO;
import com.example.usermanagement.entity.Department;
import com.example.usermanagement.service.DepartmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@Api(tags = "部门管理接口")
@RestController
@RequestMapping("/api/departments")
@CrossOrigin
public class DepartmentController {

    @Resource
    private DepartmentService departmentService;

    @ApiOperation("获取部门树形列表")
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('dept:list')")
    public Result<List<Department>> getDepartmentTree(DepartmentQueryDTO queryDTO) {
        return Result.success(departmentService.getDepartmentTree(queryDTO));
    }

    @ApiOperation("获取所有部门列表（扁平）")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('dept:list')")
    public Result<List<Department>> getAllDepartments() {
        return Result.success(departmentService.getAllDepartments());
    }

    @ApiOperation("获取部门下拉选项树")
    @GetMapping("/options")
    @PreAuthorize("hasAuthority('dept:list')")
    public Result<List<Department>> getDepartmentOptions() {
        return Result.success(departmentService.getDepartmentOptions());
    }

    @ApiOperation("根据ID获取部门详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('dept:list')")
    public Result<Department> getDepartmentById(@PathVariable Long id) {
        return Result.success(departmentService.getById(id));
    }

    @ApiOperation("新增部门")
    @PostMapping
    @PreAuthorize("hasAuthority('dept:add')")
    @OperationLogAnnotation(operation = "新增部门")
    public Result<Department> createDepartment(@Valid @RequestBody Department department) {
        return Result.success("创建成功", departmentService.createDepartment(department));
    }

    @ApiOperation("编辑部门")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('dept:edit')")
    @OperationLogAnnotation(operation = "编辑部门")
    public Result<Department> updateDepartment(@PathVariable Long id, @RequestBody Department department) {
        department.setId(id);
        return Result.success("更新成功", departmentService.updateDepartment(department));
    }

    @ApiOperation("删除部门")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('dept:delete')")
    @OperationLogAnnotation(operation = "删除部门")
    public Result<Void> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return Result.success("删除成功", null);
    }

    @ApiOperation("批量更新排序")
    @PutMapping("/batch-sort")
    @PreAuthorize("hasAuthority('dept:edit')")
    @OperationLogAnnotation(operation = "批量更新部门排序")
    public Result<Void> batchUpdateSortOrder(@RequestBody List<Department> departments) {
        departmentService.batchUpdateSortOrder(departments);
        return Result.success("排序保存成功", null);
    }
}
