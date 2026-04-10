package com.anti.fraud.modules.permission.mapper;

import com.anti.fraud.modules.permission.entity.Permission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 权限Mapper接口
 * 
 * @author Anti-Fraud Platform Team
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

    /**
     * 根据用户ID查询权限列表
     * @param userId 用户ID
     * @return 权限列表
     */
    @Select("SELECT DISTINCT p.* FROM sys_permission p " +
            "INNER JOIN sys_role_permission rp ON p.id = rp.permission_id " +
            "INNER JOIN sys_user_role ur ON rp.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND p.status = 1")
    List<Permission> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID查询权限列表
     * @param roleId 角色ID
     * @return 权限列表
     */
    @Select("SELECT p.* FROM sys_permission p " +
            "INNER JOIN sys_role_permission rp ON p.id = rp.permission_id " +
            "WHERE rp.role_id = #{roleId}")
    List<Permission> selectByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据权限编码查询权限
     * @param code 权限编码
     * @return 权限信息
     */
    @Select("SELECT * FROM sys_permission WHERE code = #{code} AND status = 1")
    Permission selectByCode(@Param("code") String code);

    /**
     * 查询所有启用的菜单权限（用于动态菜单生成）
     * @return 菜单权限列表
     */
    @Select("SELECT * FROM sys_permission WHERE type = 1 AND status = 1 ORDER BY sort ASC")
    List<Permission> selectAllMenus();

    /**
     * 根据父ID查询子权限
     * @param parentId 父权限ID
     * @return 子权限列表
     */
    @Select("SELECT * FROM sys_permission WHERE parent_id = #{parentId} AND status = 1 ORDER BY sort ASC")
    List<Permission> selectByParentId(@Param("parentId") Long parentId);
}
