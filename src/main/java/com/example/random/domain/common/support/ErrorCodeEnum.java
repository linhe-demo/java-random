package com.example.random.domain.common.support;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 返回数据格式枚举
 *
 * @author muhe
 * @since 2023-09-11
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum ErrorCodeEnum implements CodeAndMsg {

    USER_NOT_EXIST(10001, "用户不存在"),
    WRONG_USER_PASSWORD(10002, "用户密码错误"),
    TOKEN_CANNOT_BE_EMPTY(10003, "token 不能为空"),
    TOKEN_PARSING_IS_INCORRECT(10004, "token 解析异常"),
    USER_TOKEN_IS_INCORRECT(10005, "用户token 验证失败"),
    USER_ALREADY_EXISTS(10006, "用户已存在！"),
    TOKEN_HAS_EXPIRED(10007, "token 已过期"),
    ACCOUNT_IS_NOT_ACTIVATED(10008, "账号未激活，请联系管理员激活！"),
    FILE_IS_EMPTY(10009, "文件不能为空！"),
    FILE_UPLOAD_FAIL(10010, "文件上传失败！"),
    UN_SUPPORT_IMAGE_TYPE(10011, "不支持的图片类型"),
    FAIL_HANDLE_FILE(10012, "压缩图片失败"),
    REGISTRATION_SUCCESS(10013, "注册成功， 请使用注册的账号登录"),
    FAIL_UPLOAD_QI_NIU(10014, "七牛文件上传失败！"),
    FAIL_READ_FILE(10016, "读取压缩文件失败！"),
    NO_PERMISSION(10017, "暂无权限请联系管理员"),
    FAIL_ADD_ALBUM(10018, "相册配置添加失败！"),
    WITHOUT_PERMISSION(10019, "您没有群贤查看该相册！"),
    FAIL_CREATE_FILE(10020, "文件夹创建失败！"),
    CONFIG_NOT_FOUND(10021, "配置不存在！"),
    USER_NOT_PERMISSION(10022, "用户没有权限操作！"),
    TITLE_NOT_EMPTY(10023, "标题不能为空！"),
    CONTENT_NOT_EMPTY(10024, "内容不能为空！"),
    ;
    private Integer code;
    private String msg;

    ErrorCodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }
}
