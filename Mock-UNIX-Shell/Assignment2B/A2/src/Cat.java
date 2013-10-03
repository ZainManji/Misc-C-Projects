/**
* Class Cat extends Command
* @author Zain Manji
*/
public class Cat extends Command {
    /**
    * constructor
    */
    public Cat() {
        this.name = "cat";
        this.manual = "Displays the contents of the specified FILE in the shell.";
    }

    /**
    * Execute the cat command
    * Prints the contents of the specified file in the
    * shell
    *
    * @param param - the array of command line arguments
    * @param shell - the running shell
    * @return void
    */
    public String execute(String[] param, JShell s) {

        Dir currentDir = s.getCurDir();
        Dir root = s.getRoot();
        String path = param[0];
        String[] pathArray = path.split("/");
        String output = null;
        Document foundDoc;

        if (pathArray.length < 2) {
            foundDoc = currentDir.getDocChild(path);
            output = foundDoc.getContents();
        }
        else {
            if (path.startsWith("/")) {
                foundDoc = Dir.searchDoc(path, root);
            }
            else {
                foundDoc = Dir.searchDoc(path, currentDir);
            }
            output = foundDoc.getContents();
        }

        return output;
    }


    /**
    * Check if the parameter array can be used to execute the cat command
    *
    * @param param - the array of command line arguments
    * @param shell - the running JShell
    * @return boolean true if parameters can be executed
    */
    public boolean checkParameters(String[] param, JShell shell) {

        if (param.length != 1) {
            this.error = "Must specify one File.";
            return false;
        }

        Dir currentDir = shell.getCurDir();
        Dir root = shell.getRoot();
        String path = param[0];
        String[] pathArray = path.split("/");
        Document foundDoc;

        if (pathArray.length < 2) {
            foundDoc = currentDir.getDocChild(path);

            if (foundDoc == null) {
                this.error = "No such file exists.";
                return false;
            }

            return true;
        }
        else {
            if (path.startsWith("/")) {
                foundDoc = Dir.searchDoc(path, root);
            }
            else {
                foundDoc = Dir.searchDoc(path, currentDir);
            }

            if (foundDoc == null) {
                this.error = "No such file exists.";
                return false;
            }

            return true;
        }
    }
}