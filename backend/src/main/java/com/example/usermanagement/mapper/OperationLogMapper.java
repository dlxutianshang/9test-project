package com.example.usermanagement.mapper;

import com.example.usermanagement.entity.OperationLog;

import java.util.List;

public interface OperationLogMapper {

    OperationLog selectById(Long id);

    List<OperationLog> selectList(OperationLog operationLog);

    Long selectCount(OperationLog operationLog);

    int insert(OperationLog operationLog);

    int deleteById(Long id);

    int deleteByCreateTime(String beforeDate);
}
