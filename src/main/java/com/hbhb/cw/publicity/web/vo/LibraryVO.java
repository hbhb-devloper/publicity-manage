package com.hbhb.cw.publicity.web.vo;

import com.hbhb.cw.publicity.model.Goods;

import java.io.Serializable;
import java.util.Date;
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
public class LibraryVO implements Serializable {
    private static final long serialVersionUID = 5322118140771174224L;
    private Long id;
    /**
     * 父类id
     */
    @Schema(description ="父类id（上级活动）")
    private Long parentId;
    /**
     * 市场部或者政企
     */
    @Schema(description ="市场部或者政企")
    private Integer unitId;
    /**
     * 活动名称
     */
    @Schema(description ="活动名称")
    private String activityName;
    /**
     * 是否启用
     */
    @Schema(description ="是否启用")
    private Integer state;
    /**
     * 创建时间
     */
    @Schema(description ="创建时间")
    private Date createTime;
    /**
     * 创建人
     */
    @Schema(description ="创建人")
    private String createBy;
    /**
     * 更新时间
     */
    @Schema(description ="更新时间")
    private Date updateTime;
    /**
     * 更新人
     */
    @Schema(description ="更新人")
    private String updateBy;
    /**
     * 宣传物料活动
     */
    @Schema(description ="宣传物料活动")
    private List<LibraryVO> activityVOList;
    /**
     * 宣传物料用品
     */
    @Schema(description ="宣传物料用品")
    private List<Goods> goodsList;
}