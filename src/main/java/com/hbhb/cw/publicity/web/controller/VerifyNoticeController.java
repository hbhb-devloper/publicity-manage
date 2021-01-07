package com.hbhb.cw.publicity.web.controller;

import com.hbhb.cw.publicity.api.PublicityVerifyApi;
import com.hbhb.cw.publicity.service.VerifyNoticeService;
import com.hbhb.cw.publicity.web.vo.ApplicationNoticeVO;
import com.hbhb.web.annotation.UserId;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.annotation.Resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @author yzc
 * @since 2021-01-07
 */
@Tag(name = "物料审核-工作台")
@RestController
@RequestMapping("/verify/notice")
public class VerifyNoticeController implements PublicityVerifyApi {

    @Resource
    private VerifyNoticeService verifyNoticeService;

    @Operation(summary = "统计待办提醒数量")
    @Override
    public Long countNotice(@Parameter(description = "用户id") Integer userId) {
        return verifyNoticeService.countNotice(userId);
    }

    @Operation(summary = "获取登录用户的待办提醒")
    @GetMapping("/summary")
    public List<ApplicationNoticeVO> getUserInvoiceNotice(@Parameter(hidden = true) @UserId Integer userId) {
        return verifyNoticeService.listInvoiceNotice(userId);
    }

    @Operation(summary = "更新提醒消息为已读")
    @PutMapping("/{id}")
    public void changeNoticeState(@Parameter(description = "id") @PathVariable Long id) {
        verifyNoticeService.changeNoticeState(id);
    }
}
