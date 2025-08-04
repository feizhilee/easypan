package com.easypan.controller;

import com.easypan.annotation.GlobalInterceptor;
import com.easypan.annotation.VerifyParam;
import com.easypan.entity.dto.SessionWebUserDto;
import com.easypan.entity.dto.UploadResultDto;
import com.easypan.entity.enums.FileCategoryEnums;
import com.easypan.entity.enums.FileDelFlagEnums;
import com.easypan.entity.enums.FileFolderTypeEnums;
import com.easypan.entity.po.FileInfo;
import com.easypan.entity.query.FileInfoQuery;
import com.easypan.entity.vo.FileInfoVO;
import com.easypan.entity.vo.PaginationResultVO;
import com.easypan.entity.vo.ResponseVO;
import com.easypan.utils.CopyTools;
import com.easypan.utils.StringTools;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @Description: 文件信息Controller
 * @Author: YoshuaLee
 * @Date: 2025/07/22
 */
@RestController("/fileInfoController")
@RequestMapping("/file")
public class FileInfoController extends CommonFileController {

    /**
     * 根据条件分页查询
     */
    @RequestMapping("/loadDataList")
    @GlobalInterceptor
    public ResponseVO loadDataList(HttpSession httpSession, FileInfoQuery query, String category) {
        // 传过来的 category 是具体的 video、music、image 等名称，需要转化为数字再放进 query
        FileCategoryEnums categoryEnum = FileCategoryEnums.getByCode(category);
        if (categoryEnum != null) {
            query.setFileCategory(categoryEnum.getCategory());
        }
        // 用户信息
        query.setUserId(getUserInfoFromSession(httpSession).getUserId());
        // 默认倒序排列
        query.setOrderBy("last_update_time desc");
        query.setDelFlag(FileDelFlagEnums.USING.getFlag());
        // VO 其实就是 PO 的缩略版，不需要那么多东西在里面
        PaginationResultVO result = fileInfoService.findListByPage(query);
        // FileInfo 转为 FileInfoVO
        return getSuccessResponseVO(convert2PaginationVO(result, FileInfoVO.class));
    }

    /**
     * 文件上传 文件分片上传，后端需要告诉前端总共需要切多少片、每片大小、总大小、传到第几个分片了、最后一个分片需要告诉是最后一个，然后合并 fileId 非必须，比如第一个分片是没有的，只有第一个分片传到后才会分配一个
     * fileId fileMd5 是前端做的，如果是后端做则无法实现秒传，因为后端只有当文件全部传输完毕才能计算 md5，前端本地做完 md5 一并传过去即可
     *
     * @param httpSession
     * @param fileId
     * @param file
     * @param fileName
     * @param filePid
     * @param fileMd5
     * @param chunkIndex
     * @param chunks
     * @return
     */
    @RequestMapping("/uploadFile")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO uploadFile(HttpSession httpSession, String fileId, MultipartFile file,
        @VerifyParam(required = true) String fileName, @VerifyParam(required = true) String filePid,
        @VerifyParam(required = true) String fileMd5, @VerifyParam(required = true) Integer chunkIndex,
        @VerifyParam(required = true) Integer chunks) {
        // 登录信息
        SessionWebUserDto webUserDto = getUserInfoFromSession(httpSession);
        UploadResultDto resultDto =
            fileInfoService.uploadFile(webUserDto, fileId, file, fileName, filePid, fileMd5, chunkIndex, chunks);
        return getSuccessResponseVO(resultDto);
    }

    /**
     * 获取缩略图
     *
     * @param response
     * @param imageFolder
     * @param imageName
     */
    @RequestMapping("/getImage/{imageFolder}/{imageName}")
    @Override
    public void getImage(HttpServletResponse response, @PathVariable("imageFolder") String imageFolder,
        @PathVariable("imageName") String imageName) {
        super.getImage(response, imageFolder, imageName);
    }

    /**
     * 视频预览
     *
     * @param request
     * @param response
     * @param session
     * @param fileId
     */
    @RequestMapping("/ts/getVideoInfo/{fileId}")
    @GlobalInterceptor(checkParams = true)
    public void getVideoInfo(HttpServletRequest request, HttpServletResponse response, HttpSession session,
        @PathVariable("fileId") @VerifyParam(required = true) String fileId) {
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        super.getFile(request, response, fileId, webUserDto.getUserId());
    }

