package com.hbhb.cw.publicity.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author wangxiaogang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrintFile implements Serializable {
    private static final long serialVersionUID = 2288666333649629159L;
    /**
     * id
     */
    private Long id;
    /**
     * 印刷品id
     */
    private Long printId;
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
