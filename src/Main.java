import java.io.File;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        boolean exists = false;
        String pathString = "";
        Scanner input = new Scanner(System.in);
        while (!exists) {
            System.out.println("Please enter the path to the root folder : ");
            pathString = input.nextLine();
            Path rootPath = Paths.get(pathString);
            exists = Files.exists(rootPath);
        }
        FileProcessing fileProcessing = new FileProcessing(pathString);
        fileProcessing.filesRecursion(new File(pathString));
        fileProcessing.checkLoops();
        System.out.println("Here's the file list : ");
        fileProcessing.printFileList();
        System.out.println("Please enter the name of the file you want to concatenate files in : ");
        pathString = input.nextLine();
        fileProcessing.fileConcatenation(pathString);
        input.close();
    }
}