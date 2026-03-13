package com.tju.elm.notification.zoo.utils;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import exception.APIException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import result.ResultCodeEnum;

import java.io.InputStream;
import java.util.Date;

@Slf4j
@Component
public class TXCosUtil {
    @Value("${txcos.secretId}")
    private String secretId;

    @Value("${txcos.secretKey}")
    private String secretKey;

    @Value("${txcos.region}")
    private String region;

    @Value("${txcos.bucketName}")
    private String bucketName;

    @Value("${txcos.enableAccelerate:false}")
    private boolean enableAccelerate; // 是否启用全球加速

    private COSClient cosClient;

    @PostConstruct
    public void init() {

        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        ClientConfig clientConfig = new ClientConfig(new Region(region));
        clientConfig.setHttpProtocol(HttpProtocol.https);

        this.cosClient = new COSClient(cred, clientConfig);

        log.info("COS客户端初始化完成" + "加速模式配置: " + (enableAccelerate ? "已启用" : "未启用"));
        log.info("Bucket: " + bucketName + " Region: " + region);
    }

    /**
     * 上传文件并返回可访问的URL
     * @param fileName 文件名（包含扩展名）
     * @param inputStream 文件输入流
     * @return 文件的完整访问URL
     */
    public String uploadFile(String fileName, InputStream inputStream) {
        return uploadFile(fileName, inputStream, null, null);
    }

    /**
     * 上传文件并返回可访问的URL
     * @param fileName 文件名（包含扩展名）
     * @param inputStream 文件输入流
     * @param contentType 文件内容类型
     * @return 文件的完整访问URL
     */
    public String uploadFile(String fileName, InputStream inputStream, String contentType) {
        return uploadFile(fileName, inputStream, contentType, null);
    }

    /**
     * 上传文件并返回可访问的URL
     * @param fileName 文件名（包含扩展名）
     * @param inputStream 文件输入流
     * @param contentType 文件内容类型
     * @param contentLength 文件大小（字节数）
     * @return 文件的完整访问URL
     */
    public String uploadFile(String fileName, InputStream inputStream, String contentType, Long contentLength) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();

            if (contentType != null && !contentType.isEmpty()) {
                metadata.setContentType(contentType);
            }

            if (contentLength != null && contentLength > 0) {
                metadata.setContentLength(contentLength);
            }

            // 创建上传请求
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, inputStream, metadata);

            // 执行上传
            System.out.println("正在上传到COS...");
            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
            System.out.println("上传成功，ETag: " + putObjectResult.getETag());

            // 生成访问URL
            String url = generateFileUrl(fileName);
            return url;
        } catch (Exception e) {
            log.info("文件上传失败: {}", e.getMessage());
            e.printStackTrace();
            throw new APIException(ResultCodeEnum.UPLOAD_FAILED);
        } finally {
            // 确保输入流被关闭
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    // 忽略关闭异常
                }
            }
        }
    }

    /**
     * 生成文件访问URL
     * @param fileName 文件名
     * @return 完整的访问URL
     */
    public String generateFileUrl(String fileName) {
        String url;

        if (enableAccelerate) {
            // 全球加速域名格式：https://<bucket-name>.cos.accelerate.myqcloud.com/<key>
            url = String.format("https://%s.cos.accelerate.myqcloud.com/%s",
                    bucketName,
                    fileName);
        } else {
            // 普通域名格式：https://<bucket-name>.cos.<region>.myqcloud.com/<key>
            url = String.format("https://%s.cos.%s.myqcloud.com/%s",
                    bucketName,
                    region,
                    fileName);
        }

        return url;
    }

    /**
     * 生成预签名URL（如果文件是私有的）
     * @param fileName 文件名
     * @param expiryTime 过期时间（秒）
     * @return 预签名URL
     */
    public String generatePresignedUrl(String fileName, long expiryTime) {
        try {
            Date expiration = new Date(System.currentTimeMillis() + expiryTime * 1000);

            // 旧版本使用默认的GET方法
            return cosClient.generatePresignedUrl(bucketName, fileName, expiration, HttpMethodName.GET).toString();
        } catch (Exception e) {
            System.err.println("生成预签名URL失败: " + e.getMessage());
            throw new RuntimeException("生成预签名URL失败", e);
        }
    }

    /**
     * 删除文件
     * @param fileName 文件名
     */
    public void deleteFile(String fileName) {
        try {
            cosClient.deleteObject(bucketName, fileName);
            System.out.println("文件删除成功: " + fileName);
        } catch (Exception e) {
            System.err.println("删除文件失败: " + e.getMessage());
            throw new RuntimeException("删除文件失败", e);
        }
    }

    /**
     * 检查文件是否存在
     * @param fileName 文件名
     * @return 是否存在
     */
    public boolean fileExists(String fileName) {
        try {
            return cosClient.doesObjectExist(bucketName, fileName);
        } catch (Exception e) {
            System.err.println("检查文件是否存在失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 关闭COS客户端
     */
    public void shutdown() {
        if (cosClient != null) {
            cosClient.shutdown();
            System.out.println("COS客户端已关闭");
        }
    }

}