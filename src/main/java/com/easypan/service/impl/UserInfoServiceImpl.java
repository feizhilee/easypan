package com.easypan.service.impl;

import com.easypan.component.RedisComponent;
import com.easypan.entity.config.AppConfig;
import com.easypan.entity.constants.Constants;
import com.easypan.entity.dto.QQInfoDto;
import com.easypan.entity.dto.SessionWebUserDto;
import com.easypan.entity.dto.SysSettingsDto;
import com.easypan.entity.dto.UserSpaceDto;
import com.easypan.entity.enums.PageSize;
import com.easypan.entity.enums.UserStatusEnum;
import com.easypan.entity.po.FileInfo;
import com.easypan.entity.po.UserInfo;
import com.easypan.entity.query.FileInfoQuery;
import com.easypan.entity.query.SimplePage;
import com.easypan.entity.query.UserInfoQuery;
import com.easypan.entity.vo.PaginationResultVO;
import com.easypan.exception.BusinessException;
import com.easypan.mappers.FileInfoMapper;
import com.easypan.mappers.UserInfoMapper;
import com.easypan.service.EmailCodeService;
import com.easypan.service.UserInfoService;
import com.easypan.utils.JsonUtils;
import com.easypan.utils.OKHttpUtils;
import com.easypan.utils.StringTools;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description: 用户信息Service
 * @Author: YoshuaLee
 * @Date: 2025/04/21
 */
@Service("userInfoService")
public class UserInfoServiceImpl implements UserInfoService {

    private static final Logger logger = LoggerFactory.getLogger(UserInfoServiceImpl.class);

    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;

    @Resource
    private FileInfoMapper<FileInfo, FileInfoQuery> fileInfoMapper;

    @Resource
    private EmailCodeService emailCodeService;

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private AppConfig appConfig;

    /**
     * 根据条件查询列表
     */
    @Override
    public List<UserInfo> findListByParam(UserInfoQuery query) {
        return this.userInfoMapper.selectList(query);
    }

    /**
     * 根据条件查询数量
     */
    @Override
    public Integer findCountByParam(UserInfoQuery query) {
        return this.userInfoMapper.selectCount(query);
    }

    /**
     * 分页查询方法
     */
    @Override
    public PaginationResultVO<UserInfo> findListByPage(UserInfoQuery query) {
        Integer count = this.findCountByParam(query);
        Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
        SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
        query.setSimplePage(page);
        List<UserInfo> list = this.findListByParam(query);
        PaginationResultVO<UserInfo> result =
            new PaginationResultVO<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
        return result;
    }

    /**
     * 新增
     */
    @Override
    public Integer add(UserInfo bean) {
        return this.userInfoMapper.insert(bean);
    }

    /**
     * 批量新增
     */
    @Override
    public Integer addBatch(List<UserInfo> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.userInfoMapper.insertBatch(listBean);
    }

    /**
     * 批量新增或修改
     */
    @Override
    public Integer addOrUpdateBatch(List<UserInfo> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.userInfoMapper.insertOrUpdateBatch(listBean);
    }

    /**
     * 根据 UserId 查询
     */
    @Override
    public UserInfo getUserInfoByUserId(String userId) {
        return this.userInfoMapper.selectByUserId(userId);
    }

    /**
     * 根据 UserId 更新
     */
    @Override
    public Integer updateUserInfoByUserId(UserInfo bean, String userId) {
        return this.userInfoMapper.updateByUserId(bean, userId);
    }

    /**
     * 根据 UserId 删除
     */
    @Override
    public Integer deleteUserInfoByUserId(String userId) {
        return this.userInfoMapper.deleteByUserId(userId);
    }

    /**
     * 根据 Email 查询
     */
    @Override
    public UserInfo getUserInfoByEmail(String email) {
        return this.userInfoMapper.selectByEmail(email);
    }

    /**
     * 根据 Email 更新
     */
    @Override
    public Integer updateUserInfoByEmail(UserInfo bean, String email) {
        return this.userInfoMapper.updateByEmail(bean, email);
    }

