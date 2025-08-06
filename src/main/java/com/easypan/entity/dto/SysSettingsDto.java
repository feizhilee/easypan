package com.easypan.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

// 注解作用：忽略掉 json 字符串中有，而在 Java bean 中缺未定义的属性
@JsonIgnoreProperties(ignoreUnknown = true)
public class SysSettingsDto implements Serializable {
    /**
     * 注册发送邮件标题
     */
    private String registerEmailTitle = "邮箱验证码";

    /**
     * 注册发送邮件内容
     */
    private String registerMailContent = "您好，您的邮箱验证码是，%s，15 分钟有效";

    /**
     * 用户初始化空间大小 5M
     */
    private Integer userInitUseSpace = 5;

    public String getRegisterEmailTitle() {
        return registerEmailTitle;
    }

    public void setRegisterEmailTitle(String registerEmailTitle) {
        this.registerEmailTitle = registerEmailTitle;
    }

    public String getRegisterMailContent() {
        return registerMailContent;
    }

    public void setRegisterMailContent(String registerMailContent) {
        this.registerMailContent = registerMailContent;
    }

    public Integer getUserInitUseSpace() {
        return userInitUseSpace;
    }

    public void setUserInitUseSpace(Integer userInitUseSpace) {
        this.userInitUseSpace = userInitUseSpace;
    }

}
