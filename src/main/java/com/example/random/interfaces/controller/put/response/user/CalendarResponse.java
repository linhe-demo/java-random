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
}
