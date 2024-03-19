package com.example.random.interfaces.controller.put.response.life;


import lombok.Data;

@Data
public class GoodsResponse {
    /**
     * 食物名字
     */
    private String name;

    /**
     * 危险等级
     */
    private Integer level;

    /**
     * 危险原因
     */
    private String reason;
}
