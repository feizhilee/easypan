package com.easypan.utils;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class ScaleFilter {
    private static Logger logger = LoggerFactory.getLogger(ScaleFilter.class);

    /**
     * 生成封面，第一帧作为封面
     *
     * @param sourceFile
     * @param width
     * @param targetFile
     */
    public static void createCover4Video(File sourceFile, Integer width, File targetFile) {
        try {
            String cmd = "ffmpeg -i %s -y -vframes 1 -vf scale=%d:%d/a %s";
            ProcessUtils.executeCommand(
                String.format(cmd, sourceFile.getAbsoluteFile(), width, width, targetFile.getAbsoluteFile()), false);
        } catch (Exception e) {
            logger.error("生成视频封面失败", e);
        }
    }

    /**
     * 生成缩略图
     *
     * @param file
     * @param thumbnailWidth
     * @param targetFile
     * @param delSource
     * @return
     */
    public static Boolean createThumbnailWidthFFmpeg(File file, int thumbnailWidth, File targetFile,
        Boolean delSource) {
        try {
            BufferedImage src = ImageIO.read(file);
            // thumbnailWidth 缩略图的宽度   thumbnailHeight 缩略图的高度
            int sorceW = src.getWidth();
            int sorceH = src.getHeight();
            // 小于 指定高宽不压缩
            if (sorceW <= thumbnailWidth) {
                return false;
            }
            compressImage(file, thumbnailWidth, targetFile, delSource);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 压缩图片
     *
     * @param sourceFile
     * @param width
     * @param targetFile
     * @param delSource
     */
    public static void compressImage(File sourceFile, Integer width, File targetFile, Boolean delSource) {
        try {
            String cmd = "ffmpeg -i %s -vf scale=%d:-1 %s -y";
            ProcessUtils.executeCommand(
                String.format(cmd, sourceFile.getAbsoluteFile(), width, targetFile.getAbsoluteFile()), false);
            if (delSource) {
                FileUtils.forceDelete(sourceFile);
            }
        } catch (Exception e) {
            logger.error("压缩图片失败");
        }
    }

}
