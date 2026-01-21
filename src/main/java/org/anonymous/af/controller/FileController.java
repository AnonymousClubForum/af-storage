package org.anonymous.af.controller;

import org.anonymous.af.common.BaseResponse;
import org.anonymous.af.entity.FileEntity;
import org.anonymous.af.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;


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
    @PostMapping("/upload")
    public BaseResponse<Long> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            return BaseResponse.success(fileService.uploadFile(file));
        } catch (Exception e) {
            return BaseResponse.error("文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 文件下载
     */
    @GetMapping("/download")
    public ResponseEntity<?> downloadFile(@RequestParam("file_id") Long fileId) throws FileNotFoundException {
        FileEntity entity = fileService.getById(fileId);
        if (entity == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""
                        + new String(entity.getFileName().getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1) + "\"")
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new InputStreamResource(fileService.getFileInputStream(entity)));
    }
}