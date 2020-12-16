package com.hbhb.cw.publicity.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yzc
 * @since 2020-11-23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDetail implements java.io.Serializable {
    private static final long serialVersionUID = 6916212452789783608L;
    private Long id;
    /**
     * 申请id
     */
    private Long applicationId;
    /**
     * 产品id
     */
    private Long goodsId;
    /**
     * 申请数量
     */
    private Long applyAmount;
    /**
     * 修改后申请数量
     */
    private Long modifyAmount;
    /**
     * 审核状态（0-待审核、1-已通过、2-被拒绝）
     */
    private Integer state;
}
