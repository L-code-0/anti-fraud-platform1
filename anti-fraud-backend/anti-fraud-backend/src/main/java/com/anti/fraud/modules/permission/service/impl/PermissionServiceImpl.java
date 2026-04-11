package com.anti.fraud.modules.permission.service.impl;

import com.anti.fraud.modules.permission.dto.PermissionDTO;
import com.anti.fraud.modules.permission.dto.PermissionQueryDTO;
import com.anti.fraud.modules.permission.entity.Permission;
import com.anti.fraud.modules.permission.entity.Role;
import com.anti.fraud.modules.permission.mapper.PermissionMapper;
import com.anti.fraud.modules.permission.mapper.RoleMapper;
import com.anti.fraud.modules.permission.service.PermissionService;
import com.anti.fraud.modules.permission.vo.PermissionVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 权限Service实现类
 *
 * @author Anti-Fraud Platform Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    private final PermissionMapper permissionMapper;
    private final RoleMapper roleMapper;

    @Override
    public Page<PermissionVO> queryPage(PermissionQueryDTO queryDTO) {
        Page<Permission> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(queryDTO.getName())) {
            wrapper.like(Permission::getName, queryDTO.getName());
        }
        if (StringUtils.hasText(queryDTO.getCode())) {
            wrapper.eq(Permission::getCode, queryDTO.getCode());
        }
        if (queryDTO.getType() != null) {
            wrapper.eq(Permission::getType, queryDTO.getType());
        }
        if (queryDTO.getStatus() != null) {
            wrapper.eq(Permission::getStatus, queryDTO.getStatus());
        }
        if (queryDTO.getParentId() != null) {
            wrapper.eq(Permission::getParentId, queryDTO.getParentId());
        }

        wrapper.orderByAsc(Permission::getSort);
        Page<Permission> result = page(page, wrapper);

        Page<PermissionVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream().map(this::convertToVO).collect(Collectors.toList()));

        return voPage;
    }

    @Override
    public PermissionVO getById(Long id) {
        Permission permission = baseMapper.selectById(id);
        return convertToVO(permission);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean create(PermissionDTO dto) {
        Permission permission = new Permission();
        permission.setName(dto.getName());
        permission.setCode(dto.getCode());
        permission.setType(dto.getType());
        permission.setParentId(dto.getParentId() != null ? dto.getParentId() : 0L);
        permission.setPath(dto.getPath());
        permission.setComponent(dto.getComponent());
        permission.setIcon(dto.getIcon());
        permission.setSort(dto.getSort() != null ? dto.getSort() : 0);
        permission.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        permission.setVisible(dto.getVisible() != null ? dto.getVisible() : 1);
        permission.setKeepAlive(dto.getKeepAlive() != null ? dto.getKeepAlive() : 1);
        permission.setDescription(dto.getDescription());
        permission.setRemark(dto.getRemark());

        return baseMapper.insert(permission) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(PermissionDTO dto) {
        if (dto.getId() == null) {
            return false;
        }

        Permission permission = new Permission();
        permission.setId(dto.getId());
        permission.setName(dto.getName());
        permission.setCode(dto.getCode());
        permission.setType(dto.getType());
        permission.setParentId(dto.getParentId() != null ? dto.getParentId() : 0L);
        permission.setPath(dto.getPath());
        permission.setComponent(dto.getComponent());
        permission.setIcon(dto.getIcon());
        permission.setSort(dto.getSort() != null ? dto.getSort() : 0);
        permission.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        permission.setVisible(dto.getVisible() != null ? dto.getVisible() : 1);
        permission.setKeepAlive(dto.getKeepAlive() != null ? dto.getKeepAlive() : 1);
        permission.setDescription(dto.getDescription());
        permission.setRemark(dto.getRemark());

        return baseMapper.updateById(permission) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        // 删除该权限及其子权限
        List<Long> ids = new ArrayList<>();
        ids.add(id);
        findChildIds(id, ids);

        return baseMapper.deleteByIds(ids) > 0;
    }

    private void findChildIds(Long parentId, List<Long> ids) {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getParentId, parentId);
        List<Permission> children = baseMapper.selectList(wrapper);
        for (Permission child : children) {
            ids.add(child.getId());
            findChildIds(child.getId(), ids);
        }
    }

    @Override
    public List<String> getPermissionCodesByUserId(Long userId) {
        List<Permission> permissions = permissionMapper.selectByUserId(userId);
        return permissions.stream()
                .map(Permission::getCode)
                .collect(Collectors.toList());
    }

    @Override
    public List<PermissionVO> getMenuTreeByUserId(Long userId) {
        List<Permission> permissions = permissionMapper.selectByUserId(userId);
        // 过滤出菜单类型的权限
        List<Permission> menus = permissions.stream()
                .filter(p -> p.getType() != null && p.getType() == 1)
                .collect(Collectors.toList());

        return buildMenuTree(menus);
    }

    @Override
    public List<PermissionVO> getAllMenuTree() {
        List<Permission> menus = permissionMapper.selectAllMenus();
        return buildMenuTree(menus);
    }

    private List<PermissionVO> buildMenuTree(List<Permission> permissions) {
        Map<Long, List<Permission>> groupByParent = permissions.stream()
                .collect(Collectors.groupingBy(Permission::getParentId));

        List<PermissionVO> result = new ArrayList<>();
        for (Permission permission : permissions) {
            if (permission.getParentId() == null || permission.getParentId() == 0) {
                PermissionVO vo = convertToVO(permission);
                vo.setChildren(buildChildren(permission.getId(), groupByParent));
                result.add(vo);
            }
        }

        // 按sort排序
        result.sort(Comparator.comparing(PermissionVO::getSort));
        return result;
    }

    private List<PermissionVO> buildChildren(Long parentId, Map<Long, List<Permission>> groupByParent) {
        List<Permission> children = groupByParent.getOrDefault(parentId, Collections.emptyList());
        if (children.isEmpty()) {
            return Collections.emptyList();
        }

        List<PermissionVO> childVOs = new ArrayList<>();
        for (Permission child : children) {
            PermissionVO vo = convertToVO(child);
            vo.setChildren(buildChildren(child.getId(), groupByParent));
            childVOs.add(vo);
        }

        childVOs.sort(Comparator.comparing(PermissionVO::getSort));
        return childVOs;
    }

    @Override
    public List<Long> getPermissionIdsByRoleId(Long roleId) {
        List<Permission> permissions = permissionMapper.selectByRoleId(roleId);
        return permissions.stream()
                .map(Permission::getId)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignPermissions(Long roleId, List<Long> permissionIds) {
        // 先删除该角色的所有权限
        roleMapper.deleteRolePermissions(roleId);

        // 重新插入权限
        if (permissionIds != null && !permissionIds.isEmpty()) {
            for (Long permissionId : permissionIds) {
                roleMapper.insertRolePermission(roleId, permissionId);
            }
        }

        return true;
    }

    @Override
    public boolean hasPermission(Long userId, String permissionCode) {
        List<String> codes = getPermissionCodesByUserId(userId);
        return codes.contains(permissionCode);
    }

    @Override
    public boolean hasRole(Long userId, String roleCode) {
        List<Role> roles = roleMapper.selectByUserId(userId);
        return roles.stream().anyMatch(r -> r.getCode().equals(roleCode));
    }

    @Override
    public boolean checkRoleLevel(Long userId, int requiredLevel) {
        List<Role> roles = roleMapper.selectByUserId(userId);
        if (roles.isEmpty()) {
            return false;
        }
        // 用户拥有最高角色的级别
        int maxLevel = roles.stream()
                .mapToInt(Role::getLevel)
                .max()
                .orElse(0);
        return maxLevel >= requiredLevel;
    }

    private PermissionVO convertToVO(Permission permission) {
        if (permission == null) {
            return null;
        }
        PermissionVO vo = new PermissionVO();
        vo.setId(permission.getId());
        vo.setName(permission.getName());
        vo.setCode(permission.getCode());
        vo.setType(permission.getType());
        vo.setParentId(permission.getParentId());
        vo.setPath(permission.getPath());
        vo.setComponent(permission.getComponent());
        vo.setIcon(permission.getIcon());
        vo.setSort(permission.getSort());
        vo.setStatus(permission.getStatus());
        vo.setVisible(permission.getVisible());
        vo.setKeepAlive(permission.getKeepAlive());
        vo.setDescription(permission.getDescription());
        vo.setCreateTime(permission.getCreateTime());
        return vo;
    }
}

