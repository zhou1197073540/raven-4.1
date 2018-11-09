package com.raven.wallet.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.raven.wallet.consts.Const;

@JsonIgnoreProperties
public class RMBDto extends TaskDto {


    private static final long serialVersionUID = 6163451902367761842L;

    public RMBDto() {
        super.setType(Const.RMB);
        super.setTypeStr(Const.RMB_STR);
    }
}
