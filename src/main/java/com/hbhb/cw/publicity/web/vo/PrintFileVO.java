package com.hbhb.cw.publicity.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class PrintFileVO implements Serializable {
    private static final long serialVersionUID = -2073153559743495041L;

    private Long id;

    @Schema(description = "宣传印刷品id")
    private Long printId;

    @Schema(description = "附件id")
    private Long fileId;

    @Schema(description = "文件名称")
    private String fileName;

    @Schema(description = "文件路径")
    private String filePath;

    @Schema(description = "文件大小")
    private String fileSize;

    @Schema(description = "作者")
    private String author;

    @Schema(description = "创建时间")
    private String createTime;
}
