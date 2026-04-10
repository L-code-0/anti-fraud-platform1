package com.anti.fraud.modules.permission.service;

import com.anti.fraud.modules.permission.dto.PermissionDTO;
import com.anti.fraud.modules.permission.dto.PermissionQueryDTO;
import com.anti.fraud.modules.permission.entity.Permission;
import com.anti.fraud.modules.permission.vo.PermissionVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 权限Service接口
 * 
 * @author Anti-Fraud Platform Team
 */
public interface PermissionService extends IService<Permission> {

    /**
     * 分页查询权限列表
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<PermissionVO> queryPage(PermissionQueryDTO queryDTO);

    /**
     * 根据ID查询权限详情
     * @param id 权限ID
     * @return 权限详情
     */
    PermissionVO getById(Long id);

    /**
     * 创建权限
     * @param dto 权限信息
     * @return 是否成功
     */
    boolean create(PermissionDTO dto);

    /**
     * 更新权限
     * @param dto 权限信息
     * @return 是否成功
     */
    boolean update(PermissionDTO dto);

    /**
     * 删除权限
     * @param id 权限ID
     * @return 是否成功
     */
    boolean delete(Long id);

    /**
     * 根据用户ID查询权限列表
     * @param userId 用户ID
     * @return 权限编码列表
     */
    List<String> getPermissionCodesByUserId(Long userId);

    /**
     * 根据用户ID查询菜单权限树
     * @param userId 用户ID
     * @return 菜单权限树
     */
    List<PermissionVO> getMenuTreeByUserId(Long userId);

    /**
     * 查询所有菜单权限树
     * @return 菜单权限树
     */
    List<PermissionVO> getAllMenuTree();

    /**
     * 根据角色ID查询权限ID列表
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    List<Long> getPermissionIdsByRoleId(Long roleId);

    /**
     * 分配权限给角色
     * @param roleId 角色ID
     * @param permissionIds 权限ID列表
     * @return 是否成功
     */
    boolean assignPermissions(Long roleId, List<Long> permissionIds);

    /**
     * 检查用户是否拥有指定权限
     * @param userId 用户ID
     * @param permissionCode 权限编码
     * @return 是否拥有
     */
    boolean hasPermission(Long userId, String permissionCode);

    /**
     * 检查用户是否拥有指定角色
     * @param userId 用户ID
     * @param roleCode 角色编码
     * @return 是否拥有
     */
    boolean hasRole(Long userId, String roleCode);

    /**
     * 根据角色级别检查权限（角色级别越高，权限越大）
     * @param userId 用户ID
     * @param requiredLevel 需要的最低级别
     * @return 是否满足
     */
    boolean checkRoleLevel(Long userId, int requiredLevel);
}
