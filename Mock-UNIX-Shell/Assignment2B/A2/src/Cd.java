// Program created on June 23 by Shih-Chin Liang
//Part of assignment 2a for CSC207
//Move the current directory by the relative path or absolute path
//provided by the user.

public class Cd extends Command {

    /**
     *constructor
     */
    public Cd() {
        this.name = "cd";
        this.manual = "Change directory to Dir, " +
                "which may be relative to the current " +
                "directory or may be a full path.";
    }

    /**
     * Execute the cd command
     * Move the current directory to the input relative path or
     * absolute path provided by the user, if it does exist.
     *
     * @param param the array of command line arguments
     * @param shell JShell instance, the current working shell
     * @return void
     */
    public String execute(String[] param, JShell shell){
        String fullPath= shell.getRoot().createPath(param[0], shell);
        String path = shell.getCurDir().getPath();

        if (param[0].equals("..")){
            path = path.substring(0,path.lastIndexOf("/")+1);
            shell.setCurDir(Dir.searchDir(path, shell.getRoot()));
        }
        else if (param[0].equals("/")){
            shell.setCurDir(shell.getRoot());
        }
        else if(!(param[0].equals("."))){
            shell.setCurDir(Dir.searchDir(fullPath, shell.getRoot()));
        }
        return null;
    }

    /**
     * check the parameter array can be used to execute the cd command
     * valid parameter meaning
     * - the input is a path to an exist directory
     * - .. go back to the parent directory (Don't work at root directory)
     * - .  stay in the current directory nothing change
     * - /  go back to the root directory
     * - contain only one file path
     *
     * @param param the array of command line arguments
     * @param shell JShell instance, the current working shell
     * @return boolean true if parameters can be executed
     */
    public boolean checkParameters(String[] param, JShell shell){
        String fullPath = shell.getRoot().createPath(param[0], shell);
        String path = fullPath.substring(0,fullPath.lastIndexOf("/"));
        String dirName = fullPath.substring(fullPath.lastIndexOf("/")+1);
        Dir dir;

        if (path.equals(""))
            path = "/";
        if (param.length == 0){
            this.error = "Cd can't take space";
            return false;
        }
        if (param.length != 1){
            this.error = "Cd take only one varaible";
            return false;
        }
        else if (param[0].equals("..")){
            if (shell.getCurDir().getFullPath().equals("/")){
                this.error = "There is no parent directory. This is the root directory.";
                return false;
            }
        }
        else if (!(param[0].equals(".") || param[0].equals("/"))){
            if (!shell.getRoot().checkPathExists(fullPath, shell.getRoot())){
                this.error = "The directory " + fullPath.substring(fullPath.lastIndexOf("/") + 1) + " doesn't exist";
                return false;
            }
            else{
                dir = Dir.searchDir(path, shell.getRoot());
                if (dir != null && dir.getDocChild(dirName) != null){
                    this.error = dirName + " is a file not a directory";
                    return false;
                }
            }
        }
        return true;
    }
}