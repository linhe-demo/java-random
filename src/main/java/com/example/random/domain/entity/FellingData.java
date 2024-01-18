package com.example.random.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 用户心情配置表
 * </p>
 *
 * @author muhe
 * @since 2024-01-08
 */
@Data
@TableName("felling_data")
public class FellingData implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户关联id
     */
    private Long personId;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String text;

    /**
     * 启用状态 1：未启用 2: 启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
