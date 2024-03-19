package com.example.random.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author muhe
 * @since 2024-03-14
 */
@Data
@TableName("food_reason")
public class FoodReason implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 食物类型id
     */
    private Integer typeId;

    /**
     * 原因
     */
    private String reason;
}
