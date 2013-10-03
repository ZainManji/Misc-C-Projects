/**
* Class Ls extends Command
* @author Zain Manji
*/
public class Exit extends Command
{
    /**
    * constructor
    */
    public Exit() {
        this.name = "exit";
        this.manual = "Quits the program.";
    }

    /**
    * Execute the exit command
    * Quits the running JShell program
    *
    * @param param - the array of command line arguments
    * @param shell - the running shell
    * @return void
    */
    public String execute(String[] param, JShell shell) throws JShellExceptionQuit {
        throw new JShellExceptionQuit("End of program.");
    }
}