package com.anti.fraud.common.utils;

import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 敏感数据脱敏工具类
 * 提供对手机号、邮箱、身份证、银行卡、姓名等敏感信息的脱敏处理
 */
public class DesensitizationUtil {

    private DesensitizationUtil() {
        // 私有构造函数，防止实例化
    }

    /**
     * 手机号脱敏：138****8888
     */
    public static String desensitizePhone(String phone) {
        if (StrUtil.isBlank(phone)) {
            return phone;
        }
        try {
            return DesensitizedUtil.mobilePhone(phone);
        } catch (Exception e) {
            // 如果 Hutool 脱敏失败，使用自定义逻辑
            return maskMiddle(phone, 3, 7);
        }
    }

    /**
     * 邮箱脱敏：a***@example.com
     */
    public static String desensitizeEmail(String email) {
        if (StrUtil.isBlank(email)) {
            return email;
        }
        try {
            return DesensitizedUtil.email(email);
        } catch (Exception e) {
            // 如果 Hutool 脱敏失败，使用自定义逻辑
            int atIndex = email.indexOf('@');
            if (atIndex > 0) {
                String prefix = email.substring(0, atIndex);
                String suffix = email.substring(atIndex);
                if (prefix.length() <= 1) {
                    return prefix + "***" + suffix;
                }
                return prefix.charAt(0) + "***" + suffix;
            }
            return "***";
        }
    }

    /**
     * 身份证号脱敏：110101**********1234
     */
    public static String desensitizeIdCard(String idCard) {
        if (StrUtil.isBlank(idCard)) {
            return idCard;
        }
        try {
            return DesensitizedUtil.idCardNum(idCard, 4, 4);
        } catch (Exception e) {
            // 如果 Hutool 脱敏失败，使用自定义逻辑
            int length = idCard.length();
            if (length <= 4) {
                return idCard;
            }
            return maskMiddle(idCard, 0, length - 4);
        }
    }

    /**
     * 银行卡号脱敏：622202**********1234
     */
    public static String desensitizeBankCard(String bankCard) {
        if (StrUtil.isBlank(bankCard)) {
            return bankCard;
        }
        try {
            return DesensitizedUtil.bankCard(bankCard);
        } catch (Exception e) {
            // 如果 Hutool 脱敏失败，使用自定义逻辑
            int length = bankCard.length();
            if (length <= 4) {
                return bankCard;
            }
            return maskMiddle(bankCard, 0, length - 4);
        }
    }

    /**
     * 真实姓名脱敏：张*
     */
    public static String desensitizeRealName(String realName) {
        if (StrUtil.isBlank(realName)) {
            return realName;
        }
        int length = realName.length();
        if (length == 1) {
            return realName;
        } else if (length == 2) {
            return realName.charAt(0) + "*";
        } else {
            return realName.charAt(0) + "*" + realName.charAt(length - 1);
        }
    }

    /**
     * 用户名脱敏：te***t
     */
    public static String desensitizeUsername(String username) {
        if (StrUtil.isBlank(username)) {
            return username;
        }
        int length = username.length();
        if (length <= 2) {
            return username.charAt(0) + "***";
        }
        return maskMiddle(username, 1, length - 2);
    }

    /**
     * 地址脱敏：北京市***区
     */
    public static String desensitizeAddress(String address) {
        if (StrUtil.isBlank(address)) {
            return address;
        }
        int length = address.length();
        if (length <= 4) {
            return address;
        }
        return address.substring(0, 2) + "***";
    }

    /**
     * 密码脱敏：******
     */
    public static String desensitizePassword(String password) {
        if (StrUtil.isBlank(password)) {
            return password;
        }
        return "******";
    }

    /**
     * 中间部分脱敏通用方法
     */
    public static String maskMiddle(String str, int start, int end) {
        if (StrUtil.isBlank(str)) {
            return str;
        }
        int length = str.length();
        if (length <= start || length <= end) {
            return str;
        }
        int maskLength = length - start - end;
        if (maskLength <= 0) {
            return str;
        }
        String prefix = str.substring(0, start);
        String suffix = str.substring(length - end);
        return prefix + "*".repeat(Math.min(maskLength, 8)) + suffix;
    }
}
