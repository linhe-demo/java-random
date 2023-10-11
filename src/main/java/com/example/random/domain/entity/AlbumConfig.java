package com.example.random.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
 * @since 2023-09-18
 */
@TableName("album_config")
@Data
public class AlbumConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 相册标题
     */
    private String title;

    /**
     * 相册描述
     */
    @TableField(value="`desc`")
    private String desc;

    /**
     * 相册时间
     */
    private Date date;

    /**
     * 状态 0：未启用 1：启用
     */
    private Integer status;

    /**
     * 私人相册id
     */
    private Long personAlbumId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
