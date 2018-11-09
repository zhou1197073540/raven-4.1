package com.tenthousand.fileupload.mapper;

import com.tenthousand.fileupload.bean.IIRIReportIndexBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MapperTest {

    @Autowired
    FileMapper mapper;

    @Test
    public void sqlConnectTest() throws Exception {
        Integer count = mapper.getFileCount();
        System.out.println("count:" + count);
    }

    @Test
    public void createIndexTest() throws Exception {
        IIRIReportIndexBean bean = new IIRIReportIndexBean();
        bean.setFid("fucker");
        bean.setTitle("asdasd");
        mapper.createIIRIReportIndex(bean);
    }

    @Test
    public void updateHtmlPath() throws Exception {
        IIRIReportIndexBean bean = new IIRIReportIndexBean();
        bean.setFid("fucker");
        bean.setHtml_path("fucker!!!!!");
        bean.setImg_path("IMG FUCKER!!!");
        mapper.updateHtmlIndexAndSummary(bean);
        mapper.updateImageIndex(bean);
    }

    @Test
    public void updateInputsTest() throws Exception {
        IIRIReportIndexBean bean = new IIRIReportIndexBean();
        bean.setFid("1511166346456");
        bean.setTitle("111");
        bean.setDate("20150101");
        bean.setAuthor("fucker");
        mapper.updateByFid(bean);
    }
}
