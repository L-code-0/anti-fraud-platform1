package com.anti.fraud.modules.user.service;

import com.anti.fraud.modules.user.dto.LoginDTO;
import com.anti.fraud.modules.user.dto.RegisterDTO;
import com.anti.fraud.modules.user.entity.User;
import com.anti.fraud.modules.user.vo.LoginVO;
import com.anti.fraud.modules.user.vo.UserVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface UserService {

    LoginVO login(LoginDTO loginDTO);

    void register(RegisterDTO registerDTO);

    UserVO getUserInfo(Long userId);

    void updateUserInfo(Long userId, UserVO userVO);

    void changePassword(Long userId, String oldPassword, String newPassword);

    Page<UserVO> getUserPage(Integer page, Integer size, String keyword, Integer roleId);

    void updateUserStatus(Long userId, Integer status);

    String generatePasswordHash(String password);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户实体
     */
    User findByUsername(String username);

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息
     */
    UserVO getCurrentUserInfo();

    /**
     * 账号合并
     *
     * @param sourceUserId 源用户ID
     * @param targetUserId 目标用户ID
     * @param password 目标用户密码
     */
    void mergeAccounts(Long sourceUserId, Long targetUserId, String password);

    /**
     * 注销账号
     *
     * @param userId 用户ID
     * @param password 用户密码
     */
    void deleteAccount(Long userId, String password);

    /**
     * 软删除用户（标记为已删除）
     *
     * @param userId 用户ID
     */
    void softDeleteUser(Long userId);
}
