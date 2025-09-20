import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileReaderImpl implements FileReader {

    @Override
    public FileDetails readFile(String filePath) throws IOException {
        Path path = Path.of(filePath);
        boolean isReadable = Files.isReadable(path);
        String contents = isReadable ? Files.readString(path) : "";
        long size = Files.exists(path) ? Files.size(path) : 0;

        return new FileDetails( path.getFileName().toString(), path.toAbsolutePath().toString(),
                isReadable, String.valueOf(size), contents);
    }

}
