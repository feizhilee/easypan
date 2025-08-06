package com.easypan.controller;

import com.easypan.annotation.GlobalInterceptor;
import com.easypan.annotation.VerifyParam;
import com.easypan.component.RedisComponent;
import com.easypan.entity.dto.SysSettingsDto;
import com.easypan.entity.query.FileInfoQuery;
import com.easypan.entity.query.UserInfoQuery;
import com.easypan.entity.vo.PaginationResultVO;
import com.easypan.entity.vo.ResponseVO;
import com.easypan.entity.vo.UserInfoVO;
import com.easypan.service.FileInfoService;
import com.easypan.service.UserInfoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController("/adminController")
@RequestMapping("/admin")
public class AdminController extends ABaseController {
    @Resource
    private FileInfoService fileInfoService;

    @Resource
    RedisComponent redisComponent;

    @Resource
    UserInfoService userInfoService;

    /**
     * 获取系统设置
     *
     * @return
     */
    @RequestMapping("/getSysSettings")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public ResponseVO getSysSettings() {
        return getSuccessResponseVO(redisComponent.getSysSettingsDto());
    }

    /**
     * 保存系统设置
     *
     * @param registerEmailTitle
     * @param registerMailContent
     * @param userInitUseSpace
     * @return
     */
    @RequestMapping("/saveSysSettings")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public ResponseVO saveSysSettings(@VerifyParam(required = true) String registerEmailTitle,
        @VerifyParam(required = true) String registerMailContent,
        @VerifyParam(required = true) Integer userInitUseSpace) {
        SysSettingsDto sysSettingsDto = new SysSettingsDto();
        sysSettingsDto.setRegisterEmailTitle(registerEmailTitle);
        sysSettingsDto.setRegisterMailContent(registerMailContent);
        sysSettingsDto.setUserInitUseSpace(userInitUseSpace);
        redisComponent.saveSysSettingsDto(sysSettingsDto);
        return getSuccessResponseVO(null);
    }

    /**
     * 加载用户列表
     *
     * @param userInfoQuery
     * @return
     */
    @RequestMapping("/loadUserList")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public ResponseVO loadUserList(UserInfoQuery userInfoQuery) {
        userInfoQuery.setOrderBy("join_time desc");
        PaginationResultVO resultVO = userInfoService.findListByPage(userInfoQuery);
        return getSuccessResponseVO(convert2PaginationVO(resultVO, UserInfoVO.class));
    }

    /**
     * 修改用户状态
     *
     * @param userId
     * @param status
     * @return
     */
    @RequestMapping("/updateUserStatus")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public ResponseVO updateUserStatus(@VerifyParam(required = true) String userId,
        @VerifyParam(required = true) Integer status) {
        userInfoService.updateUserStatus(userId, status);
        return getSuccessResponseVO(null);
    }

    /**
     * 修改用户空间
     *
     * @param userId
     * @param changeSpace
     * @return
     */
    @RequestMapping("/updateUserSpace")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public ResponseVO updateUserSpace(@VerifyParam(required = true) String userId,
        @VerifyParam(required = true) Integer changeSpace) {
        userInfoService.changeUserSpace(userId, changeSpace);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/loadFileList")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public ResponseVO loadFileList(FileInfoQuery query) {
        query.setOrderBy("last_update_time desc");
        // 需要显示发布人，关联查询
        query.setQueryNickName(true);
        PaginationResultVO result = fileInfoService.findListByPage(query);
        return getSuccessResponseVO(result);
    }

}
