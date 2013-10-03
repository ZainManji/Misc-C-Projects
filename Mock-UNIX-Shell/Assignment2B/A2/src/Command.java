//Program created on June 12, 2012
//Authors Avraham Sherman and Zain Manji
//c2sherma c1manjiz
//Assignment1 CSC207 - extended for A2a

/**
* abstract class Command
* @author Avraham Sherman, Zain Manji
*/
abstract class Command
{
    /**
    * @param name the name of the command
    * @param manual the manual entry for the command
    * @param error the error of the command, default ""
    */
    protected String name;
    protected String manual = "No manual entry.";
    protected String error = "";

    /**
    * get the error message of this command
    * reset the error String then return it
    *
    * @return get the error
    */
    public String getError() {
        String temp = this.error;
        this.error = "";
        return temp;
    }

    /**
    * get the name of this command
    *
    * @return get the name
    */
    public String getName() {
        return this.name;
    }

    /**
    * print the manual for this command
    *
    * @return void
    */
    public final String getMan() {
        return this.name + ": " + this.manual;
    }

    /**
    * execute the command specific functions
    *
    * @param param an array of arguments parsed from the command line
    * @param shell the current JShell instance
    * @return String null if nothing to print, otherwise the ouput of the command
    */
    public String execute(String[] param, JShell shell) throws JShellExceptionQuit {
        return null;
    }

    /**
    * check the parameters
    * return true or set this.error and return false
    *
    * @param param an array of arguments parsed from the command line
    * @param shell the current JShell instance
    * @return boolean default true
    */
    public boolean checkParameters(String[] param, JShell shell) {
        return true;
    }
}