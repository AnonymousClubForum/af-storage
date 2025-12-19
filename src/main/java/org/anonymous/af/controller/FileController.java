package org.anonymous.af.controller;

import org.anonymous.af.common.BaseResponse;
import org.anonymous.af.constants.FileType;
import org.anonymous.af.model.entity.FileEntity;
import org.anonymous.af.model.request.UploadImageRequest;
import org.anonymous.af.model.response.UploadImageResponse;
import org.anonymous.af.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


/**
 * 文件接口控制器
 */
@Controller
@RequestMapping("/file")
public class FileController {

    @Autowired
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

    /**
     * 文件下载
     */
    @GetMapping("/download")
    public ResponseEntity<?> downloadFile(@RequestParam("file_id") Long fileId) {
        FileEntity entity = fileService.getById(fileId);
        if (entity == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"")
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new InputStreamResource(fileService.getFileInputStream(entity)));
    }
}