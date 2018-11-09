package com.raven.iirinstitute.utils;


import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class NumericTest {

    @Test
    public void NumericTest() throws Exception {
        System.out.println(StringUtils.isNumeric("-1"));
    }
}
