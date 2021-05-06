package cn.modernpoem.util;

import cn.modernpoem.bean.Poem;
import cn.modernpoem.bean.Poet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * @author zhy
 */
public class FileHelper {
    private static String ROOT_DIR_PATH;

    public static boolean PRINT_SIMILAR = false;

    static {
        String fs = File.separator;
        String poemDir = System.getProperty("poemDir");
        if (poemDir == null || poemDir.length() == 0) {
            System.out.println("No poem directory");
            System.exit(0);
        } else {
            if (!poemDir.endsWith(fs)) {
                poemDir += fs;
            }
            ROOT_DIR_PATH = poemDir;
        }
    }

    public FileHelper() {
    }

    public List<Poet> getAll() {
        List<Poet> poetList = new LinkedList<>();
        File file = new File(ROOT_DIR_PATH);
        String[] childFile = file.list();
        if (childFile != null) {
            for (String s : childFile) {
                File f = new File(ROOT_DIR_PATH + s);
                if (f.isDirectory() && !f.isFile()) {
                    Poet poet = new Poet();
                    int indexOfUnderline = s.indexOf(95);
                    poet.setName(indexOfUnderline == -1 ? s : s.substring(0, indexOfUnderline));
                    poet.setDirName(s);
                    poetList.add(poet);
                }
            }
        }

        return poetList;
    }

    public List<Poem> findByPoet(Poet poet) {
        File file = new File(ROOT_DIR_PATH + "/" + poet.getDirName());
        String[] childFile = file.list();
        if (childFile == null) {
            return Collections.emptyList();
        }

        List<Poem> poems = new ArrayList<>();
        for (String s : childFile) {
            if (!s.endsWith("pt")) {
                continue;
            }

            File f = new File(ROOT_DIR_PATH + "/" + poet.getDirName() + "/" + s);
            try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(f));
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                Poem poem = new Poem();
                poem.setPoet(poet);
                int infoFinish = 2;
                boolean contentStart = false;

                String ss;
                while ((ss = reader.readLine()) != null) {
                    if (ss.startsWith("title")) {
                        String inTitle = ss.substring(6).trim();
                        poem.setTitle(inTitle);
                        if (!(inTitle + ".pt").equals(s)) {
                            System.out.println("WARNING: Bad file name " + poet.getDirName() + File.separator + s);
                        }
                        --infoFinish;
                    } else if (ss.startsWith("date")) {
                        poem.setDate(ss.substring(5).trim());
                        --infoFinish;
                    } else if (infoFinish == 0) {
                        if (contentStart) {
                            poem.lineAppend(ss);
                        } else if (!ss.trim().isEmpty()) {
                            poem.lineAppend(ss);
                            contentStart = true;
                        }
                    }
                }

                if (poem.getTitle() != null) {
                    poems.add(poem);
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        if (PRINT_SIMILAR) {
            findSimilar(poems);
        }
        return poems;
    }

    private String getPath(Poem p) {
        return ROOT_DIR_PATH + p.getFilePath();
    }

    private void findSimilar(List<Poem> poems) {
        int size = poems.size();
        for (int i = 0; i < size; i++) {
            Poem p1 = poems.get(i);
            String title1 = p1.getTitle();
            int l1 = title1.length();
            for (int j = i + 1; j < size; j++) {
                Poem p2 = poems.get(j);
                String title2 = p2.getTitle();

                String prefix = EditDistance.maxPrefixOf(title1, title2);
                if (prefix.contains("：")) {
                    // The same title
                    break;
                }

                int l2 = title2.length();

                boolean similar = false;
                if (l1 > 1 && l2 > 1) {
                    boolean contains12 = title1.startsWith(title2) && l1 - l2 < 2;
                    boolean contains21 = title2.startsWith(title1) && l2 - l1 < 2;
                    if (contains21 || contains12) {
                        similar = true;
                    }
                } else {
                    int distance = EditDistance.minDistance(title1, title2);
                    if (distance * 6 <= l1 + l2) {
                        similar = true;
                    }
                }
                if (similar) {
                    System.out.println("WARNING: Similar file names. " + p1.getFilePath() + "  &  " + p2.getFilePath());
                }
            }
        }
    }

    public void write(Poem p) {
        File file = new File(this.getPath(p));
        String realPath = this.getPath(p);
        File temp = new File(realPath + "temp");
        if (file.exists() && file.isFile()) {
            this.copyFile(file, temp);
        }

        this.write(this.getPath(p), p);
        if (!temp.delete()) {
            System.out.println("Failed to deleted the temp file: " + temp.getAbsolutePath());
        }
    }

    private void copyFile(File sourceFile, File targetFile) {
        try (FileInputStream input = new FileInputStream(sourceFile);
             BufferedInputStream inBuff = new BufferedInputStream(input);
             FileOutputStream output = new FileOutputStream(targetFile);
             BufferedOutputStream outBuff = new BufferedOutputStream(output)) {
            byte[] b = new byte[5120];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            outBuff.flush();
        } catch (IOException var61) {
            throw new RuntimeException(var61.getMessage());
        }
    }

    private void write(String path, Poem p) {
        try (FileWriter writer = new FileWriter(path, false)) {
            writer.write("title:" + p.getTitle() + "\n");
            writer.write("date:" + p.getDate() + "\n\n");
            List<String> lines = p.getLines();
            int i = lines.size();

            String s;
            for (Iterator<String> var7 = lines.iterator(); var7.hasNext(); writer.append(s)) {
                s = var7.next();
                --i;
                if (i > 0) {
                    s = s + "\n";
                }
            }

            writer.flush();
        } catch (IOException var15) {
            var15.printStackTrace();
        }

    }
}
