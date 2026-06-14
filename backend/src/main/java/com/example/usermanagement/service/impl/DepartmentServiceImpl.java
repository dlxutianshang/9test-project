package com.example.usermanagement.service.impl;

import com.example.usermanagement.dto.DepartmentQueryDTO;
import com.example.usermanagement.entity.Department;
import com.example.usermanagement.exception.BusinessException;
import com.example.usermanagement.mapper.DepartmentMapper;
import com.example.usermanagement.service.DepartmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Resource
    private DepartmentMapper departmentMapper;

    @Override
    public Department getById(Long id) {
        return departmentMapper.selectById(id);
    }

    @Override
    public List<Department> getDepartmentTree(DepartmentQueryDTO queryDTO) {
        List<Department> allDepts = departmentMapper.selectAll();
        List<Department> filteredDepts = allDepts;

        boolean hasFilter = (queryDTO.getDeptName() != null && !queryDTO.getDeptName().isEmpty())
                || queryDTO.getStatus() != null;

        if (hasFilter) {
            List<Department> matchedDepts = allDepts.stream()
                    .filter(dept -> {
                        boolean nameMatch = queryDTO.getDeptName() == null || queryDTO.getDeptName().isEmpty()
                                || dept.getDeptName().contains(queryDTO.getDeptName());
                        boolean statusMatch = queryDTO.getStatus() == null
                                || dept.getStatus().equals(queryDTO.getStatus());
                        return nameMatch && statusMatch;
                    })
                    .collect(Collectors.toList());

            Set<Long> keepIds = new HashSet<>();
            for (Department matched : matchedDepts) {
                keepIds.add(matched.getId());
                if (matched.getAncestors() != null && !matched.getAncestors().isEmpty()) {
                    String[] ancestorIds = matched.getAncestors().split(",");
                    for (String aid : ancestorIds) {
                        if (!aid.isEmpty()) {
                            keepIds.add(Long.parseLong(aid));
                        }
                    }
                }
            }

            filteredDepts = allDepts.stream()
                    .filter(dept -> keepIds.contains(dept.getId()))
                    .collect(Collectors.toList());
        }

        return buildTree(filteredDepts, 0L);
    }

    @Override
    public List<Department> getAllDepartments() {
        return departmentMapper.selectAll();
    }

    @Override
    public List<Department> getDepartmentOptions() {
        List<Department> all = departmentMapper.selectAll();
        return buildTree(all, 0L);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Department createDepartment(Department department) {
        Long parentId = department.getParentId() == null ? 0L : department.getParentId();
        department.setParentId(parentId);

        Department exist = departmentMapper.selectByParentIdAndDeptName(parentId, department.getDeptName());
        if (exist != null) {
            throw new BusinessException("同层级下部门名称已存在");
        }

        if (department.getSortOrder() == null) {
            department.setSortOrder(0);
        }
        if (department.getStatus() == null) {
            department.setStatus(1);
        }

        String ancestors = "0";
        if (parentId > 0) {
            Department parent = departmentMapper.selectById(parentId);
            if (parent == null) {
                throw new BusinessException("上级部门不存在");
            }
            ancestors = parent.getAncestors() + "," + parentId;
        }
        department.setAncestors(ancestors);

        departmentMapper.insert(department);
        return department;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Department updateDepartment(Department department) {
        Department exist = departmentMapper.selectById(department.getId());
        if (exist == null) {
            throw new BusinessException("部门不存在");
        }

        if (department.getDeptName() != null && !department.getDeptName().equals(exist.getDeptName())) {
            Department duplicate = departmentMapper.selectByParentIdAndDeptName(exist.getParentId(), department.getDeptName());
            if (duplicate != null && !duplicate.getId().equals(department.getId())) {
                throw new BusinessException("同层级下部门名称已存在");
            }
        }

        departmentMapper.update(department);
        return departmentMapper.selectById(department.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDepartment(Long id) {
        Department exist = departmentMapper.selectById(id);
        if (exist == null) {
            throw new BusinessException("部门不存在");
        }

        int childCount = departmentMapper.countByParentId(id);
        if (childCount > 0) {
            throw new BusinessException("该部门下存在子部门，无法删除，请先删除子部门");
        }

        departmentMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateSortOrder(List<Department> departments) {
        if (departments == null || departments.isEmpty()) {
            return;
        }
        for (Department dept : departments) {
            if (dept.getId() != null && dept.getSortOrder() != null) {
                departmentMapper.updateSortOrder(dept.getId(), dept.getSortOrder());
            }
        }
    }

    private List<Department> buildTree(List<Department> departments, Long parentId) {
        List<Department> tree = new ArrayList<>();
        for (Department dept : departments) {
            Long pid = dept.getParentId() == null ? 0L : dept.getParentId();
            if (pid.equals(parentId)) {
                dept.setChildren(buildTree(departments, dept.getId()));
                tree.add(dept);
            }
        }
        return tree;
    }
}
