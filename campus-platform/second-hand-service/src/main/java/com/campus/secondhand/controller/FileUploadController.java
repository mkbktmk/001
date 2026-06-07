package com.campus.secondhand.controller;

import com.campus.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 通用图片上传 — 将文件保存到本地 uploads 目录，返回访问 URL
 */
@Slf4j
@Tag(name = "图片上传接口")
@RestController
@RequestMapping("/second-hand")
public class FileUploadController {

    @Value("${upload.path:uploads}")
    private String uploadPath;

    @Value("${upload.url-prefix:/uploads}")
    private String urlPrefix;

    /** 允许的图片类型 */
    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp", "image/bmp"
    );
    private static final long MAX_SIZE = 5 * 1024 * 1024; // 5MB

    @Operation(summary = "上传图片（支持多文件）")
    @PostMapping("/upload")
    public Result<List<String>> upload(@RequestParam("files") MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return Result.badRequest("请选择图片");
        }

        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            // 校验类型
            if (!ALLOWED_TYPES.contains(file.getContentType())) {
                return Result.badRequest("仅支持 JPG/PNG/GIF/WebP/BMP 格式");
            }
            // 校验大小
            if (file.getSize() > MAX_SIZE) {
                return Result.badRequest("单张图片不能超过 5MB");
            }

            try {
                String url = saveFile(file);
                urls.add(url);
            } catch (IOException e) {
                log.error("图片保存失败", e);
                return Result.fail("图片上传失败，请稍后重试");
            }
        }
        return Result.ok("上传成功", urls);
    }

    private String saveFile(MultipartFile file) throws IOException {
        // 按日期分目录：uploads/secondhand/2024-01-01/
        String dateDir = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        // 确保使用绝对路径
        Path basePath = new File(uploadPath).getAbsoluteFile().toPath();
        Path dir = basePath.resolve("secondhand").resolve(dateDir);
        Files.createDirectories(dir);

        // 生成唯一文件名
        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }
        String newName = UUID.randomUUID().toString().replace("-", "") + ext;

        // 保存
        Path target = dir.resolve(newName);
        file.transferTo(target.toFile());

        // 返回访问 URL
        return urlPrefix + "/secondhand/" + dateDir + "/" + newName;
    }
}
