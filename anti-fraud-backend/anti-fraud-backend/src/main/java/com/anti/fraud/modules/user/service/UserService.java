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
}
