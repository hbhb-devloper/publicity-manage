package com.hbhb.cw.publicity.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author wangxiaogang
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrintMaterials implements Serializable {
    private static final long serialVersionUID = 7349886277606375304L;
    /**
     * id
     */
    private Long id;
    /**
     * 申请数量
     */
    private Integer applyCount;
    /**
     * 计量单位
     */
    private String units;
    /**
     * 纸张样式
     */
    private String style;
    /**
     * 制作工艺
     */
    private String craft;
    /**
     * 部门
     */
    private Integer unitId;
    /**
     * 分配地方（地址）
     */
    private String address;
    /**
     * 是否加盖合同分公司印章
     */
    private Integer isSeal;
    /**
     * 接收人/电话
     */
    private String receivedBy;
    /**
     * 是否有合同编号
     */
    private Integer isNum;
    /**
     * 需送达日期
     */
    private Date deliveryDate;
    /**
     * 备注
     */
    private String remark;
    /**
     * 物料名称
     */
    private String materialsName;
    /**
     * 类型（1-业务单式、2-宣传单页）
     */
    private Integer type;
    /**
     * 印刷品id
     */
    private Long printId;

}
