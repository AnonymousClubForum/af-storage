package org.anonymous.af.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anonymous.af.config.AfProperties;
import org.anonymous.af.entity.FileEntity;
import org.anonymous.af.exception.AfException;
import org.anonymous.af.mapper.FileMapper;
import org.anonymous.af.service.FileService;
import org.apache.opendal.Operator;
import org.apache.opendal.OperatorOutputStream;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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
    @Resource
    private Operator operator;

    /**
     * 生成唯一文件名（UUID）
     *
     * @param fileName 原始文件名
     * @return 生成的文件名
     */
    private String generateFileName(String fileName) {
        return IdUtil.fastSimpleUUID() + FileUtil.getSuffix(fileName);
    }

    private String getFullPath(String fileName) {
        return afProperties.getRootSavePath() + File.separator + fileName;
    }

    /**
     * 存储文件
     *
     * @param path        存储路径
     * @param inputStream 文件输入流
     */
    private void storageFile(String path, InputStream inputStream) {
        OperatorOutputStream os = operator.createOutputStream(path);
        IoUtil.copy(inputStream, os);
    }

    /**
     * 通用文件上传
     */
    @Override
    public Long uploadFile(MultipartFile file) throws IOException {
        if (file.isEmpty() || StrUtil.isBlank(file.getOriginalFilename())) {
            throw new AfException("上传文件为空");
        }
        // 保存文件
        String fileName = generateFileName(file.getOriginalFilename());
        String filePath = getFullPath(fileName);
        storageFile(filePath, file.getInputStream());

        FileEntity fileEntity = new FileEntity();
        fileEntity.setId(IdWorker.getId());
        fileEntity.setFileName(fileName);
        save(fileEntity);

        return fileEntity.getId();
    }

    @Override
    public InputStream getFileInputStream(FileEntity entity) {
        String fileName = entity.getFileName();
        String filePath = getFullPath(fileName);
        return operator.createInputStream(filePath);
    }
}