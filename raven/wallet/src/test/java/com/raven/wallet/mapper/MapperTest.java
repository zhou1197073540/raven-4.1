package com.raven.wallet.mapper;


import com.raven.wallet.bean.WalletUserFrozenBean;
import com.raven.wallet.consts.Const;
import com.raven.wallet.dto.AssetsDto;
import com.raven.wallet.dto.AssetsQueryDto;
import com.raven.wallet.dto.PointsDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class MapperTest {

    @Autowired
    WalletMapper mapper;

    @Test
    public void extendsDtoTest() throws Exception {
        AssetsDto dto = new AssetsDto();
        System.out.println(dto.toString());
    }

    @Test
    public void addWalletUserChangeLogTest() throws Exception {
        PointsDto dto = new PointsDto();
        dto.setChange(1);
        dto.setSerialNum("e267af0086d9bc9bdfa07aa84f4ae17c");
        dto.setUid("f5a5804d22335318befd4edcf732f2ee");
        dto.setDate("2017-10-30");
        dto.setTime("2017-10-30 11:44:02");
        dto.setComment(6);
        mapper.addWalletUserChangeLog(dto);
    }

    @Test
    public void getUserPointsListByUidTest() throws Exception {
//        List<PointsDto> dtos = mapper.getUserPointsListByUid("hehe");
//        System.out.println(dtos);
    }

    @Test
    public void getWalletUserFrozenByOidTest() throws Exception {
        WalletUserFrozenBean bean =
                mapper.getWalletUserFrozenByOidAndFrozenStatus("1111", Const.FROZEN);
        if (bean != null) {
            System.out.println(bean.toString());
        } else {
            System.out.println("is null");
        }
    }

    @Test
    public void updateWalletUserFrozenByOidTest() throws Exception {
        int num = mapper.updateWalletUserFrozenStatusByOid("e267af0086d9bc9bdfa07aa84f4ae17d", 0);
        System.out.println(num);
    }

    @Test
    public void getUserMoneyByUidAndTypeAndDateRangeTest() throws Exception {
        AssetsQueryDto dto = new AssetsQueryDto();
        dto.setType(Const.POINTS);
        dto.setUid("f5a5804d22335318befd4edcf732f2ee");
        dto.setStartDate("2017-11-14");
        dto.setEndDate("2017-11-30");
        System.out.println(dto.toString());
       Integer num = mapper.getUserMoneyByUidAndTypeAndDateRange(dto);
        System.out.println(num);
    }
}
