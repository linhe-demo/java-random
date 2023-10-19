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
 * @since 2023-10-17
 */
@TableName("date_config")
@Data
public class DateConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 私人相册id
     */
    private Long personAlbumId;

    /**
     * 相册描述
     */
    private String date;

    /**
     * 创建时间
     */
    private Date createTime;
}
