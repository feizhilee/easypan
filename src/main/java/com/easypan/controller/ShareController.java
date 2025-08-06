package com.easypan.controller;

import com.easypan.annotation.GlobalInterceptor;
import com.easypan.annotation.VerifyParam;
import com.easypan.entity.dto.SessionWebUserDto;
import com.easypan.entity.po.FileShare;
import com.easypan.entity.query.FileShareQuery;
import com.easypan.entity.vo.PaginationResultVO;
import com.easypan.entity.vo.ResponseVO;
import com.easypan.service.FileShareService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@RestController("/shareController")
@RequestMapping("/share")
public class ShareController extends ABaseController {
    @Resource
    private FileShareService fileShareService;

    /**
     * 加载分享列表
     *
     * @param session
     * @param query
     * @return
     */
    @RequestMapping("/loadShareList")
    @GlobalInterceptor
    public ResponseVO loadShareList(HttpSession session, FileShareQuery query) {
        query.setOrderBy("share_time desc");
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        query.setUserId(webUserDto.getUserId());
        // 因为文件名可以更改，所以 share 表中没有存文件名，需要联合查询
        query.setQueryFileName(true);
        PaginationResultVO result = fileShareService.findListByPage(query);
        return getSuccessResponseVO(result);
    }

    /**
     * 分享文件
     *
     * @param session
     * @param fileId
     * @param validType
     * @param code，若前端传了则使用，若没传则系统生成
     * @return
     */
    @RequestMapping("/shareFile")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO shareFile(HttpSession session, @VerifyParam(required = true) String fileId,
        @VerifyParam(required = true) Integer validType, String code) {
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        FileShare share = new FileShare();
        share.setValidType(validType);
        share.setCode(code);
        share.setUserId(webUserDto.getUserId());
        share.setFileId(fileId);
        fileShareService.saveShare(share);
        return getSuccessResponseVO(share);
    }

    /**
     * 取消分享
     *
     * @param session
     * @param shareIds
     * @return
     */
    @RequestMapping("/cancelShare")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO cancelShare(HttpSession session, @VerifyParam(required = true) String shareIds) {
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        fileShareService.deleteFileShareBatch(shareIds.split(","), webUserDto.getUserId());
        return getSuccessResponseVO(null);
    }

}
