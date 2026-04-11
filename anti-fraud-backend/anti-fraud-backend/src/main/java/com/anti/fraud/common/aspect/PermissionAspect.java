package com.anti.fraud.common.aspect;

import com.anti.fraud.common.annotation.RequirePermission;
import com.anti.fraud.common.annotation.RequireRole;

import com.anti.fraud.common.exception.BusinessException;
import com.anti.fraud.common.utils.SecurityUtils;
import com.anti.fraud.modules.permission.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 权限校验切面
 * 处理 @RequirePermission 和 @RequireRole 注解
 *
 * @author Anti-Fraud Platform Team
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {

    private final PermissionService permissionService;

    /**
     * 处理 @RequirePermission 注解
     */
    @Around("@annotation(com.anti.fraud.common.annotation.RequirePermission)")
    public Object aroundPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequirePermission annotation = method.getAnnotation(RequirePermission.class);

        // 检查方法上的注解，如果没有则检查类上的注解
        if (annotation == null) {
            annotation = joinPoint.getTarget().getClass().getAnnotation(RequirePermission.class);
        }

        if (annotation == null) {
            return joinPoint.proceed();
        }

        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }

        // 检查角色级别
        if (annotation.level() > 0) {
            boolean hasLevel = permissionService.checkRoleLevel(userId, annotation.level());
            if (!hasLevel) {
                log.warn("用户 {} 角色级别不足，需要级别: {}", userId, annotation.level());
                throw new BusinessException("权限不足");
            }
            // 如果只检查了级别，已满足要求
            if (annotation.value() == null || annotation.value().length == 0) {
                return joinPoint.proceed();
            }
        }

        // 检查角色编码
        if (annotation.isRole()) {
            return checkRolePermission(joinPoint, annotation, userId);
        }

        // 检查权限编码
        return checkCodePermission(joinPoint, annotation, userId);
    }

    /**
     * 处理 @RequireRole 注解
     */
    @Around("@annotation(com.anti.fraud.common.annotation.RequireRole)")
    public Object aroundRole(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequireRole annotation = method.getAnnotation(RequireRole.class);

        if (annotation == null) {
            annotation = joinPoint.getTarget().getClass().getAnnotation(RequireRole.class);
        }

        if (annotation == null) {
            return joinPoint.proceed();
        }

        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }

        return checkRolePermission(joinPoint, annotation, userId);
    }

    /**
     * 检查权限编码
     */
    private Object checkCodePermission(ProceedingJoinPoint joinPoint, RequirePermission annotation, Long userId) throws Throwable {
        String[] codes = annotation.value();
        if (codes == null || codes.length == 0) {
            return joinPoint.proceed();
        }

        boolean hasPermission;
        if (annotation.mode() == RequirePermission.Mode.ALL) {
            // 需要满足所有权限
            hasPermission = true;
            for (String code : codes) {
                if (!permissionService.hasPermission(userId, code)) {
                    hasPermission = false;
                    break;
                }
            }
        } else {
            // 满足任一权限即可
            hasPermission = false;
            for (String code : codes) {
                if (permissionService.hasPermission(userId, code)) {
                    hasPermission = true;
                    break;
                }
            }
        }

        if (!hasPermission) {
            log.warn("用户 {} 缺少必要权限: {}", userId, String.join(", ", codes));
            throw new BusinessException("权限不足");
        }

        return joinPoint.proceed();
    }

    /**
     * 检查角色权限
     */
    private Object checkRolePermission(ProceedingJoinPoint joinPoint, RequirePermission annotation, Long userId) throws Throwable {
        String[] roles = annotation.value();
        if (roles == null || roles.length == 0) {
            return joinPoint.proceed();
        }

        boolean hasRole;
        if (annotation.mode() == RequirePermission.Mode.ALL) {
            hasRole = true;
            for (String role : roles) {
                if (!permissionService.hasRole(userId, role)) {
                    hasRole = false;
                    break;
                }
            }
        } else {
            hasRole = false;
            for (String role : roles) {
                if (permissionService.hasRole(userId, role)) {
                    hasRole = true;
                    break;
                }
            }
        }

        if (!hasRole) {
            log.warn("用户 {} 缺少必要角色: {}", userId, String.join(", ", roles));
            throw new BusinessException("权限不足");
        }

        return joinPoint.proceed();
    }

    /**
     * 检查角色权限（@RequireRole注解专用）
     */
    private Object checkRolePermission(ProceedingJoinPoint joinPoint, RequireRole annotation, Long userId) throws Throwable {
        String[] roles = annotation.value();
        if (roles == null || roles.length == 0) {
            return joinPoint.proceed();
        }

        boolean hasRole;
        if (annotation.mode() == RequireRole.Mode.ALL) {
            hasRole = true;
            for (String role : roles) {
                if (!permissionService.hasRole(userId, role)) {
                    hasRole = false;
                    break;
                }
            }
        } else {
            hasRole = false;
            for (String role : roles) {
                if (permissionService.hasRole(userId, role)) {
                    hasRole = true;
                    break;
                }
            }
        }

        if (!hasRole) {
            log.warn("用户 {} 缺少必要角色: {}", userId, String.join(", ", roles));
            throw new BusinessException("权限不足");
        }

        return joinPoint.proceed();
    }
}
