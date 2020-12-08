package com.hbhb.cw.publicity.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.beetl.sql.annotation.entity.ResultProvider;
import org.beetl.sql.core.mapping.join.AutoJsonMapper;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author wangxiaogang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ResultProvider(AutoJsonMapper.class)
public class PictureInfoVO implements Serializable {
    private static final long serialVersionUID = -9113779419276377978L;
    private Long id;

    @Schema(description = "申请单名称")
    private String pictureName;

    @Schema(description = "申请单号")
    private String applyNum;

    @Schema(description = "单位id")
    private Integer unitId;

    @Schema(description = "申请时间")
    private Date applyTime;

    @Schema(description = "用户id")
    private Integer userId;

    @Schema(description = "附件")
    private List<PictureFileVO> files;

}
