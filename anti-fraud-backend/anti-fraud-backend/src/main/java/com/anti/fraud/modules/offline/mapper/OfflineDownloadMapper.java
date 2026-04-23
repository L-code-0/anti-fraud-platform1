package com.anti.fraud.modules.offline.mapper;

import com.anti.fraud.modules.offline.entity.OfflineDownload;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 离线下载Mapper
 */
@Mapper
public interface OfflineDownloadMapper extends BaseMapper<OfflineDownload> {

    /**
     * 根据用户ID查询下载记录
     * @param userId 用户ID
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @return 下载记录列表
     */
    List<OfflineDownload> selectByUserId(@Param("userId") Long userId, @Param("status") Integer status, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据资源ID查询下载记录
     * @param resourceId 资源ID
     * @param status 状态
     * @return 下载记录列表
     */
    List<OfflineDownload> selectByResourceId(@Param("resourceId") Long resourceId, @Param("status") Integer status);

    /**
     * 按状态统计下载记录数量
     * @param userId 用户ID
     * @return 状态统计
     */
    List<Map<String, Object>> selectStatusStats(@Param("userId") Long userId);

    /**
     * 按资源类型统计下载记录数量
     * @param userId 用户ID
     * @return 类型统计
     */
    List<Map<String, Object>> selectResourceTypeStats(@Param("userId") Long userId);

    /**
     * 更新下载状态
     * @param id 下载记录ID
     * @param status 状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 更新下载进度
     * @param id 下载记录ID
     * @param progress 进度
     * @param downloadedSize 已下载大小
     * @return 影响行数
     */
    int updateProgress(@Param("id") Long id, @Param("progress") Integer progress, @Param("downloadedSize") Long downloadedSize);

    /**
     * 完成下载
     * @param id 下载记录ID
     * @param status 状态
     * @param progress 进度
     * @param endTime 完成时间
     * @return 影响行数
     */
    int completeDownload(@Param("id") Long id, @Param("status") Integer status, @Param("progress") Integer progress, @Param("endTime") java.time.LocalDateTime endTime);

    /**
     * 标记下载失败
     * @param id 下载记录ID
     * @param status 状态
     * @param failureReason 失败原因
     * @return 影响行数
     */
    int markDownloadFailed(@Param("id") Long id, @Param("status") Integer status, @Param("failureReason") String failureReason);

    /**
     * 获取待下载的记录
     * @param limit 数量限制
     * @return 待下载记录列表
     */
    List<OfflineDownload> selectPendingDownloads(@Param("limit") Integer limit);

    /**
     * 获取下载中的记录
     * @param limit 数量限制
     * @return 下载中记录列表
     */
    List<OfflineDownload> selectDownloadingRecords(@Param("limit") Integer limit);

    /**
     * 统计用户下载总量
     * @param userId 用户ID
     * @return 下载总量（字节）
     */
    Long selectTotalDownloadSize(@Param("userId") Long userId);

    /**
     * 统计用户已完成的下载数量
     * @param userId 用户ID
     * @return 已完成下载数量
     */
    Integer selectCompletedCount(@Param("userId") Long userId);
}
