package org.jwj.novelresource.service.impl;

import com.alibaba.cloud.commons.io.FileUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jwj.novelcommon.constants.ErrorCodeEnum;
import org.jwj.novelcommon.constants.SystemConfigConsts;
import org.jwj.novelcommon.resp.RestResp;
import org.jwj.novelconfig.exception.BusinessException;
import org.jwj.novelresource.manager.redis.VerifyCodeManager;
import org.jwj.novelresource.resp.ImgVerifyCodeRespDto;
import org.jwj.novelresource.service.ResourceService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceServiceImpl implements ResourceService {
    private final VerifyCodeManager verifyCodeManager;
    @Value("${novel.file.upload.path}")
    private String fileUploadPath;
    @Override
    public RestResp<ImgVerifyCodeRespDto> getImgVerifyCode() throws IOException {
        String sessionId = IdWorker.get32UUID();
        return RestResp.ok(ImgVerifyCodeRespDto.builder()
                .sessionId(sessionId)
                .img(verifyCodeManager.genImgVerifyCode(sessionId))
                .build());
    }

    @SneakyThrows
    @Override
    public RestResp<String> uploadImage(MultipartFile file) {
        LocalDateTime now = LocalDateTime.now();
        // 生成保存路径
        String savePath =
                SystemConfigConsts.IMAGE_UPLOAD_DIRECTORY
                        + now.format(DateTimeFormatter.ofPattern("yyyy")) + File.separator
                        + now.format(DateTimeFormatter.ofPattern("MM")) + File.separator
                        + now.format(DateTimeFormatter.ofPattern("dd"));
        String originalFilename = file.getOriginalFilename();
        if(Objects.isNull(originalFilename)){
            throw new BusinessException(ErrorCodeEnum.USER_UPLOAD_FILE_ERROR);
        }
        String fileName = IdWorker.get32UUID() + originalFilename.substring(originalFilename.lastIndexOf("."));
        File saveDir = new File(fileUploadPath + File.separator + savePath);
        if(!saveDir.exists()) {
            boolean mkdir = saveDir.mkdirs();
            if(!mkdir) {
                throw new BusinessException(ErrorCodeEnum.USER_UPLOAD_FILE_ERROR);
            }
        }
        // 保存文件
        File saveFile = new File(saveDir, fileName);
        file.transferTo(saveFile);
        if (Objects.isNull(ImageIO.read(saveFile))) {
            // 上传的文件不是图片
            Files.delete(saveFile.toPath());
            throw new BusinessException(ErrorCodeEnum.USER_UPLOAD_FILE_TYPE_NOT_MATCH);
        }
        // 返回可访问的URL路径
        return RestResp.ok(savePath + File.separator + fileName);
    }
}
