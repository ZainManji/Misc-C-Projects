import java.util.*;

/**
 * class handles removing of files and directories within a virtual file system
 *
 * @author Sean Gallagher
 */
public class Rm extends Command {
    /**
     * @param currentDir
     *            the current directory of the shell
     * @param rootDir
     *            the root directory of the file system
     * @param paths
     *            a list of parsed input containing paths to be removed
     * @param force
     *            boolean that is used to determine whether or not to delete a
     *            given file or directory. Will not delete if false.
     * @param reader
     *            a scanner to read user input
     */
    private Dir currentDir;
    private Dir rootDir;
    private String[] paths;
    private boolean force = false;
    final Scanner reader = new Scanner(System.in);

    /**
     * constructor sets command specific variables
     */
    public Rm() {
        this.name = "rm";
        this.manual = "Given confirmation, removes a file or directory "
                + "from the file system. If a directory is removed, its "
                + "contents are recursively removed. Use -f to bypass "
                + "asking for confirmation.";
        this.error = "invalid arguements";
    }

    /**
     * execute the remove functionality on the given parameters
     *
     * @param param
     *            an array of arguments parsed from the command line
     * @param shell
     *            the current JShell instance
     * @return null as the command does not require output.
     */
    public String execute(String[] param, JShell shell) {
        Dir rmDir = null;
        Document rmDoc;
        Dir rmParent;

        for (int x = 0; x < paths.length; x++) {
            rmDir = currentDir.searchDir(paths[x], rootDir);
            if (rmDir != null) {
                rmParent = getParent(rmDir);
                removeChildren(rmDir);
                if (rmDir.getDocChildren().size() == 0
                        && rmDir.getDirChildren().size() == 0) {
                    if (getConfirmation(rmDir)) {
                        rmParent.removeDir(rmDir);
                    }
                } else {
                    return "Did not remove " + rmDir.getName()
                            + " as it is not empty.";
                }
            } else {
                rmDoc = currentDir.searchDoc(paths[x], rootDir);
                if (rmDoc != null) {
                    if (getConfirmation(rmDoc)) {
                        rmParent = getParent(rmDoc);
                        rmParent.removeDoc(rmDoc);
                    }
                }
            }
        }
        return null;
    }

    /**
     * confirms with the user whether or not to remove a given document or
     * directory, unless the force command has been given
     *
     * @param rmDoc
     *            the directory or document to be removed
     * @return boolean true if it has been confirmed that document or directory
     *         is to be deleted
     */
    private boolean getConfirmation(Document rmDoc) {
        String input;
        
        if (force)
            return true;
        else {
        	
            while (true) {
                System.out.println("Are you sure you wish to remove "
                        + rmDoc.getName() + "? [Y/n]");
                input = reader.nextLine().trim().toLowerCase();

                if (input.equals("y") || input.equals("yes")) {
                    return true;
                } else if (input.equals("n") || input.equals("no")) {
                    return false;
                }
                System.out
                        .println("Invalid input, please choose either Y or N.");
            }
        }
    }

    /**
     * given a directory or document, return its parent directory
     *
     * @param child
     *            the child directory or document
     * @return parent the parent directory
     */
    private Dir getParent(Document child) {
        Dir parent;
        if (child.getPath().equals("/"))
            parent = currentDir.searchDir(child.getPath(), rootDir);
        else
            parent = currentDir.searchDir(
                    child.getPath().substring(0, child.getPath().length() - 1),
                    rootDir);
        return parent;
    }

    /**
     * recursively removes all directories and documents contained within a
     * given directory
     *
     * @param rmDir
     *            the directory to be emptied
     * @return void
     */
    private void removeChildren(Dir rmDir) {
        List<Document> docChildren = rmDir.getDocChildren();
        List<Dir> dirChildren = rmDir.getDirChildren();

        for (int x = docChildren.size() - 1; x >= 0; x--) {
            if (getConfirmation(docChildren.get(x))) {
                rmDir.removeDoc(docChildren.get(x));
            }
        }

        for (int z = dirChildren.size() - 1; z >= 0; z--) {
            removeChildren(dirChildren.get(z));
            if (getConfirmation(dirChildren.get(z))) {
                if (dirChildren.get(z).getDocChildren().size() == 0
                        && dirChildren.get(z).getDirChildren().size() == 0) {
                    rmDir.removeDir(dirChildren.get(z));
                }
            }
        }
    }

    /**
     * converts a relative path into an absolute path, and removes a trailing
     * '/' if there is one
     *
     * @param param
     *            a String containing the path to be formatted
     * @return param a String containing the formatted path
     */
    private String formatParam(String param) {

        if (param.charAt(0) != '/') {
            if (currentDir.getFullPath().equals("/")) {
                param = "/" + param;
            } else {
                param = currentDir.getFullPath() + "/" + param;
            }
        }
        if (param.charAt(param.length() - 1) == '/') {
            param = param.substring(0, param.length() - 1);
        }
        return param;
    }

    /**
     * finds and returns whether or not parameters given in param are valid to
     * remove
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
        paths = param;

        if (param.length < 1) {
            error = "rm takes at least 1 parameter.";
            return false;
        }
        if (param[0].equals("-f")) {
            force = true;
            if (param.length == 1) {
                error = "no directories or files specified.";
                return false;
            }
            paths = Arrays.copyOfRange(param, 1, param.length);
        }

        for (int x = 0; x < paths.length; x++) {
            if (paths[x].equals("/")) {
                error = "cannot remove root directory.";
                return false;
            } else {
                paths[x] = formatParam(paths[x]);
                if (!currentDir.checkPathExists(paths[x], rootDir)) {
                    error = paths[x] + " is not a valid path.";
                    return false;
                }
            }
        }
        return true;
    }
}