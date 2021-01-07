package com.hbhb.cw.publicity.model;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yzc
 * @since 2021-01-07
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsFile implements Serializable {
    private static final long serialVersionUID = -8970615184697225824L;

    private Long id;
    private Long goodsId;
    /**
     * 是否必传0，1非必传
     */
    private Integer required;
    /**
     * 作者
     */
    private String author;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 附件id
     */
    private Long fileId;
}
