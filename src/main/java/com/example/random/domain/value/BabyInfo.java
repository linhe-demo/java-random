package com.example.random.domain.value;

import lombok.Data;

@Data
public class BabyInfo {
    /**
     * 小名
     */
    private String nickName;

    /**
     * 学名
     */
    private String realName;

    /**
     * 性别
     */
    private String sexy;

    /**
     * 年龄
     */
    private String age;

    /**
     * 生日
     */
    private String birthday;
}
