// Program created on June 23 by Avraham Sherman
// Program created on June 23 by Avraham Sherman
// Part of assignment 2a for CSC207

/**
* Class PWD extends Command
* @author Avraham Sherman
*/
public class PWD extends Command {
    /**
     * constructor
     */
    public PWD() {
        this.name = "pwd";
        this.manual = "Print the current working directory.";
    }

    /**
     * Execute the pwd command
     * Print the current working directory.
     *
     * @param param the array of command line arguments
     * @param shell the JShell isntance
     * @return void
     */
    public String execute(String[] param, JShell shell) {
        return shell.getCurDir().getFullPath();
    }
}