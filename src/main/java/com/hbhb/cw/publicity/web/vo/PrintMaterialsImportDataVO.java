package com.hbhb.cw.publicity.web.vo;

import com.hbhb.cw.publicity.model.PrintMaterials;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author wangxiaogang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrintMaterialsImportDataVO implements Serializable {
    private static final long serialVersionUID = 6278219553609856334L;

    private String id;

    private List<PrintMaterials> materials;
}
