package com.easypan.mappers;

import org.apache.ibatis.annotations.Param;

/**
 * @Description: 用户信息 Mapper
 * @Author: YoshuaLee
 * @Date: 2025/04/21
 */
public interface UserInfoMapper<T, P> extends BaseMapper {

    /**
     * 根据 UserId 查询
     */
    T selectByUserId(@Param("userId") String userId);

    /**
     * 根据 UserId 更新
     */
    Integer updateByUserId(@Param("bean") T t, @Param("userId") String userId);

    /**
     * 根据 UserId 删除
     */
    Integer deleteByUserId(@Param("userId") String userId);

    /**
     * 根据 Email 查询
     */
    T selectByEmail(@Param("email") String email);

    /**
     * 根据 Email 更新
     */
    Integer updateByEmail(@Param("bean") T t, @Param("email") String email);

    /**
     * 根据 Email 删除
     */
    Integer deleteByEmail(@Param("email") String email);

    /**
     * 根据 QqOpenId 查询
     */
    T selectByQqOpenId(@Param("qqOpenId") String qqOpenId);

    /**
     * 根据 QqOpenId 更新
     */
    Integer updateByQqOpenId(@Param("bean") T t, @Param("qqOpenId") String qqOpenId);

    /**
     * 根据 QqOpenId 删除
     */
    Integer deleteByQqOpenId(@Param("qqOpenId") String qqOpenId);

    /**
     * 根据 NickName 查询
     */
    T selectByNickName(@Param("nickName") String nickName);

    /**
     * 根据 NickName 更新
     */
    Integer updateByNickName(@Param("bean") T t, @Param("nickName") String nickName);

    /**
     * 根据 NickName 删除
     */
    Integer deleteByNickName(@Param("nickName") String nickName);

    Integer updateUserSpace(@Param("userId") String userId, @Param("useSpace") Long useSpace,
        @Param("totalSpace") Long totalSpace);

}