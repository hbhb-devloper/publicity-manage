package com.hbhb.cw.publicity.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ResultProvider(AutoJsonMapper.class)
public class PrintInfoVO implements Serializable {
    private static final long serialVersionUID = 1254866564949328907L;

    @Schema(description = "id")
    private Long id;

    @Schema(description = "申请单号")
    private String printNum;

    @Schema(description = "申请单名称")
    private String printName;

    @Schema(description = "申请部门单位id")
    private Integer unitId;

    @Schema(description = "申请人用户id")
    private Integer userId;

    @Schema(description = "申请时间")
    private Date applyTime;

    @Schema(description = "材料类型（0 业务单式/1 宣传单页）")
    private Integer materialType;

    @Schema(description = "市场部审核员")
    private Integer roleUserId;

    @Schema(description = "预估金额")
    private BigDecimal predictAmount;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "附件列表")
    private List<PrintFileVO> file;
}
