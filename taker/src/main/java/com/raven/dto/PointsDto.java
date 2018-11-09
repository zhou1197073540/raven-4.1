package com.raven.dto;


import com.raven.consts.Const;

public class PointsDto extends TaskDto {

    private static final long serialVersionUID = 5163209526400467227L;

    public PointsDto() {
        super.setType(Const.POINTS);
        super.setTypeStr("_points");
    }
}
