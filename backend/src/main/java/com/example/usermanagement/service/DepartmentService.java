package com.example.usermanagement.service;

import com.example.usermanagement.dto.DepartmentQueryDTO;
import com.example.usermanagement.entity.Department;

import java.util.List;

public interface DepartmentService {

    Department getById(Long id);

    List<Department> getDepartmentTree(DepartmentQueryDTO queryDTO);

    List<Department> getAllDepartments();

    List<Department> getDepartmentOptions();

    Department createDepartment(Department department);

    Department updateDepartment(Department department);

    void deleteDepartment(Long id);

    void batchUpdateSortOrder(List<Department> departments);
}
