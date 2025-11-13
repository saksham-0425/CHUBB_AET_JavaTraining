import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;


public class WordCounter {

    public static void main(String[] args) {
        String filePath = "data.txt";  
        String target = "india";

        try {
            long count = Files.lines(Paths.get(filePath))
                    .flatMap(line -> Arrays.stream(line.split("\\W+")))
                    .map(String::toLowerCase)
                    .filter(word -> word.equals(target))
                    .count();

            System.out.println("Occurrences of 'INDIA': " + count);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
