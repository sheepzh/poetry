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

    @Test
    public void test5() {
        Assert.assertEquals(formatter.format("200303/21"), formatter.format("2003.3.21"));
    }

    @Test
    public void test6() {
        Assert.assertEquals(formatter.format("2003-3-7"), formatter.format("2003.3.7"));
    }

    @Test
    public void test7() {
        Assert.assertEquals(formatter.format("2007-6-6"), "20070606");
    }

    @Test
    public void test8() {
        Assert.assertEquals(formatter.format("一九二三年三月二十九日"), "19230329");
    }

    @Test
    public void test89() {
        Assert.assertEquals(formatter.format("一九九二、四、二"), "19920402");
    }
}
