package com.easypan.utils;

import com.easypan.entity.enums.VerifyRegexEnum;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 验证正则
 */
public class VerifyUtils {
    public static Boolean verify(String regs, String value) {
        if (StringTools.isEmpty(value)) {
            return false;
        }
        Pattern pattern = Pattern.compile(regs);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    public static Boolean verify(VerifyRegexEnum verifyRegexEnum, String value) {
        return verify(verifyRegexEnum.getRegex(), value);
    }
}
