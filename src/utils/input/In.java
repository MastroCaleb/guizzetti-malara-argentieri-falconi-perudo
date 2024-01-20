package utils.input;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Input class to facilitate obtaining input.
 */
public class In {
    private static final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    /**
     * @return Asks for an input and returns it.
     */
    public static String nextLine() {
        while(true) {
            try {
                return in.readLine();
            }
            catch (IOException e) {
                System.out.println("Input error, try again.");
            }
        }
    }

    /**
     * @return Asks for an input and returns it.
     */
    public static int nextInt() {
        while(true) {
            try {
                return Integer.parseInt(nextLine());
            }
            catch (NumberFormatException e) {
                System.out.println("Not a number.");
            }
        }
    }
}
