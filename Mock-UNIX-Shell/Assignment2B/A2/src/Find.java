import java.util.List;

// Program created on July 13 by Shih-Chin Liang
// Part of assignment 2b for CSC207
// Recursively search path(relative or full) for file names containing
// regular expression and print them one per line.

public class Find extends Command {
    /**
     *constructor
     */
    public Find() {
        this.name = "find";
        this.manual = "Print the filename that contain regex one per line " +
                "including their path";
    }

    /**
     * Execute the Find command
     * Recursively search path for file names containing
     * the regular expression input by the user and print
     *  them one per line.
     *
     * @param param the array of command line arguments
     * @param shell JShell instance, the current working shell
     * @return String result
     */
    public String execute(String[] param, JShell shell) {
        String result = "";
        String path;
        String regex = param[0];
        Dir dir = shell.getRoot();
        for (int i = 1; i < param.length; i++){
            path = dir.createPath(param[i], shell);
            dir = Dir.searchDir(path, shell.getRoot());
            result = traverse(result, regex, dir, shell);
        }
        if (result.contains("\n"))
            result = result.substring(0, result.length()-1);
        else if (result.equals(""))
            result = "No such file is found";
        return result;
    }

    /**
     * Traverse through the directory
     * Recursively go through the sub-directory or only directory and
     * record the filename that matches the regular expression.
     *
     * @param result String the result of all line containing regular expression
     * @param regex String the regular expression
     * @param dir Dir the current directory
     * @param shell JShell instance, the current working shell
     * @return String result
     */
    private String traverse(String result, String regex, Dir dir, JShell shell){
        List <Document> doc;
        List <Dir> child;
        String path;
        if (dir != null){
            doc = dir.getDocChildren();
            for (int j = 0; j < doc.size(); j ++){
                if (dir.getFullPath().equals("/"))
                    path = dir.getFullPath() + doc.get(j).getName();
                else
                    path = dir.getFullPath() + "/" + doc.get(j).getName();
                if (Regex.comparePart(regex, doc.get(j).getName()) && !result.contains(path))
                    result += path + "\n";
            }
            child = dir.getDirChildren();
            for (int i = 0; i < child.size(); i ++){
                if (child.get(i).getDirChildren() != null)
                    result = traverse(result, regex, child.get(i), shell);
            }
        }
        return result;
    }


    /**
     * Check the parameter array used to execute the Find command
     * valid parameter meaning the input(s)
     * - the path(relative or full) have to exists
     * - the regular expression is valid
     * - have at least one regular expression and one path(relative or full)
     *
     * @param param the array of command line arguments
     * @param shell JShell instance, the current working shell
     * @return boolean true if parameters can be executed
     */
    public boolean checkParameters(String[] param, JShell shell) {
        Dir rootDir = shell.getRoot();
        String path;

        if (!(param.length > 1)){
            this.error = "Find take at least two inputs";
            return false;
        }
        else if (!Regex.isRegex(param[0])){
            this.error = "Invalid regular exprsession " + param[0];
            return false;
        }
        else{
            for (int i = 1; i < param.length; i ++){
                path = rootDir.createPath(param[i], shell);
                if (!rootDir.checkPathExists(path, rootDir)){
                    this.error = "The path " + param[i] + " doesn't exist";
                    return false;
                }
            }
        }
        return true;
    }
}
