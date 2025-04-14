package org.jwj.novelcommon.auth;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@UtilityClass
@Slf4j
public class JwtUtils {
    /**
     * JWT 加密密钥
     */
    private static final String SECRET = "E66559580A1ADF48CDD928516062F12E";

    /**
     * 定义系统标识头常量
     */
    private static final String HEADER_SYSTEM_KEY = "systemKeyHeader";


    /**
     * 根据用户ID和系统标识生成JWT令牌
     * @param uid
     * @param systemKey
     * @return
     */
    public String generateToken(Long uid, String systemKey) {
        return Jwts.builder()
                .setHeaderParam(HEADER_SYSTEM_KEY, systemKey)
                .setSubject(uid.toString())
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    /**
     * 解析JWT令牌，返回用户ID
     * @param token
     * @param systemKey
     * @return
     */
    public Long paraseToken(String token, String systemKey) {
        Jws<Claims> claimsJwts;
        try {
            // 解析JWT
            claimsJwts = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8))) // 设置密钥
                    .build()
                    .parseClaimsJws(token);
            // 判断系统标识
            if(Objects.equals(claimsJwts.getHeader().get(HEADER_SYSTEM_KEY), systemKey)) {
                // 返回用户ID
                return Long.parseLong(claimsJwts.getBody().getSubject());
            } else {
                log.error("JWT系统标识不匹配");
            }
        } catch (JwtException e) {
            // JWT令牌不可用
            log.error("JWT解析失败:{}", token);
        }
        return null;
    }
}
