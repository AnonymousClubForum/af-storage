package org.anonymous.af.controller;

import jakarta.annotation.Resource;
import org.anonymous.af.common.BaseResponse;
import org.anonymous.af.constants.FileType;
import org.anonymous.af.model.request.UploadImageRequest;
import org.anonymous.af.model.response.UploadImageResponse;
import org.anonymous.af.service.FileService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


/**
 * 文件接口控制器
 */
@Controller
@RequestMapping("/file")
public class FileController {

    @Resource
    private FileService fileService;

    /**
     * 通用文件上传
     */
    @PostMapping("/upload/file")
    public BaseResponse<Long> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            return BaseResponse.success(fileService.uploadFile(file, FileType.OTHER));
        } catch (Exception e) {
            return BaseResponse.error("文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 图片上传
     */
    @PostMapping("/upload/image")
    public BaseResponse<UploadImageResponse> uploadImage(@RequestBody UploadImageRequest request) {
        try {
            return BaseResponse.success(fileService.uploadImage(request));
        } catch (Exception e) {
            return BaseResponse.error("图片上传失败：" + e.getMessage());
        }
    }
}