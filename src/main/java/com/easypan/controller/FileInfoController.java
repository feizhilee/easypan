package com.easypan.controller;

import com.easypan.annotation.GlobalInterceptor;
import com.easypan.annotation.VerifyParam;
import com.easypan.entity.dto.SessionWebUserDto;
import com.easypan.entity.dto.UploadResultDto;
import com.easypan.entity.enums.FileCategoryEnums;
import com.easypan.entity.enums.FileDelFlagEnums;
import com.easypan.entity.query.FileInfoQuery;
import com.easypan.entity.vo.FileInfoVO;
import com.easypan.entity.vo.PaginationResultVO;
import com.easypan.entity.vo.ResponseVO;
import com.easypan.service.FileInfoService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @Description: 文件信息Controller
 * @Author: YoshuaLee
 * @Date: 2025/07/22
 */
@RestController("/fileInfoController")
@RequestMapping("/file")
public class FileInfoController extends CommonFileController {
    @Resource
    private FileInfoService fileInfoService;

    /**
     * 根据条件分页查询
     */
    @RequestMapping("/loadDataList")
    @GlobalInterceptor
    public ResponseVO loadDataList(HttpSession httpSession, FileInfoQuery query, String category) {
        // 传过来的 category 是具体的 vedio、music、image 等名称，需要转化为数字再放进 query
        FileCategoryEnums categoryEnums = FileCategoryEnums.getByCode(category);
        if (categoryEnums != null) {
            query.setFileCatogary(categoryEnums.getCategory());
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

    // 文件分片上传，后端需要告诉前端总共需要切多少片、每片大小、总大小、传到第几个分片了、最后一个分片需要告诉是最后一个，然后合并
    // fileId 非必须，比如第一个分片是没有的，只有第一个分片传到后才会分配一个 fileId
    // fileMd5 是前端做的，如果是后端做则无法实现秒传，因为后端只有当文件全部传输完毕才能计算 md5，前端本地做完 md5 一并传过去即可
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

    @RequestMapping("/getImage/{imageFolder}/{imageName}")
    @GlobalInterceptor(checkParams = true)
    @Override
    public void getImage(HttpServletResponse response, @PathVariable("imageFolder") String imageFolder,
        @PathVariable("imageName") String imageName) {
        super.getImage(response, imageFolder, imageName);
    }

}