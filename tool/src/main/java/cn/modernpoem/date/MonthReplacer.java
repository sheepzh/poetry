package cn.modernpoem.date;

/**
 * Replace month
 */
public class MonthReplacer {

    public String replace(String lastLine) {
        return lastLine
                .replace("元月", "1月")
                .replace("Jan", "01")
                .replace("Feb", "02")
                .replace("Mar", "03")
                .replace("Apr", "04")
                .replace("May", "05")
                .replace("Jun", "06")
                .replace("Jul", "07")
                .replace("Aug", "08")
                .replace("Sept", "09")
                .replace("Sep", "09")
                .replace("Oct", "10")
                .replace("Nov", "11")
                .replace("Dec", "12");
    }
}
