package com.hbhb.cw.publicity.web.controller;

import com.hbhb.cw.publicity.model.Goods;
import com.hbhb.cw.publicity.service.LibraryService;
import com.hbhb.cw.publicity.web.vo.GoodsInfoVO;
import com.hbhb.cw.publicity.web.vo.LibraryVO;
import com.hbhb.web.annotation.UserId;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.annotation.Resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yzc
 * @since 2020-11-23
 */
@Tag(name = "宣传管理-物料产品库")
@RestController
@RequestMapping("/library")
@Slf4j
public class LibraryController {

    @Resource
    private LibraryService libraryService;

    @GetMapping("/tree")
    @Operation(summary = "物料活动产品列表")
    public List<LibraryVO> getTreeList(@Parameter(hidden = true) @UserId Integer userId) {
        return libraryService.getTreeList(userId);
    }

    @PostMapping("/add")
    @Operation(summary = "新增物料活动产品列表")
    public void addLibrary(@Parameter(hidden = true) @UserId Integer userId, @RequestBody Goods libraryAddVO) {
        libraryService.addLibrary(userId, libraryAddVO);
    }

    @PutMapping("")
    @Operation(summary = "修改活动或产品")
    public void library(@Parameter(hidden = true) @UserId Integer userId, @RequestBody Goods libraryAddVO) {
        libraryService.updateLibrary(userId, libraryAddVO);
    }

    @GetMapping("/{id}")
    @Operation(summary = "得到详情")
    public GoodsInfoVO getInfo(@PathVariable Long id){
       return libraryService.getInfo(id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    public void deleteGoods(@PathVariable Long id){
        libraryService.deleteGoods(id);
    }
}
