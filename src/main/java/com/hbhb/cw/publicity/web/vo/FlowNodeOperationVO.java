package com.hbhb.cw.publicity.web.vo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yzc
 * @since 2020-12-07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowNodeOperationVO implements Serializable {
    private static final long serialVersionUID = 6105388282853791936L;

    private String flowNodeId;
    private Integer operation;
}
