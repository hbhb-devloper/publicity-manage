package com.hbhb.cw.publicity.web.vo;

import com.hbhb.cw.publicity.model.MaterialsInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.beetl.sql.annotation.entity.ResultProvider;
import org.beetl.sql.core.mapping.join.AutoJsonMapper;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author wangxiaogang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ResultProvider(AutoJsonMapper.class)
public class MaterialsInfoVO implements Serializable {
    private static final long serialVersionUID = -4925013973478233876L;

    private Long id;

    @Schema(description = "申请单名称")
    private String materialsName;

    @Schema(description = "申请单号")
    private String materialsNum;

    @Schema(description = "申请部门单位名称")
    private String unitName;

    @Schema(description = "申请人用户id")
    private Integer userId;

    @Schema(description = "申请人用户姓名")
    private String nickName;

    @Schema(description = "申请时间")
    private Date applyTime;

    @Schema(description = "是否有宽带")
    private Integer wideBand;

    @Schema(description = "单位id")
    private Integer unitId;

    @Schema(description = "流程状态")
    private Integer state;

    @Schema(description = "制作商")
    private String producers;

    @Schema(description = "预算费用（元）")
    private BigDecimal predictAmount;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "文件")
    private List<MaterialsFileVO> files;

    @Schema(description = "物料列表")
    private List<MaterialsInfo> materialsInfo;


    @Schema(description = "导入物料详单excel数据id")
    private String importDateId;
}
