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
@TableName("user_config")
public class UserConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 相册id
     */
    private Long personAlbumId;

    /**
     * 第一次见面时间
     */
    private Date firstMeetDate;

    /**
     * 领证时间
     */
    private Date certificateDate;

    /**
     * 结婚时间
     */
    private Date marryDate;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 更新时间
     */
    private Date updateDate;
}
