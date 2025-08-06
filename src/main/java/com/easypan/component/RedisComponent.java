package com.easypan.component;

import com.easypan.entity.constants.Constants;
import com.easypan.entity.dto.DownloadFileDto;
import com.easypan.entity.dto.SysSettingsDto;
import com.easypan.entity.dto.UserSpaceDto;
import com.easypan.entity.po.FileInfo;
import com.easypan.entity.po.UserInfo;
import com.easypan.entity.query.FileInfoQuery;
import com.easypan.entity.query.UserInfoQuery;
import com.easypan.mappers.FileInfoMapper;
import com.easypan.mappers.UserInfoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component("redisComponent")
public class RedisComponent {
    Logger logger = LoggerFactory.getLogger(RedisComponent.class);

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;

    @Resource
    private FileInfoMapper<FileInfo, FileInfoQuery> fileInfoMapper;

    /**
     * 获取系统设置
     *
     * @return 系统设置
     */
    public SysSettingsDto getSysSettingsDto() {
        SysSettingsDto sysSettingsDto = (SysSettingsDto)redisUtils.get(Constants.REDIS_KEY_SYS_SETTING);
        // logger.info("get 到了 sysSettingsDto");
        if (sysSettingsDto == null) {
            // logger.info("new 一个 sysSettingsDto");
            sysSettingsDto = new SysSettingsDto();
            redisUtils.set(Constants.REDIS_KEY_SYS_SETTING, sysSettingsDto);
        }
        return sysSettingsDto;
    }

    /**
     * 管理员用来保存设置
     *
     * @param sysSettingsDto
     */
    public void saveSysSettingsDto(SysSettingsDto sysSettingsDto) {
        redisUtils.set(Constants.REDIS_KEY_SYS_SETTING, sysSettingsDto);
    }

    /**
     * 用户登录后设置到缓存区，为什么需要这样？因为需要实时显示出登录账号的某些信息，
     *
     * @param userId
     * @param userSpaceDto
     */
    public void saveUserSpaceUse(String userId, UserSpaceDto userSpaceDto) {
        redisUtils.setex(Constants.REDIS_KEY_USER_SPACE_USE + userId, userSpaceDto, Constants.REDIS_KEY_EXPIRES_DAY);
    }

    /**
     * 刷新用户使用和总空间
     *
     * @param userId
     * @return
     */
    public UserSpaceDto resetUserSpaceUse(String userId) {
        UserSpaceDto userSpaceDto = new UserSpaceDto();
        Long userSpace = this.fileInfoMapper.selectUseSpace(userId);
        userSpaceDto.setUseSpace(userSpace);
        UserInfo userInfo = this.userInfoMapper.selectByUserId(userId);
        userSpaceDto.setTotalSpace(userInfo.getTotalSpace());
        redisUtils.setex(Constants.REDIS_KEY_USER_SPACE_USE + userId, userSpaceDto, Constants.REDIS_KEY_EXPIRES_DAY);
        return userSpaceDto;
    }

    /**
     * 获取用户使用空间大小
     *
     * @param userId
     * @return
     */
    public UserSpaceDto getUserSpaceUse(String userId) {
        UserSpaceDto userSpaceDto = (UserSpaceDto)redisUtils.get(Constants.REDIS_KEY_USER_SPACE_USE + userId);
        if (userSpaceDto == null) {
            userSpaceDto = new UserSpaceDto();
            Long useSpace = fileInfoMapper.selectUseSpace(userId);
            userSpaceDto.setUseSpace(useSpace);
            userSpaceDto.setTotalSpace(getSysSettingsDto().getUserInitUseSpace() * Constants.MB);
            saveUserSpaceUse(userId, userSpaceDto);
        }
        return userSpaceDto;
    }

    /**
     * 保存文件临时大小，每次上传分片都得保存
     *
     * @param userId
     * @param fileId
     * @param fileSize
     */
    public void saveFileTempSize(String userId, String fileId, Long fileSize) {
        Long currentSize = getFileTempSize(userId, fileId);
        redisUtils.setex(Constants.REDIS_KEY_USER_FILE_TEMP_SIZE + userId + fileId, currentSize + fileSize,
            Constants.REDIS_KEY_EXPIRES_ONE_HOUR);
    }

    /**
     * 获取文件临时大小
     *
     * @param userId
     * @param fileId
     * @return
     */
    public Long getFileTempSize(String userId, String fileId) {
        Long currentSize = getFileSizeFromRedis(Constants.REDIS_KEY_USER_FILE_TEMP_SIZE + userId + fileId);
        return currentSize;
    }

    private Long getFileSizeFromRedis(String key) {
        Object sizeObj = redisUtils.get(key);
        if (sizeObj == null) {
            return 0L;
        }
        if (sizeObj instanceof Integer) {
            return ((Integer)sizeObj).longValue();
        } else if (sizeObj instanceof Long) {
            return (Long)sizeObj;
        }
        return 0L;
    }

    /**
     * 下载文件信息保存到 redis
     *
     * @param code
     * @param downloadFileDto
     */
    public void saveDownloadCode(String code, DownloadFileDto downloadFileDto) {
        redisUtils.setex(Constants.REDIS_KEY_DOWNLOAD + code, downloadFileDto, Constants.REDIS_KEY_EXPIRES_FIVE_MIN);
    }

    /**
     * 从 redis 中获取下载文件信息
     *
     * @param code
     * @return
     */
    public DownloadFileDto getDownloadCode(String code) {
        return (DownloadFileDto)redisUtils.get(Constants.REDIS_KEY_DOWNLOAD + code);
    }
}