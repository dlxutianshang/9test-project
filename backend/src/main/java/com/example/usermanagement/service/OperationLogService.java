package com.example.usermanagement.service;

import com.example.usermanagement.common.PageResult;
import com.example.usermanagement.entity.OperationLog;

public interface OperationLogService {

    PageResult<OperationLog> getLogPage(Integer pageNum, Integer pageSize, OperationLog operationLog);

    OperationLog getById(Long id);

    void deleteLog(Long id);

    void cleanOldLogs(String beforeDate);
}
