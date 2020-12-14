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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialsFile implements Serializable {
    private static final long serialVersionUID = 9167940512247338081L;
    /**
     * id
     */
    private Long id;
    /**
     * 印刷品id
     */
    private Long materialsId;
    /**
     * 文件id
     */
    private Long fileId;
    /**
     * 上传时间
     */
    private Date createTime;
    /**
     * 上传人
     */
    private String createBy;
}
