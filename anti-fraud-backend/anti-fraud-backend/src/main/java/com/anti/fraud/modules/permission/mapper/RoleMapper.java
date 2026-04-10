package com.anti.fraud.modules.permission.mapper;

import com.anti.fraud.modules.permission.entity.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色Mapper接口
 * 
 * @author Anti-Fraud Platform Team
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 根据用户ID查询角色列表
     * @param userId 用户ID
     * @return 角色列表
     */
    @Select("SELECT r.* FROM sys_role r " +
            "INNER JOIN sys_user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.status = 1")
    List<Role> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据角色编码查询角色
     * @param code 角色编码
     * @return 角色信息
     */
    @Select("SELECT * FROM sys_role WHERE code = #{code} AND status = 1")
    Role selectByCode(@Param("code") String code);

    /**
     * 查询所有启用的角色
     * @return 角色列表
     */
    @Select("SELECT * FROM sys_role WHERE status = 1 ORDER BY level ASC")
    List<Role> selectAllEnabled();

    /**
     * 检查用户是否拥有指定角色
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 是否拥有
     */
    @Select("SELECT COUNT(1) > 0 FROM sys_user_role WHERE user_id = #{userId} AND role_id = #{roleId}")
    boolean hasRole(@Param("userId") Long userId, @Param("roleId") Long roleId);
}
