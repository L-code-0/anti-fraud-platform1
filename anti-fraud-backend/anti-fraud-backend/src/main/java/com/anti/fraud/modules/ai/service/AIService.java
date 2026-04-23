package com.anti.fraud.modules.ai.service;

import java.util.List;
import java.util.Map;

/**
 * AI服务接口
 */
public interface AiService {

    /**
     * 文本生成
     * @param prompt 提示词
     * @param model 模型名称
     * @param temperature 温度参数
     * @param maxTokens 最大token数
     * @return 生成的文本
     */
    String generateText(String prompt, String model, Double temperature, Integer maxTokens);

    /**
     * 对话生成
     * @param messages 对话历史
     * @param model 模型名称
     * @param temperature 温度参数
     * @param maxTokens 最大token数
     * @return 生成的回复
     */
    String generateChat(List<Map<String, String>> messages, String model, Double temperature, Integer maxTokens);

    /**
     * 智能问答
     * @param question 问题
     * @param context 上下文
     * @param model 模型名称
     * @return 回答
     */
    String answerQuestion(String question, String context, String model);

    /**
     * 批量生成文本
     * @param prompts 提示词列表
     * @param model 模型名称
     * @param temperature 温度参数
     * @param maxTokens 最大token数
     * @return 生成的文本列表
     */
    List<String> batchGenerateText(List<String> prompts, String model, Double temperature, Integer maxTokens);

    /**
     * 获取模型列表
     * @return 模型列表
     */
    List<String> getModels();

    /**
     * 健康检查
     * @param model 模型名称
     * @return 是否健康
     */
    boolean healthCheck(String model);

    /**
     * 设置默认模型
     * @param model 模型名称
     */
    void setDefaultModel(String model);

    /**
     * 获取默认模型
     * @return 默认模型名称
     */
    String getDefaultModel();
}
