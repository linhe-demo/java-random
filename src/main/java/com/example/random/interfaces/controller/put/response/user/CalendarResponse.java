package com.example.random.interfaces.controller.put.response.user;

import lombok.Data;

@Data
public class CalendarResponse {
    /**
     * 阳历日期
     */
    private String solarDate;
    /**
     * 农历日期
     */
    private String lunarDate;

    /**
     * 农历节气
     */
    private String lunarTerm;

    /**
     * 阳历节日
     */
    private String solarFestival;

    /**
     * 农历节日
     */
    private String lunarFestival;

    /**
     * 结婚天数
     */
    private String marryDay;

    /**
     * 第一次见面到现在的天数
     */
    private String firstMeeting;

    /**
     * 领证到现在的天数
     */
    private String certificateDay;

    /**
     * 是否展示烟花秀
     */
    private Boolean firework;

    /**
     * 周年纪念
     */
    private String anniversary;

}