    /**
     * 根据 Email 删除
     */
    @Override
    public Integer deleteUserInfoByEmail(String email) {
        return this.userInfoMapper.deleteByEmail(email);
    }

    /**
     * 根据 QqOpenId 查询
     */
    @Override
    public UserInfo getUserInfoByQqOpenId(String qqOpenId) {
        return this.userInfoMapper.selectByQqOpenId(qqOpenId);
    }

    /**
     * 根据 QqOpenId 更新
     */
    @Override
    public Integer updateUserInfoByQqOpenId(UserInfo bean, String qqOpenId) {
        return this.userInfoMapper.updateByQqOpenId(bean, qqOpenId);
    }

    /**
     * 根据 QqOpenId 删除
     */
    @Override
    public Integer deleteUserInfoByQqOpenId(String qqOpenId) {
        return this.userInfoMapper.deleteByQqOpenId(qqOpenId);
    }

    /**
     * 根据 NickName 查询
     */
    @Override
    public UserInfo getUserInfoByNickName(String nickName) {
        return this.userInfoMapper.selectByNickName(nickName);
    }

    /**
     * 根据 NickName 更新
     */
    @Override
    public Integer updateUserInfoByNickName(UserInfo bean, String nickName) {
        return this.userInfoMapper.updateByNickName(bean, nickName);
    }

    /**
     * 根据 NickName 删除
     */
    @Override
    public Integer deleteUserInfoByNickName(String nickName) {
        return this.userInfoMapper.deleteByNickName(nickName);
    }

    /**
     * 注册用户
     *
     * @param email
     * @param nickName
     * @param password
     * @param emailCode
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(String email, String nickName, String password, String emailCode) {
        UserInfo userInfo = this.userInfoMapper.selectByEmail(email);
        if (userInfo != null) {
            throw new BusinessException("邮箱账号已经存在");
        }
        UserInfo nickNameUserInfo = this.userInfoMapper.selectByNickName(nickName);
        if (nickNameUserInfo != null) {
            throw new BusinessException("昵称已经存在");
        }

        // 校验邮箱验证码，后面注册失败，验证码应仍有效，所以放在同一个事务里面
        emailCodeService.checkEmailCode(email, emailCode);

        String userId = StringTools.getRandomNumber(Constants.LENGTH_10);
        userInfo = new UserInfo();
        userInfo.setUserId(userId);
        userInfo.setNickName(nickName);
        // 存 md5，而不是密码本身
        userInfo.setPassword(StringTools.encodeByMD5(password));
        userInfo.setEmail(email);
        userInfo.setJoinTime(new Date());
        userInfo.setStatus(UserStatusEnum.ENABLE.getStatus());
        userInfo.setUseSpace(0L);
        SysSettingsDto sysSettingsDto = redisComponent.getSysSettingsDto();
        userInfo.setTotalSpace(sysSettingsDto.getUserInitUseSpace() * Constants.MB);
        this.userInfoMapper.insert(userInfo);
    }

    /**
     * 登录
     *
     * @param email
     * @param password
     * @return
     */
    @Override
    public SessionWebUserDto login(String email, String password) {
        UserInfo userInfo = this.userInfoMapper.selectByEmail(email);
        if (userInfo == null || !userInfo.getPassword().equals(password)) {
            throw new BusinessException("账号或者密码错误");
        }
        if (UserStatusEnum.DISABLE.getStatus().equals(userInfo.getStatus())) {
            throw new BusinessException("账号已禁用");
        }
        UserInfo updateInfo = new UserInfo();
        updateInfo.setLastLoginTime(new Date());
        // 下面这一句的 updateInfo 不小心写成了 userInfo（实际上确实应该是 updateInfo），
        // 随后发现生成的 xml 中的 set if 语句都漏了“,”，需要修改 easyjava 生成工具
        this.userInfoMapper.updateByEmail(updateInfo, email);
        SessionWebUserDto sessionWebUserDto = new SessionWebUserDto();
        sessionWebUserDto.setNickName(userInfo.getNickName());
        sessionWebUserDto.setUserId(userInfo.getUserId());
        // 如果是超级管理员
        if (ArrayUtils.contains(appConfig.getAdminEmails().split(","), email)) {
            sessionWebUserDto.setAdmin(true);
        } else {
            sessionWebUserDto.setAdmin(false);
        }
        // 用户空间
        UserSpaceDto userSpaceDto = new UserSpaceDto();
        Long useSpace = fileInfoMapper.selectUseSpace(userInfo.getUserId());
        userSpaceDto.setUseSpace(useSpace);
        userSpaceDto.setTotalSpace(userInfo.getTotalSpace());
        // 需要实时显示这些信息，所以需要使用 Redis 将信息保存到内存中
        redisComponent.saveUserSpaceUse(userInfo.getUserId(), userSpaceDto);
        return sessionWebUserDto;
    }

