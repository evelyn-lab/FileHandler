import java.io.File;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        boolean exists = false;
        String pathString = "";
        while (!exists) {
            System.out.println("Please enter the path to the root folder : ");
            Scanner input = new Scanner(System.in);
            pathString = input.nextLine();
            Path rootPath = Paths.get(pathString);
            exists = Files.exists(rootPath);
        }
        FileProcessing fileProcessing = new FileProcessing(pathString);
        fileProcessing.filesRecursion(new File(pathString));
        fileProcessing.printDependencies();
        fileProcessing.checkLoops();
    }
}