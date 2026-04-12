package com.anti.fraud.modules.exam.service;

import com.anti.fraud.modules.exam.entity.Challenge;
import com.anti.fraud.modules.exam.entity.ChallengeParticipant;
import java.util.List;
import java.util.Map;

public interface ChallengeService {

    /**
     * 创建挑战赛事
     * @param challenge 挑战赛事信息
     * @return 是否成功
     */
    boolean createChallenge(Challenge challenge);

    /**
     * 更新挑战赛事
     * @param challenge 挑战赛事信息
     * @return 是否成功
     */
    boolean updateChallenge(Challenge challenge);

    /**
     * 删除挑战赛事
     * @param id 挑战赛事ID
     * @return 是否成功
     */
    boolean deleteChallenge(Long id);

    /**
     * 获取挑战赛事详情
     * @param id 挑战赛事ID
     * @return 挑战赛事详情
     */
    Challenge getChallengeById(Long id);

    /**
     * 获取挑战赛事列表
     * @param status 状态
     * @param category 分类
     * @param page 页码
     * @param size 每页大小
     * @return 挑战赛事列表
     */
    List<Challenge> getChallengeList(Integer status, String category, int page, int size);

    /**
     * 参与挑战
     * @param challengeId 挑战赛事ID
     * @param userId 用户ID
     * @return 参与结果
     */
    Map<String, Object> participateChallenge(Long challengeId, Long userId);

    /**
     * 提交挑战答案
     * @param participantId 参与ID
     * @param answers 答案
     * @return 提交结果
     */
    Map<String, Object> submitChallengeAnswer(Long participantId, Map<String, Object> answers);

    /**
     * 获取挑战排行榜
     * @param challengeId 挑战赛事ID
     * @param page 页码
     * @param size 每页大小
     * @return 排行榜
     */
    List<ChallengeParticipant> getChallengeRanking(Long challengeId, int page, int size);

    /**
     * 获取用户挑战记录
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 挑战记录
     */
    List<ChallengeParticipant> getUserChallengeRecords(Long userId, int page, int size);

    /**
     * 计算挑战排名
     * @param challengeId 挑战赛事ID
     * @return 排名结果
     */
    boolean calculateChallengeRanking(Long challengeId);

    /**
     * 生成挑战题目
     * @param challengeId 挑战赛事ID
     * @return 题目列表
     */
    List<Map<String, Object>> generateChallengeQuestions(Long challengeId);

    /**
     * 获取挑战统计信息
     * @return 统计信息
     */
    Map<String, Object> getChallengeStats();
}
