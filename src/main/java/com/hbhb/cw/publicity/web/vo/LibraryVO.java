package com.hbhb.cw.publicity.web.vo;

import org.beetl.sql.annotation.entity.ResultProvider;
import org.beetl.sql.core.mapping.join.AutoJsonMapper;

import java.io.Serializable;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yzc
 * @since 2020-11-23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ResultProvider(AutoJsonMapper.class)
public class LibraryVO implements Serializable {
    private static final long serialVersionUID = 5322118140771174224L;
    private Long id;
    private String label;
    /**
     * 宣传物料活动
     */
    @Schema(description ="宣传物料活动")
    private List<LibraryVO> children;
}