package cn.modernpoem.date;

import org.junit.Assert;
import org.junit.Test;

public class YearFormatterTest {
    DateFormatter formatter = new YearFormatter();

    @Test
    public void test1() {
        Assert.assertEquals("2000", formatter.format("2000"));
    }

    @Test
    public void test2() {
        Assert.assertEquals("2000", formatter.format("2000于北京"));
    }

    @Test
    public void test3() {
        Assert.assertEquals("2003", formatter.format("2003年"));
    }

    @Test
    public void test4() {
        Assert.assertNull(formatter.format("200001"));
    }

    @Test
    public void test5() {
        Assert.assertNull(formatter.format("2000年5月"));
    }
}