    /**
     * 重置密码
     *
     * @param email
     * @param newPassword
     * @param emailCode
     */
    @Override
    // 保证邮箱验证码和新密码在同一个事务里面
    @Transactional(rollbackFor = Exception.class)
    public void resetPwd(String email, String newPassword, String emailCode) {
        UserInfo userInfo = this.userInfoMapper.selectByEmail(email);
        if (userInfo == null) {
            throw new BusinessException("邮箱账号不存在");
        }
        // 校验不通过里面会 throw 的
        emailCodeService.checkEmailCode(email, emailCode);
        UserInfo updateInfo = new UserInfo();
        updateInfo.setPassword(StringTools.encodeByMD5(newPassword));
        this.userInfoMapper.updateByEmail(updateInfo, email);
    }

    /**
     * QQ 登录
     *
     * @param code：QQ 登录回调传过来的
     * @return
     */
    @Override
    public SessionWebUserDto qqLogin(String code) {
        // 第一步：通过回调 code 拿到 accessToken
        String accessToken = getQQAccessToken(code);
        // 第二步：通过 accessToken 拿到 QQOpenId
        String openId = getQQOpenId(accessToken);
        // 判断数据库中是否存在该用户
        UserInfo user = this.userInfoMapper.selectByQqOpenId(openId);
        String avatar = null;
        if (user == null) {
            // 若为空则自动注册
            // 第三步：通过 QQOpenId 拿到 QQ 基本信息
            QQInfoDto qqInfoDto = getQQUserInfo(accessToken, openId);
            user = new UserInfo();
            String nickName = qqInfoDto.getNickname();
            nickName = nickName.length() > Constants.LENGTH_20 ? nickName.substring(0, Constants.LENGTH_20) : nickName;
            avatar = StringTools.isEmpty(qqInfoDto.getFigureurl_qq_2()) ? qqInfoDto.getFigureurl_qq_1()
                : qqInfoDto.getFigureurl_qq_2();
            Date curDate = new Date();

            user.setQqOpenId(openId);
            user.setNickName(nickName);
            user.setJoinTime(curDate);
            user.setQqAvatar(avatar);
            user.setUserId(StringTools.getRandomString(Constants.LENGTH_10));
            user.setLastLoginTime(curDate);
            user.setStatus(UserStatusEnum.ENABLE.getStatus());
            user.setUseSpace(0L);
            user.setTotalSpace(redisComponent.getSysSettingsDto().getUserInitUseSpace() * Constants.MB);
            this.userInfoMapper.insert(user);
            user = userInfoMapper.selectByQqOpenId(openId);
        } else {
            // 若存在则更新信息
            UserInfo updateInfo = new UserInfo();
            updateInfo.setLastLoginTime(new Date());
            avatar = user.getQqAvatar();
            this.userInfoMapper.updateByQqOpenId(updateInfo, openId);
        }
        SessionWebUserDto sessionWebUserDto = new SessionWebUserDto();
        sessionWebUserDto.setUserId(user.getUserId());
        sessionWebUserDto.setAvatar(avatar);
        sessionWebUserDto.setNickName(user.getNickName());

        // 如果是超级管理员
        if (ArrayUtils.contains(appConfig.getAdminEmails().split(","),
            user.getEmail() == null ? "" : user.getEmail())) {
            sessionWebUserDto.setAdmin(true);
        } else {
            sessionWebUserDto.setAdmin(false);
        }

        UserSpaceDto userSpaceDto = new UserSpaceDto();
        Long useSpace = fileInfoMapper.selectUseSpace(user.getUserId());
        userSpaceDto.setUseSpace(useSpace);
        userSpaceDto.setTotalSpace(user.getTotalSpace());
        // 每次登陆的时候往缓存更新一下使用情况
        redisComponent.saveUserSpaceUse(user.getUserId(), userSpaceDto);
        return sessionWebUserDto;
    }

