package org.jwj.novelbookservice.manager.feign;

import lombok.AllArgsConstructor;
import org.jwj.novelcommon.constants.ErrorCodeEnum;
import org.jwj.novelcommon.resp.RestResp;
import org.jwj.noveluserapi.dto.resp.UserInfoRespDto;
import org.jwj.noveluserapi.feign.UserFeign;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
public class UserFeignManager {

    private final UserFeign userFeign;

    public List<UserInfoRespDto> listUserInfoByIds(List<Long> userIds) {

        RestResp<List<UserInfoRespDto>> resp = userFeign.listUserInfoByIds(userIds);
        if (Objects.equals(ErrorCodeEnum.OK.getCode(), resp.getCode())) {
            return resp.getData();
        }
        return new ArrayList<>(0);
    }


}
