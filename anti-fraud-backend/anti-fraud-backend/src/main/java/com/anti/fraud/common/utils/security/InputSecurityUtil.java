package com.anti.fraud.common.utils.security;

import com.anti.fraud.common.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * 输入安全校验工具类
 * 提供XSS防护、SQL注入防护、敏感信息检测等功能
 */
@Component
public class InputSecurityUtil {

    // XSS防护正则表达式
    private static final Pattern[] XSS_PATTERNS = {
            // Script标签
            Pattern.compile("<script[^>]*?>.*?</script>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
            // Script标签（自闭合）
            Pattern.compile("<script[^>]*?/>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
            // JavaScript事件处理器
            Pattern.compile("on\\w+\\s*=", Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
            // <iframe>
            Pattern.compile("<iframe[^>]*?>.*?</iframe>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
            // <embed>
            Pattern.compile("<embed[^>]*?>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
            // <object>
            Pattern.compile("<object[^>]*?>.*?</object>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
            // <applet>
            Pattern.compile("<applet[^>]*?>.*?</applet>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
            // JavaScript协议
            Pattern.compile("\\s*javascript\\s*:", Pattern.CASE_INSENSITIVE),
            // VBScript协议
            Pattern.compile("\\s*vbscript\\s*:", Pattern.CASE_INSENSITIVE),
            // Data URI
            Pattern.compile("\\s*data\\s*:", Pattern.CASE_INSENSITIVE),
            // 表达式
            Pattern.compile("expression\\s*\\(", Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
            // CSS表达式
            Pattern.compile("url\\s*\\(", Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
            // 危险标签
            Pattern.compile("<[^>]*?\\s+on\\w+\\s*=", Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
    };

    // SQL注入危险关键词
    private static final String[] SQL_DANGEROUS_KEYWORDS = {
            "union", "select", "insert", "update", "delete", "drop", "create", "alter",
            "exec", "execute", "script", "eval", "function", "constructor"
    };

    // SQL注入正则模式
    private static final Pattern[] SQL_INJECTION_PATTERNS = {
            // 注释符
            Pattern.compile("--"),
            Pattern.compile("/\\*"),
            Pattern.compile("\\*/"),
            // UNION注入
            Pattern.compile("(?i)\\bunion\\b.*?\\bselect\\b", Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
            // 联合查询
            Pattern.compile("(?i)\\bunion\\s+all\\b", Pattern.CASE_INSENSITIVE),
            // SELECT注入
            Pattern.compile("(?i)\\bselect\\s+.*?\\bfrom\\b", Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
            // OR 1=1
            Pattern.compile("(?i)'\\s+or\\s+'1'\\s*=\\s*'1", Pattern.CASE_INSENSITIVE),
            // OR 1=1 (无引号)
            Pattern.compile("(?i)\\bor\\s+1\\s*=\\s*1", Pattern.CASE_INSENSITIVE),
            // AND 1=1
            Pattern.compile("(?i)'\\s+and\\s+'1'\\s*=\\s*'1", Pattern.CASE_INSENSITIVE),
            // AND 1=1 (无引号)
            Pattern.compile("(?i)\\band\\s+1\\s*=\\s*1", Pattern.CASE_INSENSITIVE),
            // 字符串逃逸
            Pattern.compile("'\\s*;"),
            // DROP TABLE
            Pattern.compile("(?i)\\bdrop\\s+table\\b", Pattern.CASE_INSENSITIVE),
            // DROP DATABASE
            Pattern.compile("(?i)\\bdrop\\s+database\\b", Pattern.CASE_INSENSITIVE),
            // INSERT INTO
            Pattern.compile("(?i)\\binsert\\s+into\\b", Pattern.CASE_INSENSITIVE),
            // UPDATE SET
            Pattern.compile("(?i)\\bupdate\\s+.*?\\bset\\b", Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
            // DELETE FROM
            Pattern.compile("(?i)\\bdelete\\s+from\\b", Pattern.CASE_INSENSITIVE),
            // 十六进制编码
            Pattern.compile("0x[0-9a-f]+", Pattern.CASE_INSENSITIVE),
            // CHAR函数
            Pattern.compile("(?i)\\bchar\\s*\\(", Pattern.CASE_INSENSITIVE),
            // CONCAT函数
            Pattern.compile("(?i)\\bconcat\\s*\\(", Pattern.CASE_INSENSITIVE),
            // @@version
            Pattern.compile("@@version", Pattern.CASE_INSENSITIVE),
            // version()
            Pattern.compile("(?i)version\\s*\\(\\s*\\)", Pattern.CASE_INSENSITIVE)
    };

    /**
     * XSS攻击检测
     *
     * @param input 输入字符串
     * @return 是否包含XSS风险
     */
    public boolean containsXss(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        for (Pattern pattern : XSS_PATTERNS) {
            if (pattern.matcher(input).find()) {
                return true;
            }
        }

        return false;
    }

    /**
     * XSS净化
     *
     * @param input 输入字符串
     * @return 净化后的字符串
     */
    public String sanitizeXss(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String sanitized = input;

        // HTML实体编码
        sanitized = sanitized.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;")
                .replace("/", "&#x2F;");

        // 移除危险协议
        sanitized = sanitized.replaceAll("(?i)javascript\\s*:", "")
                .replaceAll("(?i)vbscript\\s*:", "")
                .replaceAll("(?i)data\\s*:", "");

        // 移除事件处理器
        sanitized = sanitized.replaceAll("(?i)\\s*on\\w+\\s*=", "");

        return sanitized;
    }

    /**
     * 检测SQL注入
     *
     * @param input 输入字符串
     * @return 是否包含SQL注入风险
     */
    public boolean containsSqlInjection(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        // 检查SQL注入正则模式
        for (Pattern pattern : SQL_INJECTION_PATTERNS) {
            if (pattern.matcher(input).find()) {
                return true;
            }
        }

        // 检查危险关键词
        String lowerInput = input.toLowerCase();
        for (String keyword : SQL_DANGEROUS_KEYWORDS) {
            if (lowerInput.contains(keyword)) {
                // 进一步检查是否在SQL上下文中
                if (isInSqlContext(lowerInput, keyword)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 检查关键词是否在SQL上下文中
     *
     * @param input   输入（小写）
     * @param keyword 关键词
     * @return 是否在SQL上下文
     */
    private boolean isInSqlContext(String input, String keyword) {
        // 简化实现，实际应该检查是否被引号包围
        int index = input.indexOf(keyword);
        if (index < 0) return false;

        // 检查前后是否有SQL特殊字符
        int start = Math.max(0, index - 1);
        int end = Math.min(input.length(), index + keyword.length() + 1);

        String context = input.substring(start, end);

        // 如果被单引号或双引号包围，可能是字符串的一部分
        if (context.contains("'") || context.contains("\"")) {
            // 在字符串中，需要更复杂的上下文分析
            // 这里简单返回true进行防护
            return true;
        }

        // 如果前后有SQL关键字特征
        return context.matches(".*[\\s,()].*");
    }

    /**
     * SQL注入防护（使用参数化替代）
     * 注意：这个方法只是示例，实际应该使用参数化查询
     *
     * @param input 输入字符串
     * @return 防护后的字符串
     */
    public String escapeSql(String input) {
        if (input == null) {
            return null;
        }

        return input.replace("'", "''")
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t")
                .replace("\0", "\\0")
                .replace("\u001A", "\\Z");
    }

    /**
     * 通用输入验证
     *
     * @param input        输入
     * @param fieldName    字段名称（用于错误信息）
     * @param maxLength    最大长度
     * @param allowXss     是否允许XSS内容
     * @param allowSql     是否允许SQL注入内容
     * @throws BusinessException 如果验证失败
     */
    public void validateInput(String input, String fieldName, int maxLength, boolean allowXss, boolean allowSql) {
        if (input == null) {
            return;
        }

        // 长度检查
        if (input.length() > maxLength) {
            throw new BusinessException(fieldName + "长度不能超过" + maxLength + "个字符");
        }

        // XSS检查
        if (!allowXss && containsXss(input)) {
            throw new BusinessException(fieldName + "包含非法字符");
        }

        // SQL注入检查
        if (!allowSql && containsSqlInjection(input)) {
            throw new BusinessException(fieldName + "包含非法字符");
        }
    }

    /**
     * 验证用户名
     *
     * @param username 用户名
     * @return 是否有效
     */
    public boolean isValidUsername(String username) {
        if (username == null || username.length() < 3 || username.length() > 20) {
            return false;
        }

        // 只允许字母、数字、下划线
        String usernameRegex = "^[a-zA-Z0-9_]+$";
        if (!username.matches(usernameRegex)) {
            return false;
        }

        // 检查SQL注入
        if (containsSqlInjection(username)) {
            return false;
        }

        return true;
    }

    /**
     * 验证邮箱
     *
     * @param email 邮箱
     * @return 是否有效
     */
    public boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }

        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }

    /**
     * 验证手机号
     *
     * @param phone 手机号
     * @return 是否有效
     */
    public boolean isValidPhone(String phone) {
        if (phone == null) {
            return false;
        }

        String phoneRegex = "^1[3-9]\\d{9}$";
        return phone.matches(phoneRegex);
    }

    /**
     * 脱敏处理
     *
     * @param input    输入
     * @param keepLeft 左侧保留字符数
     * @param keepRight 右侧保留字符数
     * @param maskChar 掩码字符
     * @return 脱敏后的字符串
     */
    public String mask(String input, int keepLeft, int keepRight, char maskChar) {
        if (input == null || input.length() <= keepLeft + keepRight) {
            return input;
        }

        int maskLength = input.length() - keepLeft - keepRight;
        StringBuilder masked = new StringBuilder();

        masked.append(input.substring(0, keepLeft));

        for (int i = 0; i < maskLength; i++) {
            masked.append(maskChar);
        }

        masked.append(input.substring(input.length() - keepRight));

        return masked.toString();
    }

    /**
     * 脱敏手机号
     *
     * @param phone 手机号
     * @return 脱敏后的手机号
     */
    public String maskPhone(String phone) {
        return mask(phone, 3, 4, '*');
    }

    /**
     * 脱敏邮箱
     *
     * @param email 邮箱
     * @return 脱敏后的邮箱
     */
    public String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }

        int atIndex = email.indexOf("@");
        String prefix = email.substring(0, atIndex);
        String suffix = email.substring(atIndex);

        if (prefix.length() <= 2) {
            return prefix.charAt(0) + "***" + suffix;
        }

        return prefix.charAt(0) + "***" + prefix.charAt(prefix.length() - 1) + suffix;
    }

    /**
     * 脱敏身份证号
     *
     * @param idCard 身份证号
     * @return 脱敏后的身份证号
     */
    public String maskIdCard(String idCard) {
        return mask(idCard, 6, 4, '*');
    }

    /**
     * 脱敏银行卡号
     *
     * @param bankCard 银行卡号
     * @return 脱敏后的银行卡号
     */
    public String maskBankCard(String bankCard) {
        return mask(bankCard, 4, 4, '*');
    }

    /**
     * 清理文件名（去除危险字符）
     *
     * @param filename 文件名
     * @return 清理后的文件名
     */
    public String sanitizeFilename(String filename) {
        if (filename == null) {
            return null;
        }

        // 移除路径分隔符
        String sanitized = filename.replaceAll("[/\\\\]", "");

        // 移除危险字符
        sanitized = sanitized.replaceAll("[<>:\"|?*]", "");

        // 限制长度
        if (sanitized.length() > 255) {
            int dotIndex = sanitized.lastIndexOf('.');
            if (dotIndex > 0) {
                String name = sanitized.substring(0, dotIndex);
                String ext = sanitized.substring(dotIndex);
                int maxNameLength = 255 - ext.length();
                sanitized = name.substring(0, maxNameLength) + ext;
            } else {
                sanitized = sanitized.substring(0, 255);
            }
        }

        return sanitized;
    }
}
