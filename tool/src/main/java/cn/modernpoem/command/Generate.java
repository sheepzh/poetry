package cn.modernpoem.command;

import java.util.Objects;

/**
 * @author zhy
 */
public class Generate extends BaseCommand {
    boolean readMe = false;

    @Override
    public void help() {
        System.out.println("generate:\n" +
                "[-r]                    Generate or update the READEME.md of this project.\n");
    }

    @Override
    public boolean assertAndSave(ArgReader reader) {
        String arg;
        while (reader.hasNext()) {
            arg = this.getArg(reader.read());
            if (Objects.equals(arg, "r")) {
                readMe = true;
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    void deal0() {

    }
}
