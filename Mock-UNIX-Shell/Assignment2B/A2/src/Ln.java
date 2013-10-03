/**
 * class handles creation of symbolic links within a virtual file system
 *
 * @author Sean Gallagher
 */
public class Ln extends Command {
    /**
     * @param link
     *            the path of the symbolic link
     * @param path
     *            the path of the desired destination directory
     * @param currentDir
     *            the current directory of the shell
     * @param rootDir
     *            the root directory of the file system
     */
    private String link;
    private String path;
    private Dir currentDir;
    private Dir rootDir;

    /**
     * constructor sets command specific variables
     */
    public Ln() {
        this.name = "ln";
        this.manual = "Makes one path a symbolic link to another.";
        this.error = "invalid arguements";
    }

    /**
     * execute the command specific functions
     *
     * @param param
     *            an array of arguments parsed from the command line
     * @param shell
     *            the current JShell instance
     * @return String null if nothing to print, otherwise the ouput of the
     *         command
     */
    public String execute(String[] param, JShell shell) {
        LinkedDocument symLink;
        String linkPath = link.substring(0, link.lastIndexOf("/") + 1);
        String linkName = link.substring(link.lastIndexOf("/") + 1);
        
        if (currentDir.searchDoc(path, rootDir) != null) {
            Document sourceDoc = currentDir.searchDoc(path, rootDir);
            symLink = new LinkedDocument(linkName, linkPath, sourceDoc, rootDir);
        } else {
            Dir sourceDir = currentDir.searchDir(path, rootDir);
            symLink = new LinkedDocument(linkName, linkPath, sourceDir, rootDir);
        }
        
        return null;
    }

    /**
     * sets the link and path for Ln class based on the parameters given
     *
     * @param param
     *            the parameters passed to Ln: param[0] is the desired path of
     *            the symbolic link, param[1] is the destination directory.
     * @param currentDir
     *            the current directory of the shell
     * @return void
     *
     */
    private void setPathsFromParameters(String[] param) {
        link = param[0];
        path = param[1];

        if (param[0].charAt(0) != '/') {
            if (currentDir.getFullPath().equals("/")) {
                link = "/" + param[0];
            } else {
                link = currentDir.getFullPath() + "/" + param[0];
            }
        }
        if (param[1].charAt(0) != '/') {
            if (currentDir.getFullPath().equals("/")) {
                path = "/" + param[1];
            } else {
                path = currentDir.getFullPath() + "/" + param[1];
            }
        }
        if (param[0].charAt(param[0].length() - 1) == '/'
                && !(link.equals("/"))) {
            link = link.substring(0, link.length() - 1);
        }
        if (param[1].charAt(param[1].length() - 1) == '/'
                && !(path.equals("/"))) {
            path = path.substring(0, path.length() - 1);
        }
    }

    /**
     * finds and returns whether or not parameters given in param are valid to
     * create a symbolic link
     *
     * @param param
     *            an array of arguments parsed from the command line
     * @param shell
     *            the current JShell instance
     * @return boolean true if parameters are valid, false otherwise
     */
    public boolean checkParameters(String[] param, JShell shell) {

        currentDir = shell.getCurDir();
        rootDir = shell.getRoot();

        if (param.length != 2) {
            error = "ln takes exactly 2 parameters.";
            return false;
        }

        setPathsFromParameters(param);

        if (!currentDir.checkPathExists(link, rootDir)
                && currentDir.checkPathExists(path, rootDir)
                && currentDir.checkPathExists(
                        link.substring(0, link.lastIndexOf("/") + 1),
                        rootDir)) {
            return true;
        }
        if (currentDir.checkPathExists(link, rootDir)) {
            error = "symbolic link path cannot already exist.";
            return false;
        }
        if (!currentDir.checkPathExists(
                link.substring(0, link.lastIndexOf("/") + 1), rootDir)) {
            error = "path to symbolic link does not exist.";
            return false;
        }
        if (!currentDir.checkPathExists(path, rootDir)) {
            error = "destination does not exist.";
            return false;
        }
        return false;
    }
}