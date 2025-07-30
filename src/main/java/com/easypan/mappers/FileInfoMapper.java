package com.easypan.mappers;

import org.apache.ibatis.annotations.Param;

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

    // 乐观锁
    void updateFileStatusWithOldStatus(@Param("fileId") String fileId, @Param("userId") String userId,
        @Param("bean") T t, @Param("oldStatus") Integer oldStatus);

}