package com.easypan.mappers;

import com.easypan.entity.po.FileInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 文件信息 Mapper
 * @Author: YoshuaLee
 * @Date: 2025/07/22
 */
public interface FileInfoMapper<T, P> extends BaseMapper {

    /**
     * 根据 FileIdAndUserId 查询
     */
    T selectByFileIdAndUserId(@Param("fileId") String fileId, @Param("userId") String userId);

    /**
     * 根据 FileIdAndUserId 更新
     */
    Integer updateByFileIdAndUserId(@Param("bean") T t, @Param("fileId") String fileId, @Param("userId") String userId);

    /**
     * 根据 FileIdAndUserId 删除
     */
    Integer deleteByFileIdAndUserId(@Param("fileId") String fileId, @Param("userId") String userId);

    /**
     * 查询用户使用空间
     *
     * @param userId
     * @return
     */
    Long selectUseSpace(@Param("userId") String userId);

    /**
     * 更新状态，乐观锁
     *
     * @param fileId
     * @param userId
     * @param t
     * @param oldStatus
     */
    void updateFileStatusWithOldStatus(@Param("fileId") String fileId, @Param("userId") String userId,
        @Param("bean") T t, @Param("oldStatus") Integer oldStatus);

    /**
     * 批量更新删除状态
     *
     * @param fileInfo
     * @param userId
     * @param filePidList
     * @param fileIdList
     * @param oldDelFlag
     */
    void updateFileDelFlagBatch(@Param("bean") FileInfo fileInfo, @Param("userId") String userId,
        @Param("filePidList") List<String> filePidList, @Param("fileIdList") List<String> fileIdList,
        @Param("oldDelFlag") Integer oldDelFlag);

}