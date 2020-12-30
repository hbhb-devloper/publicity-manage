package com.hbhb.cw.publicity.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.beetl.sql.annotation.entity.AutoID;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author wangxiaogang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor

public class PictureInfoVO implements Serializable {
    private static final long serialVersionUID = -9113779419276377978L;
    @AutoID
    private Long id;

    @Schema(description = "申请单名称")
    private String pictureName;

    @Schema(description = "申请单号")
    private String applyNum;

    @Schema(description = "单位id")
    private Integer unitId;

    @Schema(description = "流程状态")
    private Integer state;

    @Schema(description = "申请时间")
    private Date applyTime;

    @Schema(description = "是否有宽带: (0-没有，1-有)")
    private Integer wideBand;

    @Schema(description = "制作商")
    private String producers;

    @Schema(description = "申请部门单位名称")
    private String unitName;

    @Schema(description = "申请人用户id")
    private Integer userId;

    @Schema(description = "申请人用户姓名")
    private String nickName;

    @Schema(description = "预算费用（元）")
    private BigDecimal predictAmount;

    @Schema(description = "选择原因")
    private String reason;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "附件")
    private List<PictureFileVO> files;

    @Schema(description = "画面设计文件id")
    private Integer fileId;

    @Schema(description = "画面设计文件路径")
    private String filePath;

    @Schema(description = "宣传画面设计文件名称")
    private String fileName;
}
