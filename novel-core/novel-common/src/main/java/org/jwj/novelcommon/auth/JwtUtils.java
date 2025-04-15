package org.jwj.novelcommon.auth;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Date;
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
        Date expirationDate = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7); // 设置过期时间为7天
        return Jwts.builder()
                .setHeaderParam(HEADER_SYSTEM_KEY, systemKey)
                .setSubject(uid.toString())
                .setIssuedAt(new Date()) // 设置签发时间 (可选)
                .setExpiration(expirationDate) // 设置过期时间 (必需)
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
        } catch (ExpiredJwtException e) {
            // JWT令牌过期
            log.error("JWT令牌过期:{}", token);
        } catch (Exception e) {
            // 其他异常
            log.error("JWT解析异常:{}", e.getMessage());
        }
        return null;
    }
}
