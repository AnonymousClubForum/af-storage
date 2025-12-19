package org.anonymous.af.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.anonymous.af.config.AfProperties;
import org.anonymous.af.constants.FileType;
import org.anonymous.af.exception.AfException;
import org.anonymous.af.mapper.FileMapper;
import org.anonymous.af.model.entity.FileEntity;
import org.anonymous.af.model.request.UploadImageRequest;
import org.anonymous.af.model.response.UploadImageResponse;
import org.anonymous.af.service.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 文件服务实现类
 */
@Slf4j
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, FileEntity> implements FileService {
    @Resource
    private AfProperties afProperties;

    /**
     * 生成唯一文件名（UUID）
     *
     * @param fileName 原始文件名
     * @return 生成的文件名
     */
    private String generateFileName(String fileName) {
        return IdUtil.fastSimpleUUID() + FileUtil.getSuffix(fileName);
    }

    private String getFullPath(String fileName, FileType fileType) {
        if (fileType.equals(FileType.IMAGE)) {
            return afProperties.getRootSavePath() + File.separator
                    + afProperties.getImageSavePath() + File.separator + fileName;
        } else if (fileType.equals(FileType.THUMBNAIL)) {
            return afProperties.getRootSavePath() + File.separator
                    + afProperties.getThumbnailSavePath() + File.separator + fileName;
        } else {
            return afProperties.getRootSavePath() + File.separator + fileName;
        }
    }

    /**
     * 存储文件
     *
     * @param path        存储路径
     * @param inputStream 文件输入流
     */
    private void storageFile(String path, InputStream inputStream) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(path)) {
            byte[] b = new byte[inputStream.available()];
            while (inputStream.read(b) != -1)
                fileOutputStream.write(b);
            inputStream.close();
            fileOutputStream.flush();
        } catch (Exception e) {
            log.error("存储文件失败：", e);
            throw new AfException("存储文件失败：" + e.getMessage());
        }
    }

    /**
     * 通用文件上传
     */
    @Override
    public Long uploadFile(MultipartFile file, FileType fileType) throws IOException {
        if (file.isEmpty() || StrUtil.isBlank(file.getOriginalFilename())) {
            throw new AfException("上传文件为空");
        }
        // 保存文件
        String fileName = generateFileName(file.getOriginalFilename());
        String filePath = getFullPath(fileName, fileType);
        storageFile(filePath, file.getInputStream());

        FileEntity fileEntity = new FileEntity();
        fileEntity.setId(IdWorker.getId());
        fileEntity.setFileName(fileName);
        save(fileEntity);

        return fileEntity.getId();
    }

    /**
     * 图片上传（生成缩略图）
     */
    @Override
    public UploadImageResponse uploadImage(UploadImageRequest request) throws IOException {
        Long imageId = uploadFile(request.getFile(), FileType.IMAGE);

        String fileName = generateFileName(request.getFile().getOriginalFilename() + "_thumbnail");
        String filePath = getFullPath(fileName, FileType.THUMBNAIL);

        Thumbnails.of(request.getFile().getInputStream())
                .size(request.getHeight(), request.getWidth())
                .keepAspectRatio(true) // 保持宽高比
                .toFile(filePath);

        FileEntity fileEntity = new FileEntity();
        fileEntity.setId(IdWorker.getId());
        fileEntity.setFileName(fileName);
        save(fileEntity);

        UploadImageResponse response = new UploadImageResponse();
        response.setImageId(imageId);
        response.setThumbnailId(fileEntity.getId());
        return response;
    }
}