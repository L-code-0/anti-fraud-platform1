package com.anti.fraud.modules.user.mapper;

import com.anti.fraud.modules.user.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT * FROM sys_user WHERE username = #{username} AND deleted = 0")
    User selectByUsername(String username);

    @Select("SELECT * FROM sys_user WHERE phone = #{phone} AND deleted = 0")
    User selectByPhone(String phone);
}