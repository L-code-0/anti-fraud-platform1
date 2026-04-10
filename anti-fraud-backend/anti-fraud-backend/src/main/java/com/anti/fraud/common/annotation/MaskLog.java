package com.anti.fraud.common.annotation;

import java.lang.annotation.*;

/**
 * 日志脱敏注解
 * <p>
 * 标记在方法参数上，自动对该参数进行脱敏处理后再记录日志。
 * 支持的脱敏类型：phone, idCard, bankCard, email, username, address, password
 * </p>
 *
 * @author Anti-Fraud Platform Team
 * @version 1.0
 * @since 2024-01-01
 * @see com.anti.fraud.common.aspect.DataMaskAspect
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MaskLog {
    
    /**
     * 脱敏类型
     * <p>
     * 可选值：
     * <ul>
     *   <li>phone - 手机号</li>
     *   <li>idCard - 身份证号</li>
     *   <li>bankCard - 银行卡号</li>
     *   <li>email - 邮箱</li>
     *   <li>username - 用户名</li>
     *   <li>address - 地址</li>
     *   <li>password - 密码</li>
     * </ul>
     * </p>
     *
     * @return 脱敏类型
     */
    String value() default "phone";
}
