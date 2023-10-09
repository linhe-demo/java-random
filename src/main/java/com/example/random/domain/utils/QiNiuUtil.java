package com.example.random.domain.utils;

import com.example.random.domain.common.exception.NewException;
import com.example.random.domain.common.support.ErrorCodeEnum;
import com.example.random.domain.constant.CommonEnum;
import com.example.random.interfaces.client.vo.response.QiNiuResponse;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;

import java.io.File;


public class QiNiuUtil {
    public static String uploadToQiNiu(String filePath, String fileName) {
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.huadongZheJiang2());
        cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;// 指定分片上传版本

        UploadManager uploadManager = new UploadManager(cfg);

        String accessKey = CommonEnum.QI_NIU_ACCESS_KEY.getValue();
        String secretKey = CommonEnum.QI_NIU_SECRET_kEY.getValue();
        String bucket = CommonEnum.QI_NIU_BUCKET.getValue();
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        try {
            File file = new File(filePath);
            String key = "images/life/" + fileName;
            Response response = uploadManager.put(file, key, upToken);
            //解析上传成功的结果
            QiNiuResponse info = ToolsUtil.convertToObject(response.bodyString(), QiNiuResponse.class);
            return info.getKey();
        } catch (QiniuException ex) {
            throw new NewException(ErrorCodeEnum.FILE_UPLOAD_FAIL.getCode(), ex.response.toString());
        }
    }
}
