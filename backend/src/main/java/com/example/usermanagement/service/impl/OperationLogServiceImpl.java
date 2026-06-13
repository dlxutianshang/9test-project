package com.example.usermanagement.service.impl;

import com.example.usermanagement.common.PageResult;
import com.example.usermanagement.entity.OperationLog;
import com.example.usermanagement.mapper.OperationLogMapper;
import com.example.usermanagement.service.OperationLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class OperationLogServiceImpl implements OperationLogService {

    @Resource
    private OperationLogMapper operationLogMapper;

    @Override
    public PageResult<OperationLog> getLogPage(Integer pageNum, Integer pageSize, OperationLog operationLog) {
        Long total = operationLogMapper.selectCount(operationLog);
        List<OperationLog> records = operationLogMapper.selectList(operationLog);
        return new PageResult<>(total, records, pageNum, pageSize);
    }

    @Override
    public OperationLog getById(Long id) {
        return operationLogMapper.selectById(id);
    }

    @Override
    public void deleteLog(Long id) {
        operationLogMapper.deleteById(id);
    }

    @Override
    public void cleanOldLogs(String beforeDate) {
        operationLogMapper.deleteByCreateTime(beforeDate);
    }
}
