// Program created on June 23 by Avraham Sherman
// Part of assignment 2a for CSC207
// echo string to standard out or textfile


/**
* Class Echo extends Command
* @author Avraham Sherman
*/
public class Echo extends Command {

    /** @param outType the output method for echo, set by checkParameters */
    private String outType;
    /**
     * constructor
     */
    public Echo() {
        this.name = "echo";
        this.manual = "Prints input to output.";
    }

    /**
     * Execute the echo command
     * Print text to screen or file specified by '>' (overwrite)
     * or '>>' (append) format example: 'echo String >fileName'
     *
     * @param param the array of command line arguments
     * @param shell the JShell instance
     * @return void
     */
    public String execute(String[] param, JShell shell) {
        String fileName;
        String retVal = "";
        if (param.length == 0) {  // if no parameters specified
            return "";
        }
        for (int i = 0; i < param.length; i++) {
            retVal += param[i] + " ";
        }
        return retVal;
    }
}