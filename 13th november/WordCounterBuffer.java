import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class WordCounterBuffer {

    public static void main(String[] args) {
        String filePath = "data.txt"; 
        String target = "india";  

        int count = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line;

            // Read file line by line
            while ((line = br.readLine()) != null) {

                // Use StringBuffer to manipulate the string
                StringBuffer sb = new StringBuffer(line);

                // Convert to lowercase to make it case-insensitive
                String lowerLine = sb.toString().toLowerCase();

                // Split the line into words
                String[] words = lowerLine.split("\\W+");

                // Count matches
                for (String word : words) {
                    if (word.equals(target)) {
                        count++;
                    }
                }
            }

            System.out.println("Occurrences of 'INDIA': " + count);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
