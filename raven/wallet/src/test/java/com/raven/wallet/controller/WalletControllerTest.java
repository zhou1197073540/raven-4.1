package com.raven.wallet.controller;


import com.raven.wallet.consts.COMMENT;
import com.raven.wallet.dto.AssetsDto;
import com.raven.wallet.dto.PointsDto;
import com.raven.wallet.dto.RespDto;
import com.raven.wallet.dto.TaskDto;
import javafx.concurrent.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class WalletControllerTest {

    @Autowired
    WalletController controller;

    @Test
    public void changePointsTest() throws Exception {
        PointsDto dto = new PointsDto();
        dto.setChange(10000);
        dto.setOid("e267af0086d9bc9bdfa07aa84f4ae17d");
        dto.setUid("f5a5804d22335318befd4edcf732f2ee");
        dto.setDate("2017-11-30");
        dto.setTime("2017-10-30 11:44:02");
        dto.setComment(6);
        controller.changePoints(dto);
    }


    @Test
    public void pointsAssetsTransferTest() throws Exception {
        AssetsDto dto = new AssetsDto();
        dto.setChange(1001);
        dto.setOid("e267af0086d9bc9bdfa07aa84f4ae124");
        dto.setUid("f5a5804d22335318befd4edcf732f2ee");
        dto.setDate(LocalDate.now().toString());
        dto.setTime(LocalDateTime.now().toString());
        RespDto retDto = controller.pointsAssetsTransfer(dto);
        System.out.println(retDto.getStatus() + retDto.getMsg());
    }


    @Test
    public void getPointsTest() throws Exception {
        RespDto respDto = controller.getPoints("f5a5804d22335318befd4edcf732f2ee");
        System.out.println(respDto.getStatus() + respDto.getMsg() + respDto.getData().toString());
    }

    @Test
    public void freezeUnfreezeTest() throws Exception {
        PointsDto dto = new PointsDto();
        dto.setChange(101);
        dto.setUid("f5a5804d22335318befd4edcf732f2ee");
        dto.setComment(COMMENT.POINTS_GAMBLE_WIN.name);
        controller.freezePoints(dto);
    }

    @Test
    public void getAssetsByRangeTest() throws Exception {
        RespDto dto = controller.getAssetsByDate("f5a5804d22335318befd4edcf732f2ee",
                "2017-11-08","2017-11-09");
        System.out.println(dto.toString());
    }
}
