package com.example.usermanagement.aspect;

import com.example.usermanagement.annotation.OperationLogAnnotation;
import com.example.usermanagement.entity.OperationLog;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.mapper.OperationLogMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
public class OperationLogAspect {

    @Resource
    private OperationLogMapper operationLogMapper;

    @Resource
    private ObjectMapper objectMapper;

    @Around("@annotation(com.example.usermanagement.annotation.OperationLogAnnotation)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OperationLogAnnotation annotation = method.getAnnotation(OperationLogAnnotation.class);

        OperationLog operationLog = new OperationLog();
        operationLog.setOperation(annotation != null ? annotation.operation() : "");
        operationLog.setMethod(joinPoint.getTarget().getClass().getName() + "." + method.getName());

        try {
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                try {
                    StringBuilder params = new StringBuilder();
                    for (int i = 0; i < args.length; i++) {
                        if (args[i] != null && !isSensitiveObject(args[i])) {
                            params.append("arg").append(i).append("=")
                                    .append(objectMapper.writeValueAsString(args[i])).append("; ");
                        }
                    }
                    operationLog.setParams(params.length() > 2000 ? params.substring(0, 2000) : params.toString());
                } catch (Exception e) {
                    log.warn("序列化参数失败", e);
                }
            }
        } catch (Exception e) {
            log.warn("处理参数异常", e);
        }

        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                operationLog.setIp(getIpAddress(request));
            }
        } catch (Exception e) {
            log.warn("获取IP异常", e);
        }

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();
                operationLog.setUserId(user.getId());
                operationLog.setUsername(user.getUsername());
            }
        } catch (Exception e) {
            log.warn("获取用户信息异常", e);
        }

        Object result;
        try {
            result = joinPoint.proceed();
            operationLog.setStatus(1);
        } catch (Throwable throwable) {
            operationLog.setStatus(0);
            operationLog.setErrorMsg(throwable.getMessage() != null && throwable.getMessage().length() > 500
                    ? throwable.getMessage().substring(0, 500) : throwable.getMessage());
            throw throwable;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            operationLog.setDuration(duration);
            try {
                operationLogMapper.insert(operationLog);
            } catch (Exception e) {
                log.error("保存操作日志失败", e);
            }
        }

        return result;
    }

    private boolean isSensitiveObject(Object obj) {
        try {
            String json = objectMapper.writeValueAsString(obj);
            return json.contains("password") || json.contains("Password");
        } catch (Exception e) {
            return false;
        }
    }

    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
