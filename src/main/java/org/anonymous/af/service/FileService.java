package org.anonymous.af.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.anonymous.af.constants.FileType;
import org.anonymous.af.model.entity.FileEntity;
import org.anonymous.af.model.request.UploadImageRequest;
import org.anonymous.af.model.response.UploadImageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * 文件服务接口
 */
public interface FileService extends IService<FileEntity> {
    /**
     * 通用文件上传
     */
    Long uploadFile(MultipartFile file, FileType fileType) throws IOException;

    /**
     * 上传图片，同时生成缩略图
     */
    UploadImageResponse uploadImage(UploadImageRequest request) throws IOException;

    /**
     * 获取文件
     */
    InputStream getFileInputStream(FileEntity entity);
}