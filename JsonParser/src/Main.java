import java.io.File;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        final String JSON_FILE_PATH = "F:\\CodingChallenges\\JsonParser\\resources\\tests\\step1";

        // Step 1
        String fileName = JSON_FILE_PATH + "\\invalid.json";
        try (Scanner sc = new Scanner(new File(fileName))) {
            StringBuilder sb = new StringBuilder();
            while (sc.hasNextLine()) sb.append(sc.nextLine());
            String content = sb.toString().trim();
            if ((content.startsWith("{")) && (content.endsWith("}")) ) {
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