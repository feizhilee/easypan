package com.easypan.mappers;

import org.apache.ibatis.annotations.Param;

/**
 * @Description: 分享信息 Mapper
 * @Author: YoshuaLee
 * @Date: 2025/08/05
 */
public interface FileShareMapper<T, P> extends BaseMapper {

    /**
     * 根据 ShareId 查询
     */
    T selectByShareId(@Param("shareId") String shareId);

    /**
     * 根据 ShareId 更新
     */
    Integer updateByShareId(@Param("bean") T t, @Param("shareId") String shareId);

    /**
     * 根据 ShareId 删除
     */
    Integer deleteByShareId(@Param("shareId") String shareId);

    /**
     * 批量删除分享
     *
     * @param shareIdArray
     * @param userId
     * @return
     */
    Integer deleteFileShareBatch(@Param("shareIdArray") String[] shareIdArray, @Param("userId") String userId);

    /**
     * 更新浏览次数 不能先查再加1，单人可以，多人并发的情况下会错误
     *
     * @param shareId
     */
    void updateShareShowCount(@Param("shareId") String shareId);

}