package com.example.random.interfaces.controller.put.response.user;


import lombok.Data;

@Data
public class UserFeatureResponse {

    /**
     * 欢迎语
     */
    private String welcomeStr;

    /**
     * 预产期
     */
    private String dueDate;

    /**
     * 当前属于孕几周
     */
    private String pregnantWeeks;

    /**
     * 孕周详情
     */
    private String pregnantWeeksDetail;

    /**
     * 底部按钮1
     */
    private String btn1;

    /**
     * 底部按钮2
     */
    private String btn2;

    /**
     * 底部按钮3
     */
    private String btn3;

    /**
     * 孕第几周
     */
    private Integer week;
}
