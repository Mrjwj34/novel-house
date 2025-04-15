package org.jwj.noveluserservice.controller.front;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jwj.novelcommon.auth.UserHolder;
import org.jwj.novelcommon.resp.RestResp;
import org.jwj.noveluserapi.dto.req.UserInfoUptReqDto;
import org.jwj.noveluserapi.dto.req.UserLoginReqDto;
import org.jwj.noveluserapi.dto.req.UserRegisterReqDto;
import org.jwj.noveluserapi.dto.resp.UserInfoRespDto;
import org.jwj.noveluserapi.dto.resp.UserLoginRespDto;
import org.jwj.noveluserapi.dto.resp.UserRegisterRespDto;
import org.jwj.noveluserservice.manager.feign.BookFeignManager;
import org.jwj.noveluserservice.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class FrontUserController {
    private final UserService userService;
    private final BookFeignManager bookFeignManager;
    /**
     * 用户注册接口
     */
    @Operation(summary = "用户注册接口")
    @PostMapping("register")
    public RestResp<UserRegisterRespDto> register(@Valid @RequestBody UserRegisterReqDto dto) {
        return userService.register(dto);
    }

    /**
     * 用户登录接口
     */
    @Operation(summary = "用户登录接口")
    @PostMapping("login")
    public RestResp<UserLoginRespDto> login(@Valid @RequestBody UserLoginReqDto dto) {
        return userService.login(dto);
    }

    /**
     * 用户信息查询接口
     */
    @Operation(summary = "用户信息查询接口")
    @GetMapping
    public RestResp<UserInfoRespDto> getUserInfo() {
        return userService.getUserInfo(UserHolder.getUserId());
    }

    /**
     * 用户信息修改接口
     */
    @Operation(summary = "用户信息修改接口")
    @PutMapping
    public RestResp<Void> updateUserInfo(@Valid @RequestBody UserInfoUptReqDto dto) {
        dto.setUserId(UserHolder.getUserId());
        return userService.updateUserInfo(dto);
    }

    /**
     * 用户反馈提交接口
     */
    @Operation(summary = "用户反馈提交接口")
    @PostMapping("feedback")
    public RestResp<Void> submitFeedback(@RequestBody String content) {
        return userService.saveFeedback(UserHolder.getUserId(), content);
    }

    /**
     * 用户反馈删除接口
     */
    @Operation(summary = "用户反馈删除接口")
    @DeleteMapping("feedback/{id}")
    public RestResp<Void> deleteFeedback(@Parameter(description = "反馈ID") @PathVariable Long id) {
        return userService.deleteFeedback(UserHolder.getUserId(), id);
    }

    /**
     * 发表评论接口
     */
//    @Operation(summary = "发表评论接口")
//    @PostMapping("comment")
//    public RestResp<Void> comment(@Valid @RequestBody BookCommentReqDto dto) {
//        return bookFeignManager.publishComment(dto);
//    }

    /**
     * 修改评论接口
     */
    @Operation(summary = "修改评论接口")
    @PutMapping("comment/{id}")
    public RestResp<Void> updateComment(@Parameter(description = "评论ID") @PathVariable Long id,
                                        String content) {
//        BookCommentReqDto dto = new BookCommentReqDto();
//        dto.setUserId(UserHolder.getUserId());
//        dto.setCommentId(id);
//        dto.setCommentContent(content);
//        return bookFeignManager.updateComment(dto);
        return RestResp.ok();
    }

    /**
     * 删除评论接口
     */
    @Operation(summary = "删除评论接口")
    @DeleteMapping("comment/{id}")
    public RestResp<Void> deleteComment(@Parameter(description = "评论ID") @PathVariable Long id) {
//        BookCommentReqDto dto = new BookCommentReqDto();
//        dto.setUserId(UserHolder.getUserId());
//        dto.setCommentId(id);
//        return bookFeignManager.deleteComment(dto);
        return RestResp.ok();
    }

    /**
     * 查询书架状态接口 0-不在书架 1-已在书架
     */
    @Operation(summary = "查询书架状态接口")
    @GetMapping("bookshelf_status")
    public RestResp<Integer> getBookshelfStatus(@Parameter(description = "小说ID") String bookId) {
        return userService.getBookshelfStatus(UserHolder.getUserId(), bookId);
    }
}
