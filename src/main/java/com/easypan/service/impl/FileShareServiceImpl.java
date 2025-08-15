package com.easypan.service.impl;

import com.easypan.entity.constants.Constants;
import com.easypan.entity.dto.SessionShareDto;
import com.easypan.entity.enums.PageSize;
import com.easypan.entity.enums.ResponseCodeEnum;
import com.easypan.entity.enums.ShareValidTypeEnums;
import com.easypan.entity.po.FileShare;
import com.easypan.entity.query.FileShareQuery;
import com.easypan.entity.query.SimplePage;
import com.easypan.entity.vo.PaginationResultVO;
import com.easypan.exception.BusinessException;
import com.easypan.mappers.FileShareMapper;
import com.easypan.service.FileShareService;
import com.easypan.utils.DateUtils;
import com.easypan.utils.StringTools;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Description: 分享信息Service
 * @Author: YoshuaLee
 * @Date: 2025/08/05
 */
@Service("fileShareService")
public class FileShareServiceImpl implements FileShareService {
    @Resource
    private FileShareMapper<FileShare, FileShareQuery> fileShareMapper;

    /**
     * 根据条件查询列表
     */
    @Override
    public List<FileShare> findListByParam(FileShareQuery query) {
        return this.fileShareMapper.selectList(query);
    }

    /**
     * 根据条件查询数量
     */
    @Override
    public Integer findCountByParam(FileShareQuery query) {
        return this.fileShareMapper.selectCount(query);
    }

    /**
     * 分页查询方法
     */
    @Override
    public PaginationResultVO<FileShare> findListByPage(FileShareQuery query) {
        Integer count = this.findCountByParam(query);
        Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
        SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
        query.setSimplePage(page);
        List<FileShare> list = this.findListByParam(query);
        PaginationResultVO<FileShare> result =
            new PaginationResultVO<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
        return result;
    }

    /**
     * 新增
     */
    @Override
    public Integer add(FileShare bean) {
        return this.fileShareMapper.insert(bean);
    }

    /**
     * 批量新增
     */
    @Override
    public Integer addBatch(List<FileShare> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.fileShareMapper.insertBatch(listBean);
    }

    /**
     * 批量新增或修改
     */
    @Override
    public Integer addOrUpdateBatch(List<FileShare> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.fileShareMapper.insertOrUpdateBatch(listBean);
    }

    /**
     * 根据 ShareId 查询
     */
    @Override
    public FileShare getFileShareByShareId(String shareId) {
        return this.fileShareMapper.selectByShareId(shareId);
    }

    /**
     * 根据 ShareId 更新
     */
    @Override
    public Integer updateFileShareByShareId(FileShare bean, String shareId) {
        return this.fileShareMapper.updateByShareId(bean, shareId);
    }

    /**
     * 根据 ShareId 删除
     */
    @Override
    public Integer deleteFileShareByShareId(String shareId) {
        return this.fileShareMapper.deleteByShareId(shareId);
    }

    /**
     * 记录分享文件
     *
     * @param share
     */
    @Override
    public void saveShare(FileShare share) {
        ShareValidTypeEnums typeEnums = ShareValidTypeEnums.getByType(share.getValidType());
        if (null == typeEnums) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        // 计算过期时间
        if (ShareValidTypeEnums.FOREVER != typeEnums) {
            share.setExpireTime(DateUtils.getAfterDate(typeEnums.getDays()));
        }
        Date curDate = new Date();
        share.setShareTime(curDate);
        if (StringTools.isEmpty(share.getCode())) {
            // 前端未传入分享码，生成一个
            share.setCode(StringTools.getRandomString(Constants.LENGTH_5));
        }
        share.setShareId(StringTools.getRandomString(Constants.LENGTH_20));
        share.setShowCount(0);
        this.fileShareMapper.insert(share);
    }

    /**
     * （批量）取消分享
     *
     * @param shareIdArray
     * @param userId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFileShareBatch(String[] shareIdArray, String userId) {
        Integer count = this.fileShareMapper.deleteFileShareBatch(shareIdArray, userId);
        if (count != shareIdArray.length) {
            // 不满足需要抛出异常并回滚
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
    }

    /**
     * 校验提取码
     *
     * @param shareId
     * @param code
     * @return
     */
    @Override
    public SessionShareDto checkShareCode(String shareId, String code) {
        FileShare share = this.fileShareMapper.selectByShareId(shareId);
        // 为空或者超过了过期时间
        if (null == share || (share.getExpireTime() != null && new Date().after(share.getExpireTime()))) {
            throw new BusinessException(ResponseCodeEnum.CODE_902.getMsg());
        }
        if (!share.getCode().equals(code)) {
            throw new BusinessException("提取码错误");
        }
        // 更新浏览次数
        // 不能先查再加1，单人可以，多人并发的情况下会导致脏读，所以要在数据库层面写
        // Integer showCount = share.getShowCount() + 1;
        // FileShare updateShare = new FileShare();
        // updateShare.setShowCount(showCount);
        // this.fileShareMapper.updateByShareId(updateShare, shareId);
        this.fileShareMapper.updateShareShowCount(shareId);
        SessionShareDto sessionShareDto = new SessionShareDto();
        sessionShareDto.setShareId(shareId);
        sessionShareDto.setShareUserId(share.getUserId());
        sessionShareDto.setFileId(share.getFileId());
        sessionShareDto.setExpireTime(share.getExpireTime());
        return sessionShareDto;
    }

}