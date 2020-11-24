package com.hbhb.cw.publicity.web.controller;

import com.hbhb.cw.publicity.model.Goods;
import com.hbhb.cw.publicity.service.LibraryService;
import com.hbhb.cw.publicity.web.vo.LibraryVO;
import com.hbhb.web.annotation.UserId;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.annotation.Resource;

import io.swagger.v3.oas.annotations.Operation;
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
    public List<LibraryVO> getTreeList(@UserId Integer userId) {
       return libraryService.getTreeList(userId);
    }

    @PostMapping("/add")
    @Operation(summary = "新增物料活动产品列表")
    public void addLibrary(@UserId Integer userId, @RequestBody Boolean flag, @RequestBody Long parentId ) {
        libraryService.addLibrary(userId, flag, parentId);
    }

    @PostMapping("/goods/add")
    @Operation(summary = "新增物料活动产品列表")
    public void addGoods(@UserId Integer userId, @RequestBody Goods goods ) {
        libraryService.addGoods(userId, goods);
    }

    @PutMapping("/")
    @Operation(summary = "修改活动或产品")
    public void library(@UserId Integer userId, @RequestBody Boolean flag, @RequestBody String name) {
        libraryService.updateLibrary(userId,flag,name);
    }

    @PutMapping("/goods")
    @Operation(summary = "修改产品详情")
    public void goodsInfo(@UserId Integer userId,  @RequestBody Goods goods) {
        libraryService.updateGoodsInfo(userId, goods);
    }


}
