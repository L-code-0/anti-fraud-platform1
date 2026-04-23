package com.anti.fraud.modules.user.service.impl;

import com.anti.fraud.common.config.ThirdPartyLoginConfig;
import com.anti.fraud.modules.user.service.ThirdPartyLoginService;
import com.anti.fraud.modules.user.vo.LoginVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 第三方登录服务实现
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ThirdPartyLoginServiceImpl implements ThirdPartyLoginService {

    private final ThirdPartyLoginConfig thirdPartyLoginConfig;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public LoginVO wechatLogin(String code) {
        try {
            log.info("Wechat login with code: {}", code);

            // 1. 获取微信 access_token
            String accessTokenUrl = String.format(
                    "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code",
                    thirdPartyLoginConfig.getWechat().getAppId(),
                    thirdPartyLoginConfig.getWechat().getAppSecret(),
                    code
            );

            ResponseEntity<String> accessTokenResponse = restTemplate.exchange(
                    accessTokenUrl,
                    HttpMethod.GET,
                    null,
                    String.class
            );

            JsonNode accessTokenNode = objectMapper.readTree(accessTokenResponse.getBody());
            if (accessTokenNode.has("errcode")) {
                int errorCode = accessTokenNode.get("errcode").asInt();
                String errorMsg = accessTokenNode.get("errmsg").asText();
                log.error("Wechat access token error: code={}, msg={}", errorCode, errorMsg);
                throw new RuntimeException("微信授权失败: " + errorMsg);
            }

            String accessToken = accessTokenNode.get("access_token").asText();
            String openId = accessTokenNode.get("openid").asText();

            // 2. 获取微信用户信息
            String userInfoUrl = String.format(
                    "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s",
                    accessToken,
                    openId
            );

            ResponseEntity<String> userInfoResponse = restTemplate.exchange(
                    userInfoUrl,
                    HttpMethod.GET,
                    null,
                    String.class
            );

            JsonNode userInfoNode = objectMapper.readTree(userInfoResponse.getBody());
            if (userInfoNode.has("errcode")) {
                int errorCode = userInfoNode.get("errcode").asInt();
                String errorMsg = userInfoNode.get("errmsg").asText();
                log.error("Wechat user info error: code={}, msg={}", errorCode, errorMsg);
                throw new RuntimeException("获取微信用户信息失败: " + errorMsg);
            }

            // 3. 构建登录信息
            LoginVO loginVO = new LoginVO();
            loginVO.setUserId(generateUserId(openId));
            loginVO.setUsername("wechat_" + openId.substring(0, 8));
            loginVO.setRealName(userInfoNode.get("nickname").asText());
            loginVO.setAvatar(userInfoNode.get("headimgurl").asText());
            loginVO.setToken(generateToken(openId, "wechat"));
            loginVO.setRefreshToken(generateRefreshToken(openId, "wechat"));

            log.info("Wechat login successful for user: {}", loginVO.getUsername());
            return loginVO;
        } catch (Exception e) {
            log.error("Wechat login failed: {}", e.getMessage(), e);
            throw new RuntimeException("微信登录失败");
        }
    }

    @Override
    public LoginVO dingTalkLogin(String code) {
        try {
            log.info("DingTalk login with code: {}", code);

            // 1. 获取钉钉 access_token
            String accessTokenUrl = String.format(
                    "https://oapi.dingtalk.com/sns/gettoken?appid=%s&appsecret=%s",
                    thirdPartyLoginConfig.getDingtalk().getAppId(),
                    thirdPartyLoginConfig.getDingtalk().getAppSecret()
            );

            ResponseEntity<String> accessTokenResponse = restTemplate.exchange(
                    accessTokenUrl,
                    HttpMethod.GET,
                    null,
                    String.class
            );

            JsonNode accessTokenNode = objectMapper.readTree(accessTokenResponse.getBody());
            if (accessTokenNode.get("errcode").asInt() != 0) {
                int errorCode = accessTokenNode.get("errcode").asInt();
                String errorMsg = accessTokenNode.get("errmsg").asText();
                log.error("DingTalk access token error: code={}, msg={}", errorCode, errorMsg);
                throw new RuntimeException("钉钉授权失败: " + errorMsg);
            }

            String accessToken = accessTokenNode.get("access_token").asText();

            // 2. 获取钉钉用户信息
            String userInfoUrl = "https://oapi.dingtalk.com/sns/getuserinfo_bycode";
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("tmp_auth_code", code);

            ResponseEntity<String> userInfoResponse = restTemplate.postForEntity(
                    userInfoUrl,
                    requestBody,
                    String.class
            );

            JsonNode userInfoNode = objectMapper.readTree(userInfoResponse.getBody());
            if (userInfoNode.get("errcode").asInt() != 0) {
                int errorCode = userInfoNode.get("errcode").asInt();
                String errorMsg = userInfoNode.get("errmsg").asText();
                log.error("DingTalk user info error: code={}, msg={}", errorCode, errorMsg);
                throw new RuntimeException("获取钉钉用户信息失败: " + errorMsg);
            }

            JsonNode userInfo = userInfoNode.get("user_info");
            String openId = userInfo.get("openid").asText();

            // 3. 构建登录信息
            LoginVO loginVO = new LoginVO();
            loginVO.setUserId(generateUserId(openId));
            loginVO.setUsername("dingtalk_" + openId.substring(0, 8));
            loginVO.setRealName(userInfo.get("nick").asText());
            loginVO.setAvatar(userInfo.get("avatar_url").asText());
            loginVO.setToken(generateToken(openId, "dingtalk"));
            loginVO.setRefreshToken(generateRefreshToken(openId, "dingtalk"));

            log.info("DingTalk login successful for user: {}", loginVO.getUsername());
            return loginVO;
        } catch (Exception e) {
            log.error("DingTalk login failed: {}", e.getMessage(), e);
            throw new RuntimeException("钉钉登录失败");
        }
    }

    @Override
    public Map<String, String> getWechatQrCode() {
        try {
            // 生成微信登录二维码
            String state = UUID.randomUUID().toString();
            String qrCodeUrl = String.format(
                    "https://open.weixin.qq.com/connect/qrconnect?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s#wechat_redirect",
                    thirdPartyLoginConfig.getWechat().getAppId(),
                    thirdPartyLoginConfig.getWechat().getRedirectUri(),
                    thirdPartyLoginConfig.getWechat().getScope(),
                    state
            );

            Map<String, String> result = new HashMap<>();
            result.put("qrCodeUrl", qrCodeUrl);
            result.put("state", state);
            result.put("expireTime", "60"); // 60秒过期

            log.info("Generated Wechat QR code");
            return result;
        } catch (Exception e) {
            log.error("Failed to generate Wechat QR code: {}", e.getMessage(), e);
            throw new RuntimeException("生成微信二维码失败");
        }
    }

    @Override
    public Map<String, String> getDingTalkQrCode() {
        try {
            // 生成钉钉登录二维码
            String state = UUID.randomUUID().toString();
            String qrCodeUrl = String.format(
                    "https://login.dingtalk.com/oauth2/auth?appid=%s&response_type=code&scope=%s&redirect_uri=%s&state=%s",
                    thirdPartyLoginConfig.getDingtalk().getAppId(),
                    thirdPartyLoginConfig.getDingtalk().getScope(),
                    thirdPartyLoginConfig.getDingtalk().getRedirectUri(),
                    state
            );

            Map<String, String> result = new HashMap<>();
            result.put("qrCodeUrl", qrCodeUrl);
            result.put("state", state);
            result.put("expireTime", "60"); // 60秒过期

            log.info("Generated DingTalk QR code");
            return result;
        } catch (Exception e) {
            log.error("Failed to generate DingTalk QR code: {}", e.getMessage(), e);
            throw new RuntimeException("生成钉钉二维码失败");
        }
    }

    @Override
    public boolean bindThirdPartyAccount(Long userId, String platform, String openId) {
        try {
            // 绑定第三方账号逻辑
            log.info("Bind third party account: userId={}, platform={}, openId={}", userId, platform, openId);

            // TODO: 实现真实的绑定逻辑，例如存储到数据库

            log.info("Third party account bound successfully");
            return true;
        } catch (Exception e) {
            log.error("Failed to bind third party account: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean unbindThirdPartyAccount(Long userId, String platform) {
        try {
            // 解绑第三方账号逻辑
            log.info("Unbind third party account: userId={}, platform={}", userId, platform);

            // TODO: 实现真实的解绑逻辑，例如从数据库中删除绑定关系

            log.info("Third party account unbound successfully");
            return true;
        } catch (Exception e) {
            log.error("Failed to unbind third party account: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Map<String, String> getBoundThirdPartyAccounts(Long userId) {
        try {
            // 获取用户绑定的第三方账号
            log.info("Get bound third party accounts for userId: {}", userId);

            // TODO: 实现真实的查询逻辑，例如从数据库中查询绑定关系

            // 模拟返回绑定的账号
            Map<String, String> accounts = new HashMap<>();
            accounts.put("wechat", "wechat_openid_" + userId);
            accounts.put("dingtalk", "dingtalk_openid_" + userId);

            log.info("Retrieved bound third party accounts");
            return accounts;
        } catch (Exception e) {
            log.error("Failed to get bound third party accounts: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    // 生成用户ID（实际项目中应该使用数据库自增ID）
    private Long generateUserId(String openId) {
        return (long) (Math.abs(openId.hashCode()) % 1000000);
    }

    // 生成token（实际项目中应该使用JWT）
    private String generateToken(String openId, String platform) {
        return platform + "_token_" + System.currentTimeMillis() + "_" + UUID.randomUUID();
    }

    // 生成refresh token（实际项目中应该使用JWT）
    private String generateRefreshToken(String openId, String platform) {
        return platform + "_refresh_token_" + System.currentTimeMillis() + "_" + UUID.randomUUID();
    }
}
