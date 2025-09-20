import java.io.File;
import java.io.IOException;

public interface FileReader {
    FileDetails readFile(String filePath) throws IOException;
//    String getFileLines(String filePath);
//    String getWords(String filePath);
//    int getCharacters(String fileContents);

}
