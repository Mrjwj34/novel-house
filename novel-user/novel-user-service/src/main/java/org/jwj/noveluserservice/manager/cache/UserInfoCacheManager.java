package org.jwj.noveluserservice.manager.cache;

import lombok.RequiredArgsConstructor;
import org.jwj.novelcommon.constants.CacheConsts;
import org.jwj.noveluserapi.dto.UserInfoDto;
import org.jwj.noveluserservice.dao.entity.UserInfo;
import org.jwj.noveluserservice.dao.mapper.UserInfoMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class UserInfoCacheManager {
    private final UserInfoMapper userInfoMapper;
    // 查询用户信息并放入缓存
    @Cacheable(cacheManager = CacheConsts.REDIS_CACHE_MANAGER,
        value = CacheConsts.USER_INFO_CACHE_NAME)
    public UserInfoDto getUser(Long uid) {
        UserInfo userInfo = userInfoMapper.selectById(uid);
        return Objects.isNull(userInfo) ? null : UserInfoDto.builder()
                .id(userInfo.getId()).status(userInfo.getStatus()).build();
    }
}
