import java.util.Arrays;

public class RegexTest {
    /**
     * main method
     *
     * @param args optional String array not used
     */
    public static void main (String[] args) {
        System.out.println("Testing Regex.compareAll...");
        String[][] testcases = {{"a*", "abc"}, {"a?bc", "abc"},
                                {"a", "abc"}, {"b*", "abc"},
                                {"ab*c+", "abc"}, {"[a-z]+", "abc"},
                                {"zx", "abc"}, {"a*", "bbb"}};
        for (int i = 0; i < testcases.length; i++) {
            System.out.println ("Comparing " + testcases[i][0]
                                + " against all of " +testcases[i][1]);
            System.out.println (Regex.compareAll (testcases[i][0], testcases[i][1]));
        }
        System.out.println("\nTesting Regex.comparePart...");
        for (int i = 0; i < testcases.length; i++) {
            System.out.println ("Comparing " + testcases[i][0]
                                + " against part of " +testcases[i][1]);
            System.out.println (Regex.comparePart (testcases[i][0], testcases[i][1]));
        }
    }
}