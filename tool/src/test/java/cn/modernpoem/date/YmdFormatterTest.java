package cn.modernpoem.date;

import org.junit.Assert;
import org.junit.Test;

public class YmdFormatterTest {
    DateFormatter formatter = new YmdFormatter();

    @Test
    public void test1() {
        Assert.assertEquals("20120223", formatter.format("2012/02/23"));
    }

    @Test
    public void test2() {
        Assert.assertEquals("20010919", formatter.format("2001．9．19．"));
    }

    @Test
    public void test3() {
        Assert.assertNull(formatter.format("1992/02"));
    }

    @Test
    public void test4() {
        Assert.assertNull(formatter.format("1992"));
    }
}
