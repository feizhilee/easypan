package com.easypan.controller;

import com.easypan.annotation.GlobalInterceptor;
import com.easypan.annotation.VerifyParam;
import com.easypan.entity.constants.Constants;
import com.easypan.entity.dto.SessionShareDto;
import com.easypan.entity.dto.SessionWebUserDto;
import com.easypan.entity.enums.FileDelFlagEnums;
import com.easypan.entity.enums.ResponseCodeEnum;
import com.easypan.entity.po.FileInfo;
import com.easypan.entity.po.FileShare;
import com.easypan.entity.po.UserInfo;
import com.easypan.entity.query.FileInfoQuery;
import com.easypan.entity.vo.FileInfoVO;
import com.easypan.entity.vo.PaginationResultVO;
import com.easypan.entity.vo.ResponseVO;
import com.easypan.entity.vo.ShareInfoVO;
import com.easypan.exception.BusinessException;
import com.easypan.service.FileInfoService;
import com.easypan.service.FileShareService;
import com.easypan.service.UserInfoService;
import com.easypan.utils.CopyTools;
import com.easypan.utils.StringTools;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;

@RestController("/webShareController")
@RequestMapping("/showShare")
public class WebShareController extends CommonFileController {

    @Resource
    private FileShareService fileShareService;

    @Resource
    private FileInfoService fileInfoService;

    @Resource
    private UserInfoService userInfoService;

    /**
     * 点击分享链接后跳转界面获取信息
     *
     * @param session
     * @param shareId
     * @return
     */
    @RequestMapping("/getShareLoginInfo")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public ResponseVO getShareLoginInfo(HttpSession session, @VerifyParam(required = true) String shareId) {
        SessionShareDto sessionShareDto = getSessionShareFromSession(session, shareId);
        if (sessionShareDto == null) {
            return getSuccessResponseVO(null);
        }
        ShareInfoVO shareInfoVO = getShareInfoCommon(shareId);
        // 判断是否是当前用户分享的文件
        SessionWebUserDto userDto = getUserInfoFromSession(session);
        if (userDto != null && userDto.getUserId().equals(sessionShareDto.getShareUserId())) {
            shareInfoVO.setCurrentUser(true);
        } else {
            shareInfoVO.setCurrentUser(false);
        }
        return getSuccessResponseVO(shareInfoVO);
    }

    /**
     * 获取分享文件信息
     *
     * @param shareId
     * @return
     */
    @RequestMapping("/getShareInfo")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public ResponseVO getShareInfo(@VerifyParam(required = true) String shareId) {
        return getSuccessResponseVO(getShareInfoCommon(shareId));
    }

    /**
     * 获取分享文件信息
     *
     * @param shareId
     * @return
     */
    private ShareInfoVO getShareInfoCommon(String shareId) {
        FileShare share = fileShareService.getFileShareByShareId(shareId);
        // 为空或者超过了过期时间
        if (null == share || (share.getExpireTime() != null && new Date().after(share.getExpireTime()))) {
            throw new BusinessException(ResponseCodeEnum.CODE_902.getMsg());
        }
        ShareInfoVO shareInfoVO = CopyTools.copy(share, ShareInfoVO.class);
        FileInfo fileInfo = fileInfoService.getFileInfoByFileIdAndUserId(share.getFileId(), share.getUserId());
        // 不存在或者不在使用中
        if (fileInfo == null || !FileDelFlagEnums.USING.getFlag().equals(fileInfo.getDelFlag())) {
            throw new BusinessException(ResponseCodeEnum.CODE_902.getMsg());
        }
        shareInfoVO.setFileName(fileInfo.getFileName());
        UserInfo userInfo = userInfoService.getUserInfoByUserId(share.getUserId());
        shareInfoVO.setNickName(userInfo.getNickName());
        shareInfoVO.setAvatar(userInfo.getQqAvatar());
        shareInfoVO.setUserId(userInfo.getUserId());
        return shareInfoVO;
    }

    /**
     * 校验提取码
     *
     * @param session
     * @param shareId
     * @param code
     * @return
     */
    @RequestMapping("/checkShareCode")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public ResponseVO checkShareCode(HttpSession session, @VerifyParam(required = true) String shareId,
        @VerifyParam(required = true) String code) {
        SessionShareDto sessionShareDto = fileShareService.checkShareCode(shareId, code);
        session.setAttribute(Constants.SESSION_SHARE_KEY + shareId, sessionShareDto);
        return getSuccessResponseVO(null);
    }

