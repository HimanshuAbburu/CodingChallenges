import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private static final String JSON_FILE_PATH = "F:\\CodingChallenges\\JsonParser\\resources\\tests";

    public static void main(String[] args) throws IOException {

        // Step 1
        String file = (JSON_FILE_PATH + "\\step1\\valid.json");

        StringBuilder fileContents = new StringBuilder();

        ArrayList<Token> tokensList = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();

            tokensList = new ArrayList<>();
            int i = 0;

            // Parse file to String
            while (line != null) {
                fileContents.append(line);
                line = br.readLine();
            }

            // Tokeniser
            String fullInput = fileContents.toString();
            while (i < fullInput.length()) {
                char c = fullInput.charAt(i);
                if (Character.isWhitespace(c)) {
                    i++;
                    continue;
                }
                if (c == '{') {
                    Token token = new Token(TokenType.START_OBJECT, c + "");
                    tokensList.add(token);
                    i++;
                    continue;
                } else if (c == '}') {
                    Token token = new Token(TokenType.END_OBJECT, c + "");
                    tokensList.add(token);
                    i++;
                    continue;
                } else if (c == '[') {
                    Token token = new Token(TokenType.START_ARRAY, c + "");
                    tokensList.add(token);
                    i++;
                    continue;
                } else if (c == ']') {
                    Token token = new Token(TokenType.END_ARRAY, c + "");
                    tokensList.add(token);
                    i++;
                    continue;
                } else if (c == ':') {
                    Token token = new Token(TokenType.COLON, c + "");
                    tokensList.add(token);
                    i++;
                    continue;
                } else if (c == ',') {
                    Token token = new Token(TokenType.COMMA, c + "");
                    tokensList.add(token);
                    i++;
                    continue;
                } else if (Character.isDigit(c) || c == '-') {
                    StringBuilder number = new StringBuilder();
                    while (i < fullInput.length() && (Character.isDigit(fullInput.charAt(i)) || fullInput.charAt(i) == '.' /* etc for exponents */)) {
                        number.append(fullInput.charAt(i));
                        i++;
                    }

                    Token token = new Token(TokenType.NUMBER, number.toString());
                    tokensList.add(token);

                    continue;
                } else if (c == '"') {
                    StringBuilder str = new StringBuilder();
                    i++; // Skip opening quote
                    while (i < fullInput.length() && fullInput.charAt(i) != '"') {
                        // Possibly handle escape characters here
                        str.append(fullInput.charAt(i));
                        i++;
                    }
                    i++; // Skip closing quote
                    tokensList.add(new Token(TokenType.STRING, str.toString()));
                    continue;
                } else if (fullInput.startsWith("true", i)) {
                    Token token = new Token(TokenType.TRUE, "true");
                    tokensList.add(token);
                    i += 4;
                    continue;
                } else if (fullInput.startsWith("false", i)) {
                    Token token = new Token(TokenType.FALSE, "false");
                    tokensList.add(token);
                    i += 5;
                    continue;
                } else if (fullInput.startsWith("null", i)) {
                    Token token = new Token(TokenType.NULL, "null");
                    tokensList.add(token);
                    i += 4;
                    continue;
                } else {
                    i++;
                }
            }

        } catch (IOException io) {
            io.printStackTrace();
        }

        for (Token token : tokensList) {
            System.out.println(token.getType() + " : " + token.getValue());
        }

    }
}