package com.hbhb.cw.publicity.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaterialsInfo implements Serializable {
    private static final long serialVersionUID = 655855764419843793L;
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
    private Integer address;
    /**
     * 接收人
     */
    private String receivedBy;
    /**
     * 电话
     */
    private String receivedPhone;
    /**
     * 需送达日期
     */
    private Date deliveryDate;
    /**
     * 备注
     */
    private String remake;
    /**
     * 物料名称
     */
    private String materialsName;
    /**
     * 印刷品id
     */
    private Long materialsId;
}
