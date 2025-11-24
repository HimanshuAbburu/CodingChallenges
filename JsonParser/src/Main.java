import java.io.*;
import java.util.*;

public class Main {
    private static final String JSON_FILE_PATH = "resources/tests/step4/valid.json";
    // Set the file path as needed

    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(JSON_FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
        } catch (IOException io) {
            System.out.println("Error reading file: " + io.getMessage());
            System.exit(1);
        }

        try {
            Lexer lexer = new Lexer(sb.toString());
            List<Token> tokens = lexer.tokenize();
            Parser parser = new Parser(tokens);
            boolean valid = parser.parse();

            if (valid) {
                System.out.println("Valid JSON");
                System.exit(0);
            } else {
                System.out.println("Invalid JSON");
                System.exit(1);
            }
        } catch (Exception e) {
            System.out.println("Invalid JSON");
            System.exit(1);
        }
    }
}