    /**
     * 加载分享文件列表
     *
     * @param httpSession
     * @param shareId
     * @param filePid
     * @return
     */
    @RequestMapping("/loadFileList")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public ResponseVO loadFileList(HttpSession httpSession, @VerifyParam(required = true) String shareId,
        String filePid) {
        SessionShareDto sessionShareDto = checkShare(httpSession, shareId);
        FileInfoQuery query = new FileInfoQuery();
        // 不为空且不为根目录
        if (!StringTools.isEmpty(filePid) && !Constants.ZERO_STR.equals(filePid)) {
            // 保证不能越权，例如分享某个目录，不能查看到其他目录
            fileInfoService.checkRootFilePid(sessionShareDto.getFileId(), sessionShareDto.getShareUserId(), filePid);
            query.setFilePid(filePid);
        } else {
            query.setFileId(sessionShareDto.getFileId());
        }
        // 用户信息
        query.setUserId(sessionShareDto.getShareUserId());
        // 默认倒序排列
        query.setOrderBy("last_update_time desc");
        query.setDelFlag(FileDelFlagEnums.USING.getFlag());
        // VO 其实就是 PO 的缩略版，不需要那么多东西在里面
        PaginationResultVO result = fileInfoService.findListByPage(query);
        // FileInfo 转为 FileInfoVO
        return getSuccessResponseVO(convert2PaginationVO(result, FileInfoVO.class));
    }

    /**
     * 校验分享以及提取码是否过期
     *
     * @param session
     * @param shareId
     * @return
     */
    private SessionShareDto checkShare(HttpSession session, String shareId) {
        SessionShareDto sessionShareDto = getSessionShareFromSession(session, shareId);
        if (sessionShareDto == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_903);
        }
        if (sessionShareDto.getExpireTime() != null && new Date().after(sessionShareDto.getExpireTime())) {
            throw new BusinessException(ResponseCodeEnum.CODE_902);
        }
        return sessionShareDto;
    }

    /**
     * 获取文件夹信息（点击文件夹调用）
     *
     * @param session
     * @param shareId
     * @param path
     * @return
     */
    @RequestMapping("/getFolderInfo")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public ResponseVO getFolderInfo(HttpSession session, @VerifyParam(required = true) String shareId,
        @VerifyParam(required = true) String path) {
        SessionShareDto shareDto = checkShare(session, shareId);
        return super.getFolderInfo(path, shareDto.getShareUserId());
    }

    /**
     * 其他文件预览
     *
     * @param response
     * @param session
     * @param shareId
     * @param fileId
     */
    @RequestMapping("/getFile/{shareId}/{fileId}")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public void getFile(HttpServletResponse response, HttpSession session,
        @PathVariable("shareId") @VerifyParam(required = true) String shareId,
        @PathVariable("fileId") @VerifyParam(required = true) String fileId) {
        SessionShareDto shareDto = checkShare(session, shareId);
        super.getFile(response, fileId, shareDto.getShareUserId());
    }

    /**
     * 预览视频、图片
     *
     * @param request
     * @param response
     * @param session
     * @param shareId
     * @param fileId
     */
    @RequestMapping("/ts/getVideoInfo/{shareId}/{fileId}")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public void getVideoInfo(HttpServletRequest request, HttpServletResponse response, HttpSession session,
        @PathVariable("shareId") @VerifyParam(required = true) String shareId,
        @PathVariable("fileId") @VerifyParam(required = true) String fileId) {
        SessionShareDto shareDto = checkShare(session, shareId);
        super.getFile(response, fileId, shareDto.getShareUserId());
    }

    /**
     * 创建下载链接
     *
     * @param session
     * @param shareId
     * @param fileId
     * @return
     */
    @RequestMapping("/createDownloadUrl/{shareId}/{fileId}")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public ResponseVO createDownloadUrl(HttpSession session,
        @PathVariable("shareId") @VerifyParam(required = true) String shareId,
        @VerifyParam(required = true) @PathVariable("fileId") String fileId) {
        SessionShareDto shareDto = checkShare(session, shareId);
        return super.createDownloadUrl(fileId, shareDto.getShareUserId());
    }

    /**
     * 下载
     *
     * @param request
     * @param response
     * @param code
     * @throws Exception
     */
    @RequestMapping("/download/{code}")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    @Override
    public void download(HttpServletRequest request, HttpServletResponse response,
        @VerifyParam(required = true) @PathVariable("code") String code) throws Exception {
        super.download(request, response, code);
    }

    /**
     * 保存分享
     *
     * @param session
     * @param shareId
     * @param shareFileIds
     * @param myFolderId
     * @return
     */
    @RequestMapping("/saveShare")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public ResponseVO saveShare(HttpSession session, @VerifyParam(required = true) String shareId,
        @VerifyParam(required = true) String shareFileIds, @VerifyParam(required = true) String myFolderId) {
        SessionShareDto shareSessionDto = checkShare(session, shareId);
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        // 不能保存自己分享的文件
        if (shareSessionDto.getShareUserId().equals(webUserDto.getUserId())) {
            throw new BusinessException("自己分享的文件无法保存到自己的网盘");
        }
        fileInfoService.saveShare(shareSessionDto.getFileId(), shareFileIds, myFolderId,
            shareSessionDto.getShareUserId(), webUserDto.getUserId());
        return getSuccessResponseVO(null);
    }
}
