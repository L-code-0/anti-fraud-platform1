package com.anti.fraud.modules.ai.service.impl;

import com.anti.fraud.modules.ai.config.AiConfig;
import com.anti.fraud.modules.ai.service.AiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final AiConfig aiConfig;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private String defaultModel;

    @Override
    public String generateText(String prompt, String model, Double temperature, Integer maxTokens) {
        try {
            if (model == null || model.isEmpty()) {
                model = getDefaultModel();
            }

            if (model.contains("wenxin")) {
                return generateTextWithWenxin(prompt, model, temperature, maxTokens);
            } else if (model.contains("chatglm")) {
                return generateTextWithChatGLM(prompt, model, temperature, maxTokens);
            } else {
                throw new IllegalArgumentException("Unsupported model: " + model);
            }
        } catch (Exception e) {
            log.error("生成文本失败: {}", e.getMessage(), e);
            return "生成文本失败，请稍后重试";
        }
    }

    @Override
    public String generateChat(List<Map<String, String>> messages, String model, Double temperature, Integer maxTokens) {
        try {
            if (model == null || model.isEmpty()) {
                model = getDefaultModel();
            }

            if (model.contains("wenxin")) {
                return generateChatWithWenxin(messages, model, temperature, maxTokens);
            } else if (model.contains("chatglm")) {
                return generateChatWithChatGLM(messages, model, temperature, maxTokens);
            } else {
                throw new IllegalArgumentException("Unsupported model: " + model);
            }
        } catch (Exception e) {
            log.error("生成对话失败: {}", e.getMessage(), e);
            return "生成对话失败，请稍后重试";
        }
    }

    @Override
    public String answerQuestion(String question, String context, String model) {
        try {
            String prompt = "根据以下上下文，回答问题：\n" +
                    "上下文：" + context + "\n" +
                    "问题：" + question + "\n" +
                    "回答：";
            return generateText(prompt, model, 0.7, 1000);
        } catch (Exception e) {
            log.error("回答问题失败: {}", e.getMessage(), e);
            return "回答问题失败，请稍后重试";
        }
    }

    @Override
    public List<String> batchGenerateText(List<String> prompts, String model, Double temperature, Integer maxTokens) {
        List<String> results = new ArrayList<>();
        for (String prompt : prompts) {
            results.add(generateText(prompt, model, temperature, maxTokens));
        }
        return results;
    }

    @Override
    public List<String> getModels() {
        List<String> models = new ArrayList<>();
        if (aiConfig.getWenxin() != null) {
            models.add("wenxin:" + aiConfig.getWenxin().getModel());
        }
        if (aiConfig.getChatglm() != null) {
            models.add("chatglm:" + aiConfig.getChatglm().getModel());
        }
        return models;
    }

    @Override
    public boolean healthCheck(String model) {
        try {
            if (model == null || model.isEmpty()) {
                model = getDefaultModel();
            }

            String prompt = "健康检查，返回'ok'";
            String result = generateText(prompt, model, 0.0, 10);
            return result != null && result.contains("ok");
        } catch (Exception e) {
            log.error("健康检查失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void setDefaultModel(String model) {
        this.defaultModel = model;
    }

    @Override
    public String getDefaultModel() {
        if (defaultModel != null) {
            return defaultModel;
        }

        String provider = aiConfig.getDefaultProvider();
        if ("wenxin".equals(provider) && aiConfig.getWenxin() != null) {
            return "wenxin:" + aiConfig.getWenxin().getModel();
        } else if ("chatglm".equals(provider) && aiConfig.getChatglm() != null) {
            return "chatglm:" + aiConfig.getChatglm().getModel();
        } else {
            // 默认使用文心一言
            if (aiConfig.getWenxin() != null) {
                return "wenxin:" + aiConfig.getWenxin().getModel();
            } else if (aiConfig.getChatglm() != null) {
                return "chatglm:" + aiConfig.getChatglm().getModel();
            } else {
                throw new IllegalStateException("No AI model configured");
            }
        }
    }

    // 使用文心一言生成文本
    private String generateTextWithWenxin(String prompt, String model, Double temperature, Integer maxTokens) throws Exception {
        AiConfig.Wenxin wenxin = aiConfig.getWenxin();
        if (wenxin == null) {
            throw new IllegalStateException("Wenxin config not found");
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", wenxin.getModel());
        requestBody.put("prompt", prompt);
        requestBody.put("temperature", temperature != null ? temperature : wenxin.getTemperature());
        requestBody.put("max_tokens", maxTokens != null ? maxTokens : wenxin.getMaxTokens());

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Bearer " + wenxin.getApiKey());

        // 构建请求实体
        org.springframework.http.HttpEntity<Map<String, Object>> entity = new org.springframework.http.HttpEntity<>(requestBody, new org.springframework.http.HttpHeaders() {
            {
                setAll(headers);
            }
        });

        // 发送请求
        String response = restTemplate.postForObject(wenxin.getApiUrl(), entity, String.class);

        // 解析响应
        Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
        return (String) responseMap.get("result");
    }

    // 使用ChatGLM生成文本
    private String generateTextWithChatGLM(String prompt, String model, Double temperature, Integer maxTokens) throws Exception {
        AiConfig.Chatglm chatglm = aiConfig.getChatglm();
        if (chatglm == null) {
            throw new IllegalStateException("ChatGLM config not found");
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", chatglm.getModel());
        requestBody.put("prompt", prompt);
        requestBody.put("temperature", temperature != null ? temperature : chatglm.getTemperature());
        requestBody.put("max_tokens", maxTokens != null ? maxTokens : chatglm.getMaxTokens());

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Bearer " + chatglm.getApiKey());

        // 构建请求实体
        org.springframework.http.HttpEntity<Map<String, Object>> entity = new org.springframework.http.HttpEntity<>(requestBody, new org.springframework.http.HttpHeaders() {
            {
                setAll(headers);
            }
        });

        // 发送请求
        String response = restTemplate.postForObject(chatglm.getApiUrl(), entity, String.class);

        // 解析响应
        Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
        return (String) responseMap.get("result");
    }

    // 使用文心一言生成对话
    private String generateChatWithWenxin(List<Map<String, String>> messages, String model, Double temperature, Integer maxTokens) throws Exception {
        AiConfig.Wenxin wenxin = aiConfig.getWenxin();
        if (wenxin == null) {
            throw new IllegalStateException("Wenxin config not found");
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", wenxin.getModel());
        requestBody.put("messages", messages);
        requestBody.put("temperature", temperature != null ? temperature : wenxin.getTemperature());
        requestBody.put("max_tokens", maxTokens != null ? maxTokens : wenxin.getMaxTokens());

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Bearer " + wenxin.getApiKey());

        // 构建请求实体
        org.springframework.http.HttpEntity<Map<String, Object>> entity = new org.springframework.http.HttpEntity<>(requestBody, new org.springframework.http.HttpHeaders() {
            {
                setAll(headers);
            }
        });

        // 发送请求
        String response = restTemplate.postForObject(wenxin.getApiUrl() + "/chat", entity, String.class);

        // 解析响应
        Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
        return (String) responseMap.get("result");
    }

    // 使用ChatGLM生成对话
    private String generateChatWithChatGLM(List<Map<String, String>> messages, String model, Double temperature, Integer maxTokens) throws Exception {
        AiConfig.Chatglm chatglm = aiConfig.getChatglm();
        if (chatglm == null) {
            throw new IllegalStateException("ChatGLM config not found");
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", chatglm.getModel());
        requestBody.put("messages", messages);
        requestBody.put("temperature", temperature != null ? temperature : chatglm.getTemperature());
        requestBody.put("max_tokens", maxTokens != null ? maxTokens : chatglm.getMaxTokens());

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Bearer " + chatglm.getApiKey());

        // 构建请求实体
        org.springframework.http.HttpEntity<Map<String, Object>> entity = new org.springframework.http.HttpEntity<>(requestBody, new org.springframework.http.HttpHeaders() {
            {
                setAll(headers);
            }
        });

        // 发送请求
        String response = restTemplate.postForObject(chatglm.getApiUrl() + "/chat", entity, String.class);

        // 解析响应
        Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
        return (String) responseMap.get("result");
    }
}
