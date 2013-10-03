
/**
 * Exception Class JShellExceptionQuit to be used to send a
 * quit signal to JShell
 */
public class JShellExceptionQuit extends JShellException {

    public JShellExceptionQuit(String error) {
        super(error);
    }

}