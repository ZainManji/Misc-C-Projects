/**
 * Exception Class JShellException to be used for JShell specific errors
 */
public class JShellException extends Exception {

    public JShellException(String error) {
        super(error);
    }


    public static void main (String[] args) {
        try {
            throw new JShellException("temp error");
        }catch (JShellException e) {
            System.out.println(e.getMessage());
        }
    }
}