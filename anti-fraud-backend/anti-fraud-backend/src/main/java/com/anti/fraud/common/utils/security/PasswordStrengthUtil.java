package com.anti.fraud.common.utils.security;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 密码强度校验工具类
 * 提供密码强度评估、弱密码检测等功能
 */
@Component
public class PasswordStrengthUtil {

    // 弱密码黑名单
    private static final Set<String> WEAK_PASSWORD_BLACKLIST = new HashSet<>(Arrays.asList(
            // 常见弱密码
            "password", "123456", "12345678", "qwerty", "abc123", "monkey", "1234567",
            "letmein", "trustno1", "dragon", "baseball", "iloveyou", "master", "sunshine",
            "ashley", "bailey", "shadow", "123123", "654321", "superman", "qazwsx",
            "michael", "football", "password1", "password123", "welcome", "welcome1",
            "admin", "admin123", "admin888", "root", "root123", "pass", "pass123",
            "test", "test123", "guest", "guest123", "login", "login123",
            "123456789", "1234567890", "000000", "111111", "222222", "333333",
            "444444", "555555", "666666", "777777", "888888", "999999",
            "passw0rd", "p@ssword", "p@ssw0rd", "password!", "password@",
            "qwer1234", "asdf1234", "zxcv1234", "1qaz2wsx", "1q2w3e4r",
            "0000", "1111", "2222", "3333", "4444", "5555", "6666", "7777", "8888", "9999",
            "abcd1234", "abcd123", "abc1234", "1234abcd", "qwertyuiop", "asdfghjkl",
            "zxcvbnm", "computer", "internet", "samsung", "corvette", "mercedes",
            "jessica", "matthew", "jordan", "robert", "a1b2c3", "a1b2c3d4",
            // 键盘序列
            "1q2w3e", "1q2w3e4r5t", "qwe123", "qwe1234",
            "aaa111", "qqq111", "www111",
            // 重复字符
            "aaaaaa", "bbbbbb", "cccccc", "dddddd", "eeeeee", "ffffff",
            // 中国常用弱密码
            "woaini", "woaini1314", "iloveyou", "5201314", "1314520",
            "zhongguo", "beijing", "shanghai", "guangzhou", "shenzhen",
            "wangzhan", "hacker", "hacker123", "webmaster"
    ));

