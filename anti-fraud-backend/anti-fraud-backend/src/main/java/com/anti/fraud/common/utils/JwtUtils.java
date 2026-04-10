package com.anti.fraud.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 * <p>
 * 提供JSON Web Token的生成、解析和验证功能。
 * 使用HMAC-SHA算法进行签名，支持设置过期时间。
 * </p>
 *
 * @author Anti-Fraud Platform Team
 * @version 1.0
 * @since 2024-01-01
 * @see <a href="https://github.com/jwtk/jjwt">JJWT Library</a>
 */
@Component
@Slf4j
public class JwtUtils {

    /**
     * JWT签名密钥（从配置文件读取）
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * JWT过期时间（毫秒，从配置文件读取）
     */
    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * 获取签名密钥
     * <p>
     * 将配置中的密钥字符串转换为SecretKey对象。
     * </p>
     *
     * @return 签名密钥
     */
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成JWT Token
     * <p>
     * 创建一个包含用户信息的JWT令牌，包含用户ID、用户名和角色信息。
     * </p>
     *
     * @param userId   用户ID
     * @param username 用户名
     * @param role     用户角色ID
     * @return JWT令牌字符串
     */
    public String generateToken(Long userId, String username, Integer role) {
        log.debug("生成JWT Token: userId={}, username={}", userId, username);
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);
        return createToken(claims, username);
    }

    /**
     * 创建Token
     * <p>
     * 内部方法，负责构建JWT Token的详细信息。
     * </p>
     *
     * @param claims  声明信息
     * @param subject 主题（通常为用户名）
     * @return JWT令牌字符串
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expiration);
        
        String token = Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(getSecretKey())
                .compact();
                
        log.debug("Token创建成功，过期时间: {}", expirationDate);
        return token;
    }

    /**
     * 解析Token
     * <p>
     * 解析JWT令牌并返回声明信息。如果令牌无效或已过期，返回null。
     * </p>
     *
     * @param token JWT令牌字符串
     * @return 声明信息对象，解析失败返回null
     */
    public Claims parseToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            log.debug("Token解析成功: subject={}", claims.getSubject());
            return claims;
        } catch (Exception e) {
            log.warn("Token解析失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从Token获取用户名
     *
     * @param token JWT令牌字符串
     * @return 用户名，解析失败返回null
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims != null ? claims.getSubject() : null;
    }

    /**
     * 从Token获取用户ID
     *
     * @param token JWT令牌字符串
     * @return 用户ID，解析失败返回null
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims != null ? claims.get("userId", Long.class) : null;
    }

    /**
     * 从Token获取角色信息
     *
     * @param token JWT令牌字符串
     * @return 角色ID，解析失败返回null
     */
    public Integer getRoleFromToken(String token) {
        Claims claims = parseToken(token);
        return claims != null ? claims.get("role", Integer.class) : null;
    }

    /**
     * 验证Token是否有效
     * <p>
     * 验证Token是否已签名且未过期。
     * </p>
     *
     * @param token JWT令牌字符串
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            if (claims == null) {
                log.debug("Token验证失败: 解析结果为null");
                return false;
            }
            boolean isValid = !claims.getExpiration().before(new Date());
            log.debug("Token验证结果: {}, 过期时间: {}", isValid, claims.getExpiration());
            return isValid;
        } catch (Exception e) {
            log.warn("Token验证异常: {}", e.getMessage());
            return false;
        }
    }
}
