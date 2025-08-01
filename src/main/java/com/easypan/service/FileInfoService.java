package com.easypan.service;

import com.easypan.entity.dto.SessionWebUserDto;
import com.easypan.entity.dto.UploadResultDto;
import com.easypan.entity.po.FileInfo;
import com.easypan.entity.query.FileInfoQuery;
import com.easypan.entity.vo.PaginationResultVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Description: 文件信息Service
 * @Author: YoshuaLee
 * @Date: 2025/07/22
 */
public interface FileInfoService {
    /**
     * 根据条件查询列表
     */
    List<FileInfo> findListByParam(FileInfoQuery query);

    /**
     * 根据条件查询数量
     */
    Integer findCountByParam(FileInfoQuery query);

    /**
     * 分页查询
     */
    PaginationResultVO<FileInfo> findListByPage(FileInfoQuery query);

    /**
     * 新增
     */
    Integer add(FileInfo bean);

    /**
     * 批量新增
     */
    Integer addBatch(List<FileInfo> listBean);

    /**
     * 批量新增或修改
     */
    Integer addOrUpdateBatch(List<FileInfo> listBean);

    /**
     * 根据 FileIdAndUserId 查询
     */
    FileInfo getFileInfoByFileIdAndUserId(String fileId, String userId);

    /**
     * 根据 FileIdAndUserId 更新
     */
    Integer updateFileInfoByFileIdAndUserId(FileInfo bean, String fileId, String userId);

    /**
     * 根据 FileIdAndUserId 删除
     */
    Integer deleteFileInfoByFileIdAndUserId(String fileId, String userId);

    /**
     * 上传文件
     *
     * @param webUserDto
     * @param fileId
     * @param file
     * @param fileName
     * @param filePid
     * @param fileMd5
     * @param chunkIndex
     * @param chunks
     * @return
     */
    UploadResultDto uploadFile(SessionWebUserDto webUserDto, String fileId, MultipartFile file, String fileName,
        String filePid, String fileMd5, Integer chunkIndex, Integer chunks);

    /**
     * 新建文件夹
     * @param filePid
     * @param userId
     * @param folderName
     * @return
     */
    FileInfo newFolder(String filePid, String userId, String folderName);
}