    // 密码强度正则表达式
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]");

    // 最小密码长度
    private static final int MIN_PASSWORD_LENGTH = 8;
    // 最大密码长度
    private static final int MAX_PASSWORD_LENGTH = 32;

    /**
     * 密码强度枚举
     */
    public enum PasswordStrength {
        VERY_WEAK(0, "极弱", "#c53030"),
        WEAK(1, "弱", "#dd6b20"),
        FAIR(2, "一般", "#d69e2e"),
        STRONG(3, "较强", "#38a169"),
        VERY_STRONG(4, "强", "#2f855a");

        private final int score;
        private final String description;
        private final String color;

        PasswordStrength(int score, String description, String color) {
            this.score = score;
            this.description = description;
            this.color = color;
        }

        public int getScore() {
            return score;
        }

        public String getDescription() {
            return description;
        }

        public String getColor() {
            return color;
        }
    }

    /**
     * 校验结果
     */
    public static class ValidationResult {
        private final boolean valid;
        private final PasswordStrength strength;
        private final String message;
        private final boolean isWeakPassword;

        public ValidationResult(boolean valid, PasswordStrength strength, String message, boolean isWeakPassword) {
            this.valid = valid;
            this.strength = strength;
            this.message = message;
            this.isWeakPassword = isWeakPassword;
        }

        public boolean isValid() {
            return valid;
        }

        public PasswordStrength getStrength() {
            return strength;
        }

        public String getMessage() {
            return message;
        }

        public boolean isWeakPassword() {
            return isWeakPassword;
        }

        public static ValidationResult success(PasswordStrength strength) {
            return new ValidationResult(true, strength, "密码强度良好", false);
        }

        public static ValidationResult error(String message) {
            return new ValidationResult(false, PasswordStrength.VERY_WEAK, message, false);
        }

        public static ValidationResult weakPassword(String message) {
            return new ValidationResult(true, PasswordStrength.VERY_WEAK, message, true);
        }
    }

    /**
     * 检查项结果
     */
    public static class CheckResult {
        private final boolean passed;
        private final String message;

        public CheckResult(boolean passed, String message) {
            this.passed = passed;
            this.message = message;
        }

        public boolean isPassed() {
            return passed;
        }

        public String getMessage() {
            return message;
        }
    }

    /**
     * 校验密码强度
     *
     * @param password 密码
     * @return 校验结果
     */
    public ValidationResult validate(String password) {
        if (password == null || password.isEmpty()) {
            return ValidationResult.error("密码不能为空");
        }

        // 检查长度
        if (password.length() < MIN_PASSWORD_LENGTH) {
            return ValidationResult.error("密码长度至少" + MIN_PASSWORD_LENGTH + "位");
        }

        if (password.length() > MAX_PASSWORD_LENGTH) {
            return ValidationResult.error("密码长度不能超过" + MAX_PASSWORD_LENGTH + "位");
        }

        // 检查是否在黑名单中
        if (isInBlacklist(password)) {
            return ValidationResult.weakPassword("密码太常见，请使用更复杂的密码");
        }

        // 计算密码强度
        PasswordStrength strength = calculateStrength(password);

        // 如果强度太低，给出警告但不阻止
        if (strength.getScore() <= 1) {
            return ValidationResult.weakPassword("密码强度" + strength.getDescription() + "，建议使用更复杂的密码");
        }

        return ValidationResult.success(strength);
    }

    /**
     * 计算密码强度
     *
     * @param password 密码
     * @return 密码强度
     */
    public PasswordStrength calculateStrength(String password) {
        int score = 0;

        // 长度检查
        if (password.length() >= MIN_PASSWORD_LENGTH) score++;
        if (password.length() >= 12) score++;
        if (password.length() >= 16) score++;

        // 字符类型检查
        if (UPPERCASE_PATTERN.matcher(password).find()) score++;
        if (LOWERCASE_PATTERN.matcher(password).find()) score++;
        if (DIGIT_PATTERN.matcher(password).find()) score++;
        if (SPECIAL_CHAR_PATTERN.matcher(password).find()) score++;

        // 混合检查
        int typeCount = 0;
        if (UPPERCASE_PATTERN.matcher(password).find()) typeCount++;
        if (LOWERCASE_PATTERN.matcher(password).find()) typeCount++;
        if (DIGIT_PATTERN.matcher(password).find()) typeCount++;
        if (SPECIAL_CHAR_PATTERN.matcher(password).find()) typeCount++;

        if (typeCount >= 3) score++;
        if (typeCount >= 4) score++;

        // 转换为强度等级
        if (score <= 2) return PasswordStrength.VERY_WEAK;
        if (score <= 4) return PasswordStrength.WEAK;
        if (score <= 6) return PasswordStrength.FAIR;
        if (score <= 8) return PasswordStrength.STRONG;
        return PasswordStrength.VERY_STRONG;
    }

    /**
     * 检查密码是否在黑名单中
     *
     * @param password 密码
     * @return 是否在黑名单
     */
    public boolean isInBlacklist(String password) {
        if (password == null) return false;
        String lowerPassword = password.toLowerCase();
        return WEAK_PASSWORD_BLACKLIST.contains(lowerPassword);
    }

    /**
     * 检查密码是否包含用户名
     *
     * @param password 密码
     * @param username 用户名
     * @return 是否包含
     */
    public boolean containsUsername(String password, String username) {
        if (password == null || username == null) return false;
        return password.toLowerCase().contains(username.toLowerCase());
    }

    /**
     * 检查密码是否包含键盘序列
     *
     * @param password 密码
     * @return 是否包含
     */
    public boolean containsKeyboardPattern(String password) {
        if (password == null) return false;

        String lowerPassword = password.toLowerCase();
        String[] patterns = {
                "123456", "654321", "qwerty", "asdfgh", "zxcvbn",
                "qazwsx", "1qaz", "2wsx", "3edc", "4rfv", "5tgb", "6yhn", "7ujm",
                "abcdef", "abcdefg", "bcdefg"
        };

        for (String pattern : patterns) {
            if (lowerPassword.contains(pattern)) {
                return true;
            }
        }

        // 检查键盘行序列
        String keyboardRows = "qwertyuiopasdfghjklzxcvbnm";
        for (int i = 0; i <= keyboardRows.length() - 5; i++) {
            String subSequence = keyboardRows.substring(i, i + 5);
            String reverseSequence = new StringBuilder(subSequence).reverse().toString();
            if (lowerPassword.contains(subSequence) || lowerPassword.contains(reverseSequence)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 检查是否满足长度要求
     */
    public CheckResult checkLength(String password) {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            return new CheckResult(false, "密码长度至少" + MIN_PASSWORD_LENGTH + "位");
        }
        if (password.length() > MAX_PASSWORD_LENGTH) {
            return new CheckResult(false, "密码长度不能超过" + MAX_PASSWORD_LENGTH + "位");
        }
        return new CheckResult(true, "长度符合要求");
    }

    /**
     * 检查是否包含大写字母
     */
    public CheckResult checkUppercase(String password) {
        if (UPPERCASE_PATTERN.matcher(password).find()) {
            return new CheckResult(true, "包含大写字母");
        }
        return new CheckResult(false, "请包含大写字母");
    }

    /**
     * 检查是否包含小写字母
     */
    public CheckResult checkLowercase(String password) {
        if (LOWERCASE_PATTERN.matcher(password).find()) {
            return new CheckResult(true, "包含小写字母");
        }
        return new CheckResult(false, "请包含小写字母");
    }

    /**
     * 检查是否包含数字
     */
    public CheckResult checkDigit(String password) {
        if (DIGIT_PATTERN.matcher(password).find()) {
            return new CheckResult(true, "包含数字");
        }
        return new CheckResult(false, "请包含数字");
    }

    /**
     * 检查密码格式（用于注册时的实时校验）
     *
     * @param password 密码
     * @return 检查结果
     */
    public ValidationResult validateForRegistration(String password) {
        if (password == null || password.isEmpty()) {
            return ValidationResult.error("密码不能为空");
        }

        if (password.length() < MIN_PASSWORD_LENGTH) {
            return ValidationResult.error("密码长度至少" + MIN_PASSWORD_LENGTH + "位");
        }

        if (password.length() > MAX_PASSWORD_LENGTH) {
            return ValidationResult.error("密码长度不能超过" + MAX_PASSWORD_LENGTH + "位");
        }

        // 检查是否在黑名单
        if (isInBlacklist(password)) {
            return ValidationResult.error("密码太常见，请使用更复杂的密码");
        }

        // 检查基本要求
        boolean hasUppercase = UPPERCASE_PATTERN.matcher(password).find();
        boolean hasLowercase = LOWERCASE_PATTERN.matcher(password).find();
        boolean hasDigit = DIGIT_PATTERN.matcher(password).find();

        if (!hasUppercase || !hasLowercase || !hasDigit) {
            return ValidationResult.error("密码必须包含大小写字母和数字");
        }

        // 计算强度
        PasswordStrength strength = calculateStrength(password);

        // 如果强度太弱，给出警告
        if (strength.getScore() <= 2) {
            return ValidationResult.weakPassword("密码强度" + strength.getDescription() + "，建议使用更复杂的密码");
        }

        return ValidationResult.success(strength);
    }

    /**
     * 获取详细的密码检查结果
     *
     * @param password 密码
     * @param username 用户名（可选，用于检查是否包含用户名）
     * @return 检查结果列表
     */
    public java.util.List<CheckResult> getDetailedChecks(String password, String username) {
        java.util.List<CheckResult> results = new java.util.ArrayList<>();

        results.add(checkLength(password));
        results.add(checkUppercase(password));
        results.add(checkLowercase(password));
        results.add(checkDigit(password));

        if (isInBlacklist(password)) {
            results.add(new CheckResult(false, "不能在弱密码黑名单中"));
        }

        if (username != null && containsUsername(password, username)) {
            results.add(new CheckResult(false, "不能包含用户名"));
        }

        if (containsKeyboardPattern(password)) {
            results.add(new CheckResult(false, "不能包含键盘序列"));
        }

        return results;
    }
}
