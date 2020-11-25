package com.hbhb.cw.publicity.web.controller;

import com.hbhb.cw.publicity.web.vo.PictureResVO;
import com.hbhb.cw.publicity.web.vo.PrintResVO;
import com.hbhb.web.annotation.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.page.PageResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wangxiaogang
 */
@Tag(name = "宣传管理-宣传画面设计")
@RestController
@RequestMapping("/picture")
@Slf4j
public class PictureController {
    @Operation(summary = "印刷用品管理列表")
    @GetMapping("/list")
    public PageResult<PrintResVO> getPrintList(
            @Parameter(description = "页码，默认为1") @RequestParam(required = false) Integer pageNum,
            @Parameter(description = "每页数量，默认为10") @RequestParam(required = false) Integer pageSize,
            @Parameter(description = "查询参数") PictureResVO reqVO, @UserId Integer userId) {
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;
        return null;
    }


}
