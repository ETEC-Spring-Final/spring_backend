package com.example.spring_boot_project_api.config;

import java.lang.reflect.Method;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import com.example.spring_boot_project_api.enums.AuditActionEnum;
import com.example.spring_boot_project_api.service.AuditLogService;
import com.example.spring_boot_project_api.util.AuditLogContext;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLoggingAspect {

  private final AuditLogService auditLogService;
  private final AuditLogContext auditLogContext;

  @AfterReturning(pointcut = "@within(org.springframework.web.bind.annotation.RestController)", returning = "result")
  public void auditMutation(JoinPoint joinPoint, Object result) {
    Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
    AuditActionEnum action = actionFor(method);
    if (action == null || excludedController(method.getDeclaringClass())) {
      return;
    }

    try {
      String entityName = entityName(method.getDeclaringClass().getSimpleName());
      Long entityId = entityId(joinPoint.getArgs(), result);
      Object newValue = action == AuditActionEnum.DELETE ? null : result;
      Object oldValue = action == AuditActionEnum.CREATE ? null : requestArguments(joinPoint.getArgs());
      auditLogService.log(auditLogContext.currentUserId(), auditLogContext.clientIp(), action,
          entityName, entityId, oldValue, newValue, method.getName());
    } catch (Exception ex) {
      log.error("Failed to create audit entry for controller method {}", method.getName(), ex);
    }
  }

  private AuditActionEnum actionFor(Method method) {
    if (method.isAnnotationPresent(PostMapping.class)) return AuditActionEnum.CREATE;
    if (method.isAnnotationPresent(PutMapping.class) || method.isAnnotationPresent(PatchMapping.class)) {
      return AuditActionEnum.UPDATE;
    }
    if (method.isAnnotationPresent(DeleteMapping.class)) return AuditActionEnum.DELETE;
    return null;
  }

  private Long entityId(Object[] args, Object result) {
    for (Object arg : args) {
      if (arg instanceof Long id) return id;
      if (arg instanceof Integer id) return id.longValue();
    }
    if (result != null) {
      try {
        Method getter = result.getClass().getMethod("getId");
        Object id = getter.invoke(result);
        if (id instanceof Number number) return number.longValue();
      } catch (ReflectiveOperationException ignored) {
        // Some mutation endpoints intentionally return void.
      }
    }
    return null;
  }

  private boolean excludedController(Class<?> controllerClass) {
    String name = controllerClass.getSimpleName();
    return name.equals("AuditLogController")
        || name.equals("UserController")
        || name.equals("UserManagementController")
        || name.equals("UserProfileController");
  }

  private Object requestArguments(Object[] args) {
    for (Object arg : args) {
      if (arg != null && !(arg instanceof Long) && !(arg instanceof Integer)
          && !(arg instanceof String) && !(arg instanceof Boolean)
          && !(arg instanceof Enum) && !(arg instanceof Map)) {
        return arg;
      }
    }
    return null;
  }

  private String entityName(String controllerName) {
    String name = controllerName.endsWith("Controller")
        ? controllerName.substring(0, controllerName.length() - "Controller".length())
        : controllerName;
    return name;
  }
}
