import java.util.regex.*;

/**
* Class Regex
* @author Avraham Sherman
*/
public abstract class Regex {

    /**
     * determine if paramater regex is a valid regular expression
     *
     * @param regex a string of the regex to be validated
     * @return boolean true if regex is vald, false otherwise
     */
    public static boolean isRegex(String regex) {
        try {
            Pattern.compile(regex);
        } catch (PatternSyntaxException exception) {
            return false;
        }
        return true;
    }

    /**
     * compare a regular expression against a string
     * return true if the REGEX is equal to line
     *
     * @param regex a String containing the regular expression
     * @param line a String containing the information to be compared
     * @return boolean true if an exact match, false otherwise
     */
    public static boolean compareAll(String regex, String line) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(line);
        return m.matches();
    }
    /**
     * compare a regular expression against a string
     * return true if the REGEX exists within line string
     *
     * @param regex a String containing the regular expression
     * @param line a String containing the information to be compared
     * @return boolean true if an exact match, false otherwise
     */
    public static boolean comparePart(String regex, String line) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(line);
        return m.find();
    }

}