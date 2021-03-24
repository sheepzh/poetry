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
    public void test2() {
        Assert.assertNull(formatter.format("1992"));
    }

    @Test
    public void test3() {
        Assert.assertEquals("198704", formatter.format("一九八七四月"));
    }

    @Test
    public void test4() {
        Assert.assertEquals("200010", formatter.format("二000十月"));
    }

    @Test
    public void test5() {
        Assert.assertEquals("198010", formatter.format("一九八O年十月"));
    }

    @Test
    public void test6() {
        Assert.assertEquals("197006", formatter.format("一九七O. 6月"));
    }

    @Test
    public void test7() {
        Assert.assertEquals("197006", formatter.format("（一九七O. 6月）"));
    }
}
