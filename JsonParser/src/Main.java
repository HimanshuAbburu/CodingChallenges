import exceptions.JsonParseException;
import models.JsonValue;

import java.io.*;
import java.util.*;

public class Main {
    private static final String JSON_FILE_PATH = "resources/tests/step3/valid.json";
    
        public static void main(String[] args) {
            try (BufferedReader br = new BufferedReader(new FileReader(JSON_FILE_PATH))) {
                StringBuilder input = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) { input.append(line); }
                Lexer lexer = new Lexer(input.toString());
                List<Token> tokens = lexer.tokenize();
                JsonParser parser = new JsonParser(tokens);
                JsonValue result = parser.parse();
                System.out.println(result.toJson());
            } catch (JsonParseException e) {
                System.err.println("Invalid JSON: " + e.getMessage());
            } catch (IOException e) {
                System.err.println("File error: " + e.getMessage());
            }
        }
    }

