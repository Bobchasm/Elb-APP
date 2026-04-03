package com.tju.elm.notification.controller;

import com.tju.elm.notification.zoo.utils.TXCosUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import result.HttpResult;

import java.io.IOException;
import java.util.UUID;

@RestController
@Tag(name="文件上传")
public class FileUploadController {

    @Autowired
    private TXCosUtil txCosUtil;

    @PostMapping("/api/upload")
    @Operation(summary = "上传文件")
    public HttpResult<String> uploadFile(MultipartFile file) throws IOException {
        String originFileName = file.getOriginalFilename();
        String fileName = UUID.randomUUID().toString() + originFileName.substring(originFileName.lastIndexOf("."));
        String url= txCosUtil.uploadFile(fileName, file.getInputStream());
        // 处理文件上传逻辑
        return HttpResult.success(url);
    }
}
