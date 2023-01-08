import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        Path path = Path.of("c:" , "dev", "licenses", "windows", "readme.txt");
        System.out.println(path);
    }
}