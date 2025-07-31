package com.easypan.controller;

import com.easypan.entity.config.AppConfig;
import com.easypan.entity.constants.Constants;
import com.easypan.entity.enums.FileCategoryEnums;
import com.easypan.entity.po.FileInfo;
import com.easypan.service.FileInfoService;
import com.easypan.utils.StringTools;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

public class CommonFileController extends ABaseController {
    @Resource
    protected AppConfig appConfig;

    @Resource
    protected FileInfoService fileInfoService;

    /**
     * 返回缩略图
     *
     * @param response
     * @param imageFolder
     * @param imageName
     */
    protected void getImage(HttpServletResponse response, String imageFolder, String imageName) {
        if (StringUtils.isEmpty(imageFolder) || StringUtils.isEmpty(imageName) || !StringTools.pathIsOk(
            imageFolder) || !StringTools.pathIsOk(imageName)) {
            return;
        }
        String imageSuffix = StringTools.getFileSuffix(imageName);
        String filePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + imageFolder + "/" + imageName;
        imageSuffix = imageSuffix.replace(".", "");
        String contentType = "image/" + imageSuffix;
        response.setContentType(contentType);
        response.setHeader("Cache-Control", "max-age=2592000");
        readFile(response, filePath);
    }

    protected void getFile(HttpServletRequest request, HttpServletResponse response, String fileId, String userId) {
        FileInfo fileInfo = fileInfoService.getFileInfoByFileIdAndUserId(fileId, userId);
        String filePath = null;
        if (null == fileInfo) {
            return;
        }
        // 如果是视频文件就不能直接读取文件，需要找到对应文件夹读 m3u8 索引
        if (FileCategoryEnums.VIDEO.getCategory().equals(fileInfo.getFileCategory())) {
            String fileNameNoSuffix = StringTools.getFileNameNoSuffix(fileInfo.getFilePath());
            filePath =
                appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + fileNameNoSuffix + "/" + Constants.M3U8_NAME;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        readFile(response, filePath);
    }
}
