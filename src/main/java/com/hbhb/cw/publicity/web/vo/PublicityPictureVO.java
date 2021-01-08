package com.hbhb.cw.publicity.web.vo;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yzc
 * @since 2020-09-25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicityPictureVO implements Serializable {
    private static final long serialVersionUID = 1295737297829856520L;
    private Long id;

    @Schema(description = "物料id")
    private Long goodsId;

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
