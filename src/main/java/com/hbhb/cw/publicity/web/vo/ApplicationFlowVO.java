package com.hbhb.cw.publicity.web.vo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yzc
 * @since 2020-12-07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationFlowVO implements Serializable {

    private static final long serialVersionUID = 2742125765906993207L;

    private Long id;
    private String batchNum;
    private String flowNodeId;
    private Long flowRoleId;
    private String approverRole;
    private String nickName;
    private Integer approver;
    private Integer operation;
    private String suggestion;
    private Boolean controlAccess;
    private Boolean isJoin;
    private Long assigner;
    private String roleDesc;
    private String updateTime;
}
