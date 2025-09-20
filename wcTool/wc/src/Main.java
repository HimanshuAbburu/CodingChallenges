import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        final String FILE_PATH = "F:\\CodingChallenges\\wcTool\\wc\\test.txt";


        FileReader fileReader = new FileReaderImpl();
        try{
            FileDetails file = fileReader.readFile(FILE_PATH);
            System.out.println("File name: " + file.getFileName());
            System.out.println("File size (bytes): " + file.getFileSize());
            System.out.println("Lines: " + file.getLineCount());
            System.out.println("Words: " + file.getWordCount());
            System.out.println("Characters: " + file.getCharacterCount());
        } catch (IOException io){
            System.err.println("Failed to load file: " + io.getMessage());
        }

    }

}