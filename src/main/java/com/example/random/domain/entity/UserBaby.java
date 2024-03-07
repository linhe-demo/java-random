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
@TableName("user_baby")
@Data
public class UserBaby implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录iD
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 相册id
     */
    private Long personAlbumId;

    /**
     * baby 小名
     */
    private String nickName;

    /**
     * baby 真实姓名
     */
    private String realName;

    /**
     * baby 性别 1:男性 2:女性
     */
    private Integer sexy;

    /**
     * baby 是否出生 1：已出生 2：未出生
     */
    private Integer status;
    /**
     * baby 照片
     */
    private String imageUrl;

    /**
     * baby 出生日期
     */
    private Date birthday;

    /**
     * baby 怀孕日期
     */
    private Date pregnancyDate;

    /**
     * 创建日期
     */
    private Date createDate;

    /**
     * 更新日期
     */
    private Date updateDate;
}
