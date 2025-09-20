import java.io.BufferedReader;

public class FileDetails {

    private String fileName;
    private String absolutePath;
    private Boolean readable;
    private String contents;
    private String fileSize;

    public FileDetails(String fileName, String absolutePath, Boolean readable, String fileSize, String contents) {
        this.fileName = fileName;
        this.absolutePath = absolutePath;
        this.readable = readable;
        this.contents = contents;
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public Boolean getReadable() {
        return readable;
    }

    public String getContents() {
        return contents;
    }

    public String getFileSize() {
        return fileSize;
    }

    public int getLineCount() {
        int noOfLines = 0;
        try (BufferedReader reader = new BufferedReader(new java.io.FileReader(absolutePath))) {
            while (reader.readLine() != null) {
                noOfLines++;
            }
        } catch (Exception e) {
            return -1;
        }
        return noOfLines;
    }
    public int getWordCount() {
        return contents.isEmpty() ? 0 : contents.trim().split("\\s+").length;
    }
    public int getCharacterCount() {
        return contents.length();
    }
}
