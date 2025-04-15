package org.jwj.noveluserservice.service;


import org.jwj.novelcommon.resp.RestResp;
import org.jwj.noveluserapi.dto.req.UserInfoUptReqDto;
import org.jwj.noveluserapi.dto.req.UserLoginReqDto;
import org.jwj.noveluserapi.dto.req.UserRegisterReqDto;
import org.jwj.noveluserapi.dto.resp.UserInfoRespDto;
import org.jwj.noveluserapi.dto.resp.UserLoginRespDto;
import org.jwj.noveluserapi.dto.resp.UserRegisterRespDto;

public interface UserService {
    RestResp<UserRegisterRespDto> register(UserRegisterReqDto dto);

    RestResp<UserLoginRespDto> login(UserLoginReqDto dto);

    RestResp<UserInfoRespDto> getUserInfo(Long userId);

    RestResp<Void> updateUserInfo(UserInfoUptReqDto dto);

    RestResp<Void> saveFeedback(Long userId, String content);

    RestResp<Void> deleteFeedback(Long userId, Long id);

    RestResp<Integer> getBookshelfStatus(Long userId, String bookId);
}
