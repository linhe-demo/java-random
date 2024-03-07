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
 * 
 * </p>
 *
 * @author muhe
 * @since 2024-03-07
 */
@Data
@TableName("extension_data")
public class ExtensionData implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 数据类型 1：怀孕知识
     */
    private Short type;

    /**
     * 数据
     */
    private String value;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 更新时间
     */
    private Date updateDate;
}
