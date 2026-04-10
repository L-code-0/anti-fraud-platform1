package com.anti.fraud.modules.file.mapper;

import com.anti.fraud.modules.file.entity.FileInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 文件信息Mapper
 */
@Mapper
public interface FileInfoMapper extends BaseMapper<FileInfo> {
    
    /**
     * 分页查询文件列表
     */
    IPage<FileInfo> selectFilePage(
            Page<FileInfo> page,
            @Param("category") String category,
            @Param("bizType") String bizType,
            @Param("keyword") String keyword
    );
    
    /**
     * 根据MD5查询文件（用于去重）
     */
    FileInfo selectByMd5(@Param("md5") String md5);
    
    /**
     * 查询用户上传的文件列表
     */
    IPage<FileInfo> selectByUserId(
            Page<FileInfo> page,
            @Param("userId") Long userId
    );
    
    /**
     * 查询业务关联的文件列表
     */
    IPage<FileInfo> selectByBiz(
            Page<FileInfo> page,
            @Param("bizType") String bizType,
            @Param("bizId") Long bizId
    );
}
