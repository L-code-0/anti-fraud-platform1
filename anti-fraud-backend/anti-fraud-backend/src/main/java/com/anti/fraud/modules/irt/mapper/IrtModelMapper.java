package com.anti.fraud.modules.irt.mapper;

import com.anti.fraud.modules.irt.entity.IrtModel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * IRT模型Mapper
 */
@Mapper
public interface IrtModelMapper extends BaseMapper<IrtModel> {

    /**
     * 根据模型ID查询模型
     * @param modelId 模型ID
     * @return 模型
     */
    IrtModel selectByModelId(@Param("modelId") String modelId);

    /**
     * 根据模型类型查询模型列表
     * @param modelType 模型类型
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @return 模型列表
     */
    List<IrtModel> selectByModelType(@Param("modelType") Integer modelType, @Param("status") Integer status, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据状态查询模型列表
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @return 模型列表
     */
    List<IrtModel> selectByStatus(@Param("status") Integer status, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 更新模型状态
     * @param id 模型ID
     * @param status 状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 更新模型参数
     * @param id 模型ID
     * @param modelParams 模型参数
     * @param trainingAccuracy 训练精度
     * @param trainingDataSize 训练数据量
     * @param trainingTime 训练时间
     * @return 影响行数
     */
    int updateModelParams(@Param("id") Long id, @Param("modelParams") String modelParams, @Param("trainingAccuracy") Double trainingAccuracy, @Param("trainingDataSize") Integer trainingDataSize, @Param("trainingTime") LocalDateTime trainingTime);

    /**
     * 统计模型数量
     * @param modelType 模型类型
     * @param status 状态
     * @return 模型数量
     */
    Integer countByModelType(@Param("modelType") Integer modelType, @Param("status") Integer status);

    /**
     * 获取最新的模型
     * @param limit 数量限制
     * @param modelType 模型类型
     * @param status 状态
     * @return 模型列表
     */
    List<IrtModel> selectRecentModels(@Param("limit") Integer limit, @Param("modelType") Integer modelType, @Param("status") Integer status);

    /**
     * 批量插入模型
     * @param models 模型列表
     * @return 插入成功数量
     */
    int batchInsert(@Param("models") List<IrtModel> models);
}
