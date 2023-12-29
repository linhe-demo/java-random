package com.example.random.domain.value;

import lombok.Data;

@Data
public class CalendarInfo {
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
}
