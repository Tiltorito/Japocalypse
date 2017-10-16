import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Solution {
    private static String fileName = "Japocalypse.jar";
    public static void main(String[] args) throws Exception {
        Path currentPath = Paths.get(Utilities.getRunningDirectory());
        Path targetPath = Paths.get(Utilities.getAutoStartUp());
        System.out.println(targetPath.toAbsolutePath());
        copyFile(new File(fileName), new File(targetPath + "\\" + "second.jar"));
    }

    public static void copyFile(File source, File dest) throws Exception {

        FileReader fr = new FileReader(source);
        BufferedReader br = new BufferedReader(fr);
        Scanner reader = new Scanner(br);
        PrintWriter pr = new PrintWriter(dest);

        while(reader.hasNext()) {
            pr.println(reader.nextLine());
        }

        reader.close();
        pr.close();
    }
}
