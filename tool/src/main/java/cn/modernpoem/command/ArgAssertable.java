package cn.modernpoem.command;

/**
 * Parse and assert the arg
 *
 * @author zhy
 */
public interface ArgAssertable {

    /**
     * 校验输入参数，并保存
     *
     * @param argReader 参数 reader
     * @return 参数是否正确
     */
    boolean assertAndSave(ArgReader argReader);
}
