package cn.modernpoem.date;

import cn.mordernpoem.date.DateFormatter;
import cn.mordernpoem.date.YmdFormatter;
import org.junit.Assert;
import org.junit.Test;

public class YmdFormatterTest {
    DateFormatter formatter = new YmdFormatter();

    @Test
    public void test1() {
        Assert.assertEquals("20120223", formatter.format("2012/02/23"));
        Assert.assertEquals("20010919", formatter.format("2001．9．19．"));
    }
}
