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
 * @since 2024-03-19
 */
@Data
@TableName("baby_life_data")
public class BabyLifeData implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String text;

    /**
     * 记录时间
     */
    private Date date;

    /**
     * 私人相册id
     */
    private Long personAlbumId;

    /**
     * 创建时间
     */
    private Date createAt;
}
