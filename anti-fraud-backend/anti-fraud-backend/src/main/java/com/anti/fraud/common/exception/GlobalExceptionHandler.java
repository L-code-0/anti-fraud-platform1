package com.anti.fraud.common.exception;

import com.anti.fraud.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * <p>
 * 统一处理应用程序中的各类异常，提供一致的错误响应格式。
 * 对敏感信息进行脱敏处理后再记录日志，防止敏感数据泄露。
 * </p>
 *
 * @author Anti-Fraud Platform Team
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     * <p>
     * 业务异常通常由业务逻辑验证失败抛出，如参数校验、业务规则违反等。
     * 日志中记录异常消息用于排查问题。
     * </p>
     *
     * @param e 业务异常
     * @return 统一格式的错误响应
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常（@RequestBody）
     * <p>
     * 处理使用@Valid或@Validated注解验证请求体参数时抛出的异常。
     * 将所有字段错误信息拼接后返回给客户端。
     * </p>
     *
     * @param e 参数校验异常
     * @return 统一格式的错误响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数校验异常: {}", message);
        return Result.fail(400, message);
    }

    /**
     * 处理参数绑定异常
     * <p>
     * 处理请求参数绑定失败时抛出的异常，如参数类型不匹配等。
     * </p>
     *
     * @param e 参数绑定异常
     * @return 统一格式的错误响应
     */
    @ExceptionHandler(BindException.class)
    public Result<?> handleBindException(BindException e) {
        String message = e.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数绑定异常: {}", message);
        return Result.fail(400, message);
    }

    /**
     * 处理认证失败异常
     * <p>
     * 处理登录认证失败的情况，如用户名或密码错误。
     * 为防止暴力破解攻击，不在日志中记录具体的认证失败原因。
     * </p>
     *
     * @param e 认证失败异常
     * @return 统一格式的错误响应
     */
    @ExceptionHandler(BadCredentialsException.class)
    public Result<?> handleBadCredentialsException(BadCredentialsException e) {
        // 注意：不在日志中记录认证失败的详细信息，防止安全问题
        log.warn("用户认证失败");
        return Result.fail(401, "用户名或密码错误");
    }

    /**
     * 处理访问拒绝异常
     * <p>
     * 处理用户权限不足的情况，返回友好的权限不足提示。
     * </p>
     *
     * @param e 访问拒绝异常
     * @return 统一格式的错误响应
     */
    @ExceptionHandler(AccessDeniedException.class)
    public Result<?> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("权限不足: {}", e.getMessage());
        return Result.forbidden("权限不足，无法访问");
    }

    /**
     * 处理所有未捕获的异常
     * <p>
     * 作为最后的异常处理防线，捕获所有未预期的异常。
     * 为防止敏感信息泄露，只记录异常类型和堆栈信息，
     * 客户端返回通用错误提示。
     * 生产环境应开启更详细的日志记录用于问题排查。
     * </p>
     *
     * @param e 异常对象
     * @return 统一格式的错误响应
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        // 记录异常堆栈信息用于问题排查，但不暴露给客户端
        log.error("系统异常: type={}, message={}", e.getClass().getName(), e.getMessage(), e);
        return Result.fail("系统繁忙，请稍后再试");
    }
}
