package com.example.usermanagement.mapper;

import com.example.usermanagement.dto.DepartmentQueryDTO;
import com.example.usermanagement.entity.Department;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DepartmentMapper {

    Department selectById(Long id);

    List<Department> selectList(DepartmentQueryDTO queryDTO);

    List<Department> selectByParentId(Long parentId);

    List<Department> selectAll();

    Department selectByParentIdAndDeptName(@Param("parentId") Long parentId, @Param("deptName") String deptName);

    int countByParentId(Long parentId);

    int insert(Department department);

    int update(Department department);

    int updateSortOrder(@Param("id") Long id, @Param("sortOrder") Integer sortOrder);

    int deleteById(Long id);
}
