package com.hbhb.cw.publicity.web.controller;

import com.hbhb.cw.publicity.web.vo.PictureFlowInfoVo;
import com.hbhb.web.annotation.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author wangxiaogang
 */
@Tag(name = "宣传管理-宣传物料设计流程")
@RestController
@RequestMapping("/materials/flow")
@Slf4j
public class MaterialsFlowController {
    @Operation(summary = "获取宣传物料设计流程详情")
    @GetMapping("/list/{materialsId}")
    public List<PictureFlowInfoVo> getMaterialsFlow(@PathVariable Long materialsId, @UserId Integer userId) {
        return null;
    }
}