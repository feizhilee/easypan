package com.easypan.service;

import com.easypan.entity.dto.SessionShareDto;
import com.easypan.entity.po.FileShare;
import com.easypan.entity.query.FileShareQuery;
import com.easypan.entity.vo.PaginationResultVO;

import java.util.List;

/**
 * @Description: 分享信息Service
 * @Author: YoshuaLee
 * @Date: 2025/08/05
 */
public interface FileShareService {
    /**
     * 根据条件查询列表
     */
    List<FileShare> findListByParam(FileShareQuery query);

    /**
     * 根据条件查询数量
     */
    Integer findCountByParam(FileShareQuery query);

    /**
     * 分页查询
     */
    PaginationResultVO<FileShare> findListByPage(FileShareQuery query);

    /**
     * 新增
     */
    Integer add(FileShare bean);

    /**
     * 批量新增
     */
    Integer addBatch(List<FileShare> listBean);

    /**
     * 批量新增或修改
     */
    Integer addOrUpdateBatch(List<FileShare> listBean);

    /**
     * 根据 ShareId 查询
     */
    FileShare getFileShareByShareId(String shareId);

    /**
     * 根据 ShareId 更新
     */
    Integer updateFileShareByShareId(FileShare bean, String shareId);

    /**
     * 根据 ShareId 删除
     */
    Integer deleteFileShareByShareId(String shareId);

    /**
     * 记录分享文件
     *
     * @param fileShare
     */
    void saveShare(FileShare fileShare);

    /**
     * （批量）取消分享
     *
     * @param shareIdArray
     * @param userId
     */
    void deleteFileShareBatch(String[] shareIdArray, String userId);

    /**
     * 校验提取码
     *
     * @param shareId
     * @param code
     * @return
     */
    SessionShareDto checkShareCode(String shareId, String code);
}