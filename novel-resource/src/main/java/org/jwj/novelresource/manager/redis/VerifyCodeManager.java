package org.jwj.novelresource.manager.redis;

import lombok.RequiredArgsConstructor;
import org.jwj.novelcommon.constants.CacheConsts;
import org.jwj.novelresource.util.ImageVerifyCodeUtils;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;
import java.time.Duration;

@RequiredArgsConstructor
public class VerifyCodeManager {
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 生成图形验证码，并放入 Redis 中
     */
    public String genImgVerifyCode(String sessionId) throws IOException {
        String verifyCode = ImageVerifyCodeUtils.getRandomVerifyCode(4);
        String img = ImageVerifyCodeUtils.genVerifyCodeImg(verifyCode);
        stringRedisTemplate.opsForValue().set(CacheConsts.IMG_VERIFY_CODE_CACHE_KEY + sessionId,
            verifyCode, Duration.ofMinutes(5));
        return img;
    }
}
