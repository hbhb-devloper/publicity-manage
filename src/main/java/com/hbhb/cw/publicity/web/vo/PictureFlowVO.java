package com.hbhb.cw.publicity.web.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author wangxiaogang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PictureFlowVO implements Serializable {

    private static final long serialVersionUID = 6672023045366874196L;
    private Long id;
    private Long pictureId;
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
