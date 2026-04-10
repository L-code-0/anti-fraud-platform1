package com.anti.fraud.common.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * 数据脱敏工具类
 * <p>
 * 提供各类敏感数据的脱敏处理方法，用于日志记录和数据展示场景。
 * 防止敏感信息（如手机号、身份证号、银行卡号等）在日志中泄露。
 * </p>
 *
 * @author Anti-Fraud Platform Team
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
public class DataMaskUtils {

    /**
     * 手机号脱敏
     * <p>
     * 显示前3位和后4位，中间用*代替。
     * 示例：13812345678 -> 138****5678
     * </p>
     *
     * @param phone 手机号
     * @return 脱敏后的手机号
     */
    public static String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        int length = phone.length();
        return phone.substring(0, 3) + "****" + phone.substring(length - 4);
    }

    /**
     * 身份证号脱敏
     * <p>
     * 显示前6位和后4位，中间用*代替。
     * 示例：110101199001011234 -> 110101********1234
     * </p>
     *
     * @param idCard 身份证号
     * @return 脱敏后的身份证号
     */
    public static String maskIdCard(String idCard) {
        if (idCard == null || idCard.length() < 10) {
            return idCard;
        }
        int length = idCard.length();
        return idCard.substring(0, 6) + "********" + idCard.substring(length - 4);
    }

    /**
     * 银行卡号脱敏
     * <p>
     * 显示前6位和后4位，中间用*代替。
     * 示例：6222021234567890123 -> 622202**********0123
     * </p>
     *
     * @param bankCard 银行卡号
     * @return 脱敏后的银行卡号
     */
    public static String maskBankCard(String bankCard) {
        if (bankCard == null || bankCard.length() < 10) {
            return bankCard;
        }
        int length = bankCard.length();
        int maskLength = length - 10;
        String mask = "*".repeat(maskLength);
        return bankCard.substring(0, 6) + mask + bankCard.substring(length - 4);
    }

    /**
     * 邮箱脱敏
     * <p>
     * 显示邮箱前缀的前2位和后缀的后2位。
     * 示例：zhangsan@example.com -> zh****@example.com
     * </p>
     *
     * @param email 邮箱地址
     * @return 脱敏后的邮箱
     */
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        String[] parts = email.split("@");
        if (parts[0].length() <= 2) {
            return email;
        }
        String prefix = parts[0].substring(0, 2) + "****";
        return prefix + "@" + parts[1];
    }

    /**
     * 用户名脱敏
     * <p>
     * 显示前1位和后1位，中间用*代替。
     * 示例：张三 -> 张*；张三丰 -> 张*丰
     * </p>
     *
     * @param username 用户名
     * @return 脱敏后的用户名
     */
    public static String maskUsername(String username) {
        if (username == null || username.length() <= 1) {
            return username;
        }
        if (username.length() == 2) {
            return username.charAt(0) + "*";
        }
        return username.charAt(0) + "*" + username.charAt(username.length() - 1);
    }

    /**
     * 地址脱敏
     * <p>
     * 保留地址的前6个字符，其余用*代替。
     * 示例：北京市朝阳区某某街道123号 -> 北京市朝阳区****
     * </p>
     *
     * @param address 地址
     * @return 脱敏后的地址
     */
    public static String maskAddress(String address) {
        if (address == null || address.length() <= 6) {
            return address;
        }
        return address.substring(0, 6) + "****";
    }

    /**
     * 密码脱敏
     * <p>
     * 直接返回固定长度的*字符串。
     * 示例：anypassword -> ********
     * </p>
     *
     * @param password 密码
     * @return 脱敏后的密码
     */
    public static String maskPassword(String password) {
        if (password == null) {
            return null;
        }
        return "*".repeat(Math.min(password.length(), 8));
    }

    /**
     * 通用脱敏方法
     * <p>
     * 根据数据类型自动选择合适的脱敏方式。
     * </p>
     *
     * @param value   需要脱敏的值
     * @param dataType 数据类型：phone, idCard, bankCard, email, username, address, password
     * @return 脱敏后的值
     */
    public static String mask(String value, String dataType) {
        if (value == null) {
            return null;
        }
        
        switch (dataType.toLowerCase()) {
            case "phone":
            case "mobile":
                return maskPhone(value);
            case "idcard":
            case "id_card":
                return maskIdCard(value);
            case "bankcard":
            case "bank_card":
                return maskBankCard(value);
            case "email":
                return maskEmail(value);
            case "username":
            case "name":
                return maskUsername(value);
            case "address":
                return maskAddress(value);
            case "password":
            case "pwd":
                return maskPassword(value);
            default:
                log.warn("未知的脱敏类型: {}", dataType);
                return value;
        }
    }
}