    /**
     * 其他文件预览
     *
     * @param request
     * @param response
     * @param session
     * @param fileId
     */
    @RequestMapping("/getFile/{fileId}")
    @GlobalInterceptor(checkParams = true)
    public void getFile(HttpServletRequest request, HttpServletResponse response, HttpSession session,
        @PathVariable("fileId") @VerifyParam(required = true) String fileId) {
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        super.getFile(request, response, fileId, webUserDto.getUserId());
    }

    /**
     * 创建新文件夹
     *
     * @param session
     * @param filePid
     * @param fileName
     * @return
     */
    @RequestMapping("/newFolder")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO newFolder(HttpSession session, @VerifyParam(required = true) String filePid,
        @VerifyParam(required = true) String fileName) {
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        FileInfo fileInfo = fileInfoService.newFolder(filePid, webUserDto.getUserId(), fileName);
        return getSuccessResponseVO(CopyTools.copy(fileInfo, FileInfoVO.class));
    }

    /**
     * 获取目录信息
     *
     * @param session
     * @param path
     * @return
     */
    @RequestMapping("/getFolderInfo")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO getFolderInfo(HttpSession session, @VerifyParam(required = true) String path) {
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        return super.getFolderInfo(path, webUserDto.getUserId());
    }

    /**
     * 文件（夹）重命名
     *
     * @param session
     * @param fileId
     * @param fileName
     * @return
     */
    @RequestMapping("/rename")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO rename(HttpSession session, @VerifyParam(required = true) String fileId,
        @VerifyParam(required = true) String fileName) {
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        FileInfo fileInfo = fileInfoService.rename(fileId, webUserDto.getUserId(), fileName);
        return getSuccessResponseVO(CopyTools.copy(fileInfo, FileInfoVO.class));
    }

    /**
     * 移动文件（夹）前需要获取所有文件夹
     *
     * @param session
     * @param filePid
     * @param currentFileIds
     * @return
     */
    @RequestMapping("/loadAllFolder")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO loadAllFolder(HttpSession session, @VerifyParam(required = true) String filePid,
        String currentFileIds) {
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        FileInfoQuery infoQuery = new FileInfoQuery();
        infoQuery.setUserId(webUserDto.getUserId());
        infoQuery.setFilePid(filePid);
        infoQuery.setFolderType(FileFolderTypeEnums.FOLDER.getType());
        // 排除当前选中的文件（夹）（即不加载当前选中的待移动文件夹）
        if (!StringTools.isEmpty(currentFileIds)) {
            infoQuery.setExcludeFileIdArray(currentFileIds.split(","));
        }
        infoQuery.setDelFlag(FileDelFlagEnums.USING.getFlag());
        infoQuery.setOrderBy("create_time desc");
        List<FileInfo> fileInfoList = fileInfoService.findListByParam(infoQuery);
        return getSuccessResponseVO(CopyTools.copyList(fileInfoList, FileInfoVO.class));
    }

    /**
     * 移动一个或多个文件
     *
     * @param session
     * @param fileIds
     * @param filePid 为目的文件夹，作为移动后的父 id
     * @return
     */
    @RequestMapping("/changeFileFolder")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO changeFileFolder(HttpSession session, @VerifyParam(required = true) String fileIds,
        @VerifyParam(required = true) String filePid) {
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        fileInfoService.changeFileFolder(fileIds, filePid, webUserDto.getUserId());
        return getSuccessResponseVO(null);
    }

    /**
     * 创建下载链接，下载文件前需要有下载链接，下载文件本身不需要校验登陆，但是链接需要，code 具有实效性
     *
     * @param session
     * @param fileId
     * @return
     */
    @RequestMapping("/createDownloadUrl/{fileId}")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO createDownloadUrl(HttpSession session,
        @VerifyParam(required = true) @PathVariable("fileId") String fileId) {
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        return super.createDownloadUrl(fileId, webUserDto.getUserId());
    }

    @RequestMapping("/download/{code}")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    @Override
    public void download(HttpServletRequest request, HttpServletResponse response,
        @VerifyParam(required = true) @PathVariable("code") String code) throws Exception {
        super.download(request, response, code);
    }

}