    private String getQQAccessToken(String code) {
        /**
         * 返回结果是字符串 access_token=*&expires_in=7776000&refresh_token=* 返回错误 callback({UcWebConstants
         * .VIEW_OBJ_RESULT_KEY:111,error_description:"error msg"})
         */
        String accessToken = null;
        String url = null;
        try {
            url = String.format(appConfig.getQqUrlAccessToken(), appConfig.getQqAppId(), appConfig.getQqAppKey(), code,
                URLEncoder.encode(appConfig.getQqUrlRedirect(), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            logger.error("encode失败");
        }
        String tokenResult = OKHttpUtils.getRequest(url);
        if (tokenResult == null || tokenResult.indexOf(Constants.VIEW_OBJ_RESULT_KEY) != -1) {
            logger.error("获取qqToken失败:{}", tokenResult);
            throw new BusinessException("获取qqToken失败");
        }
        String[] params = tokenResult.split("&");
        if (params != null && params.length > 0) {
            for (String p : params) {
                if (p.indexOf("access_token") != -1) {
                    accessToken = p.split("=")[1];
                    break;
                }
            }
        }
        return accessToken;
    }

    private String getQQOpenId(String accessToken) throws BusinessException {
        // 获取openId
        String url = String.format(appConfig.getQqUrlOpenId(), accessToken);
        String openIDResult = OKHttpUtils.getRequest(url);
        String tmpJson = this.getQQResp(openIDResult);
        if (tmpJson == null) {
            logger.error("调qq接口获取openID失败:tmpJson{}", tmpJson);
            throw new BusinessException("调qq接口获取openID失败");
        }
        Map jsonData = JsonUtils.convertJson2Obj(tmpJson, Map.class);
        if (jsonData == null || jsonData.containsKey(Constants.VIEW_OBJ_RESULT_KEY)) {
            logger.error("调qq接口获取openID失败:{}", jsonData);
            throw new BusinessException("调qq接口获取openID失败");
        }
        return String.valueOf(jsonData.get("openid"));
    }

    private String getQQResp(String result) {
        if (StringUtils.isNotBlank(result)) {
            int pos = result.indexOf("callback");
            if (pos != -1) {
                int start = result.indexOf("(");
                int end = result.lastIndexOf(")");
                String jsonStr = result.substring(start + 1, end - 1);
                return jsonStr;
            }
        }
        return null;
    }

    private QQInfoDto getQQUserInfo(String accessToken, String qqOpenId) throws BusinessException {
        String url = String.format(appConfig.getQqUrlUserInfo(), accessToken, appConfig.getQqAppId(), qqOpenId);
        String response = OKHttpUtils.getRequest(url);
        if (StringUtils.isNotBlank(response)) {
            QQInfoDto qqInfo = JsonUtils.convertJson2Obj(response, QQInfoDto.class);
            if (qqInfo.getRet() != 0) {
                logger.error("qqInfo:{}", response);
                throw new BusinessException("调qq接口获取用户信息异常");
            }
            return qqInfo;
        }
        throw new BusinessException("调qq接口获取用户信息异常");
    }

}