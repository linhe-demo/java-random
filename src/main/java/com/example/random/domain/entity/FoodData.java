package com.example.random.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author muhe
 * @since 2024-03-14
 */
@Data
@TableName("food_data")
public class FoodData implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 食物类型
     */
    private Integer type;

    /**
     * 食物名
     */
    private String foodName;

    /**
     * 状态 1：可吃 2：适量 3：可吃
     */
    private Integer status;

    /**
     * 记录创建时间
     */
    private Date createAt;
}
