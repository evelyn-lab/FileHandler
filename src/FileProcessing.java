import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileProcessing {
    private final HashMap<String, ArrayList<String>> dependencies;
    private final String rootFolderPath;
    private File currentDirectory;
    private final LinkedList<String> fileList;
    FileProcessing(String pathString) {
        dependencies = new HashMap<>();
        rootFolderPath = pathString;
        currentDirectory = new File("");
        fileList = new LinkedList<>();
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
        } catch (IOException e) {
            System.out.println("Invalid file path");
        }
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
    public void addParents(String fileName) {
        for (String key : dependencies.get(fileName)) {
            if (!dependencies.get(key).isEmpty()) {
                addParents(key);
            }
        }
        if (!fileList.contains(fileName)) {
            fileList.addFirst(fileName);
        }
    }
    public void makeFileList() {
        for (String key : dependencies.keySet()) {
            if (!dependencies.get(key).isEmpty()) {
                addParents(key);
            } else {
                if (!fileList.contains(key)) {
                    fileList.add(key);
                }
            }
        }
    }
    public void printFileList() {
        makeFileList();
        List<String> copy = fileList.subList(0, fileList.size());
        Collections.reverse(copy);
        for (String file : copy) {
            System.out.println(file);
        }
    }
    public void fileConcatenation(String fileToWritePath) {
        try {
            Path path = Path.of(rootFolderPath, fileToWritePath + ".txt");
            File fileToWrite = new File(path.toString());
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(fileToWrite, true)));
            for (String fileName : fileList) {
                path = Path.of(rootFolderPath, fileName);
                File file = new File(path.toString());
                try {
                    FileReader fr = new FileReader(file);
                    BufferedReader reader = new BufferedReader(fr);
                    String line = reader.readLine();
                    while (line != null) {
                        writer.println(line);
                        line = reader.readLine();
                    }
                } catch (IOException e) {
                    System.out.println("Couldn't take info from file");
                }
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.out.println("Invalid file name");
        }
    }
}
