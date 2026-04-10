package com.anti.fraud.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

/**
 * 安全工具类
 * <p>
 * 提供从SecurityContext中获取当前用户信息的便捷方法。
 * 用于Controller和Service层获取当前登录用户的状态。
 * </p>
 *
 * @author Anti-Fraud Platform Team
 * @version 1.0
 * @since 2024-01-01
 * @see Authentication
 * @see SecurityContextHolder
 */
@Slf4j
public class SecurityUtils {

    /**
     * 获取当前登录用户的ID
     * <p>
     * 从SecurityContext中获取认证信息，返回当前登录用户的ID。
     * 如果用户未登录或认证信息中不包含用户ID，返回null。
     * </p>
     *
     * @return 当前用户ID，未登录返回null
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Long) {
            Long userId = (Long) authentication.getPrincipal();
            log.debug("获取当前用户ID: {}", userId);
            return userId;
        }
        log.debug("当前用户未登录或无法获取用户ID");
        return null;
    }

    /**
     * 获取当前登录用户的用户名
     * <p>
     * 从SecurityContext中获取认证信息，返回当前登录用户的用户名。
     * </p>
     *
     * @return 用户名，未登录返回null
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String username = authentication.getName();
            log.debug("获取当前用户名: {}", username);
            return username;
        }
        log.debug("当前用户未登录或无法获取用户名");
        return null;
    }

    /**
     * 检查当前用户是否具有指定角色
     *
     * @param role 角色标识（格式：ROLE_XXX）
     * @return 是否具有该角色
     */
    public static boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            log.debug("当前用户未认证");
            return false;
        }
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean hasRole = authorities.stream()
                .anyMatch(authority -> authority.getAuthority().equals(role));
        log.debug("检查用户角色: role={}, hasRole={}", role, hasRole);
        return hasRole;
    }

    /**
     * 检查当前用户是否为管理员
     * <p>
     * 包括普通管理员和超级管理员。
     * </p>
     *
     * @return 是否为管理员
     */
    public static boolean isAdmin() {
        boolean isAdmin = hasRole("ROLE_ADMIN") || hasRole("ROLE_SUPER_ADMIN");
        log.debug("检查是否为管理员: {}", isAdmin);
        return isAdmin;
    }

    /**
     * 检查当前用户是否为教师
     *
     * @return 是否为教师
     */
    public static boolean isTeacher() {
        boolean isTeacher = hasRole("ROLE_TEACHER");
        log.debug("检查是否为教师: {}", isTeacher);
        return isTeacher;
    }

    /**
     * 检查当前用户是否为专家
     *
     * @return 是否为专家
     */
    public static boolean isExpert() {
        boolean isExpert = hasRole("ROLE_EXPERT");
        log.debug("检查是否为专家: {}", isExpert);
        return isExpert;
    }

    /**
     * 检查当前用户是否为学生
     *
     * @return 是否为学生
     */
    public static boolean isStudent() {
        boolean isStudent = hasRole("ROLE_STUDENT");
        log.debug("检查是否为学生: {}", isStudent);
        return isStudent;
    }

    /**
     * 检查当前用户是否为超级管理员
     *
     * @return 是否为超级管理员
     */
    public static boolean isSuperAdmin() {
        boolean isSuperAdmin = hasRole("ROLE_SUPER_ADMIN");
        log.debug("检查是否为超级管理员: {}", isSuperAdmin);
        return isSuperAdmin;
    }

    /**
     * 检查当前用户是否具有任意一个指定角色
     *
     * @param roles 角色标识数组
     * @return 是否具有任意一个角色
     */
    public static boolean hasAnyRole(String... roles) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            log.debug("当前用户未认证");
            return false;
        }
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (String role : roles) {
            if (authorities.stream().anyMatch(authority -> authority.getAuthority().equals(role))) {
                log.debug("用户具有指定角色: {}", role);
                return true;
            }
        }
        log.debug("用户不具有任何指定角色");
        return false;
    }
}
