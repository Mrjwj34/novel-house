package org.jwj.novelresource.service;

import org.jwj.novelcommon.resp.RestResp;
import org.jwj.novelresource.resp.ImgVerifyCodeRespDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ResourceService {
    RestResp<ImgVerifyCodeRespDto> getImgVerifyCode() throws IOException;

    RestResp<String> uploadImage(MultipartFile file);
}
