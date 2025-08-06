package com.easypan.service;

import com.easypan.entity.dto.SessionWebUserDto;
import com.easypan.entity.po.UserInfo;
import com.easypan.entity.query.UserInfoQuery;
import com.easypan.entity.vo.PaginationResultVO;

import java.util.List;

/**
 * @Description: 用户信息Service
 * @Author: YoshuaLee
 * @Date: 2025/04/21
 */
public interface UserInfoService {
    /**
     * 根据条件查询列表
     */
    List<UserInfo> findListByParam(UserInfoQuery query);

    /**
     * 根据条件查询数量
     */
    Integer findCountByParam(UserInfoQuery query);

    /**
     * 分页查询
     */
    PaginationResultVO<UserInfo> findListByPage(UserInfoQuery query);

    /**
     * 新增
     */
    Integer add(UserInfo bean);

    /**
     * 批量新增
     */
    Integer addBatch(List<UserInfo> listBean);

    /**
     * 批量新增或修改
     */
    Integer addOrUpdateBatch(List<UserInfo> listBean);

    /**
     * 根据 UserId 查询
     */
    UserInfo getUserInfoByUserId(String userId);

    /**
     * 根据 UserId 更新
     */
    Integer updateUserInfoByUserId(UserInfo bean, String userId);

    /**
     * 根据 UserId 删除
     */
    Integer deleteUserInfoByUserId(String userId);

    /**
     * 根据 Email 查询
     */
    UserInfo getUserInfoByEmail(String email);

    /**
     * 根据 Email 更新
     */
    Integer updateUserInfoByEmail(UserInfo bean, String email);

    /**
     * 根据 Email 删除
     */
    Integer deleteUserInfoByEmail(String email);

    /**
     * 根据 QqOpenId 查询
     */
    UserInfo getUserInfoByQqOpenId(String qqOpenId);

    /**
     * 根据 QqOpenId 更新
     */
    Integer updateUserInfoByQqOpenId(UserInfo bean, String qqOpenId);

    /**
     * 根据 QqOpenId 删除
     */
    Integer deleteUserInfoByQqOpenId(String qqOpenId);

    /**
     * 根据 NickName 查询
     */
    UserInfo getUserInfoByNickName(String nickName);

    /**
     * 根据 NickName 更新
     */
    Integer updateUserInfoByNickName(UserInfo bean, String nickName);

    /**
     * 根据 NickName 删除
     */
    Integer deleteUserInfoByNickName(String nickName);

    /**
     * 注册用户
     *
     * @param email
     * @param nickName
     * @param password
     * @param emailCode
     */
    void register(String email, String nickName, String password, String emailCode);

    /**
     * 登录
     *
     * @param email
     * @param password
     * @return
     */
    SessionWebUserDto login(String email, String password);

    /**
     * 重置密码
     *
     * @param email
     * @param password
     * @param emailCode
     */
    void resetPwd(String email, String password, String emailCode);

    /**
     * QQ 登录
     *
     * @param code：QQ 登录回调传过来的
     */
    SessionWebUserDto qqLogin(String code);

    /**
     * 更新用户状态
     *
     * @param userId
     * @param status
     */
    void updateUserStatus(String userId, Integer status);

    /**
     * 修改用户空间
     *
     * @param userId
     * @param changeSpace
     */
    void changeUserSpace(String userId, Integer changeSpace);
}