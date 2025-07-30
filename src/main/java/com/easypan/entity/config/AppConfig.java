package com.easypan.entity.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("appConfig")
public class AppConfig {
    // 该注解可以读取 properties 文件的值注入到变量中
    @Value("${spring.mail.username:}")
    private String sendUsername;

    @Value("${admin.emails:}")
    private String adminEmails;

    @Value("${project.folder}")
    private String projectFolder;

    // QQ 登录相关
    @Value("${qq.app.id:}")
    private String qqAppId;

    @Value("${qq.app.key:}")
    private String qqAppKey;

    @Value("${qq.url.authorization:}")
    private String qqUrlAuthorization;

    @Value("${qq.url.access.token:}")
    private String qqUrlAccessToken;

    @Value("${qq.url.openid:}")
    private String qqUrlOpenId;

    @Value("${qq.url.user.info:}")
    private String qqUrlUserInfo;

    @Value("${qq.url.redirect:}")
    private String qqUrlRedirect;

    public String getAdminEmails() {
        return adminEmails;
    }

    public String getSendUsername() {
        return sendUsername;
    }

    public String getProjectFolder() {
        return projectFolder;
    }

    public String getQqAppId() {
        return qqAppId;
    }

    public String getQqAppKey() {
        return qqAppKey;
    }

    public String  getQqUrlAuthorization() {
        return qqUrlAuthorization;
    }

    public String getQqUrlAccessToken() {
        return qqUrlAccessToken;
    }

    public String getQqUrlOpenId() {
        return qqUrlOpenId;
    }

    public String getQqUrlUserInfo() {
        return qqUrlUserInfo;
    }

    public String getQqUrlRedirect() {
        return qqUrlRedirect;
    }

}
