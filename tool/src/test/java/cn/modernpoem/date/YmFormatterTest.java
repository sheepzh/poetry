package cn.modernpoem.date;

import org.junit.Assert;
import org.junit.Test;

public class YmFormatterTest {

    DateFormatter formatter = new YmFormatter();

    @Test
    public void test1() {
        Assert.assertEquals("200905", formatter.format("2009.5"));
    }

    @Test
    public void test2(){
        Assert.assertNull(formatter.format("1992"));
    }
}
