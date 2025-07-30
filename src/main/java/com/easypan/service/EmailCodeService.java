package com.easypan.service;

import com.easypan.entity.po.EmailCode;
import com.easypan.entity.query.EmailCodeQuery;
import com.easypan.entity.vo.PaginationResultVO;

import java.util.List;

/**
 * @Description: 邮箱验证码Service
 * @Author: YoshuaLee
 * @Date: 2025/04/21
 */
public interface EmailCodeService {
    /**
     * 根据条件查询列表
     */
    List<EmailCode> findListByParam(EmailCodeQuery query);

    /**
     * 根据条件查询数量
     */
    Integer findCountByParam(EmailCodeQuery query);

    /**
     * 分页查询
     */
    PaginationResultVO<EmailCode> findListByPage(EmailCodeQuery query);

    /**
     * 新增
     */
    Integer add(EmailCode bean);

    /**
     * 批量新增
     */
    Integer addBatch(List<EmailCode> listBean);

    /**
     * 批量新增或修改
     */
    Integer addOrUpdateBatch(List<EmailCode> listBean);

    /**
     * 根据 EmailAndCode 查询
     */
    EmailCode getEmailCodeByEmailAndCode(String email, String code);

    /**
     * 根据 EmailAndCode 更新
     */
    Integer updateEmailCodeByEmailAndCode(EmailCode bean, String email, String code);

    /**
     * 根据 EmailAndCode 删除
     */
    Integer deleteEmailCodeByEmailAndCode(String email, String code);

    /**
     * 发送邮件验证码
     *
     * @param email
     * @param type
     */
    void sendEmailCode(String email, Integer type);

    /**
     * 校验邮箱验证码
     *
     * @param email
     * @param code
     */
    void checkEmailCode(String email, String code);
}