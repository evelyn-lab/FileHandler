import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileProcessing {
    public HashMap<String, ArrayList<String>> dependencies;
    public String rootFolderPath;
    private File currentDirectory;
    FileProcessing(String pathString) {
        dependencies = new HashMap<>();
        rootFolderPath = pathString;
        currentDirectory = new File("");
    }
    public void readFile(File file) {
        try {
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            Path path;
            if (currentDirectory.exists()) {
                path = Path.of(currentDirectory.getName(), file.getName());
            } else {
                path = Path.of(file.getName());
            }
            dependencies.put(path.toString(), new ArrayList<>());
            String line = reader.readLine();
            while (line != null) {
                if (line.contains("require")) {
                    Pattern pattern = Pattern.compile(".*'([^']*)'.*");
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.matches()) {
                        String extract = matcher.group(1);
                        Path checkPath = Paths.get(rootFolderPath).resolve(extract);
                        if (Files.exists(checkPath)) {
                            dependencies.get(path.toString()).add(extract);
                        }
                    } else {
                        System.out.println("Couldn't read require from " + file.getName());
                    }
                }
                line = reader.readLine();
            }
        } catch (IOException e) {}
    }
    public void filesRecursion(File rootFolder) {
        File[] folderEntries = rootFolder.listFiles();
        if (folderEntries != null) {
            for (File entry : folderEntries) {
                if (entry.isDirectory()) {
                    currentDirectory = entry;
                    filesRecursion(entry);
                    continue;
                }
                readFile(entry);
                currentDirectory = new File("");
            }
        } else {
            System.out.println("Root folder is empty");
        }
    }
    public void printDependencies() {
        for (HashMap.Entry<String, ArrayList<String>> entry: dependencies.entrySet()) {
            System.out.println(entry);
        }
    }
    public boolean hasLoop(String currentFile, String fileName) {
        if (dependencies.get(fileName).contains(currentFile)) {
            return true;
        } else {
            if (!dependencies.get(fileName).isEmpty()) {
                for (String file : dependencies.get(fileName)) {
                    return hasLoop(currentFile, file);
                }
            }
        }
        return false;
    }
    public void checkLoops() {
        for (String key : dependencies.keySet()) {
            if (!dependencies.get(key).isEmpty()) {
                for (String fileName : dependencies.get(key)) {
                    if (!dependencies.get(fileName).isEmpty()) {
                        if (hasLoop(key, fileName)) {
                            System.out.println("There's a loop between " + key + " and " + fileName);
                            System.exit(1);
                        }
                    }
                }
            }
        }
    }
}
