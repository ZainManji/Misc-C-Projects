/**
* Class Ls extends Command
* @author Zain Manji
*/
public class Ls extends Command {
    /**
    * constructor
    */
    public Ls()    {
        this.name = "ls";
        this.manual = "If no paths are given, prints the contents of the" +
        " current directory. If a path specifies a file, prints the " +
                "path. If a path specifies a directory, prints the path and the contents of that directory." +
                " If -R is present, recursively list sub-directories.";
    }

    /**
    * Execute the ls command
    * Returns the contents of current directory, if no paths specified,
    * or for each path specified in the order listed, returns the path
    * if it specifies a file, otherwise returns the contents of the path
    * if it is a directory. If -R is present, recursively list
    * sub-directories.
    *
    * @param param - the array of command line arguments
    * @param shell - the running shell
    * @return output - shell output as a string
    */
    public String execute(String [] param, JShell shell) {

        Dir curDir = shell.getCurDir();
        Dir root = shell.getRoot();
        Dir[] dirChildArray;
        Document[] docChildArray;
        String output = "";
        Boolean listSubDir = false;
        int index = 0;

        if (param.length == 0) { //print contents of the current directory
            docChildArray = getArrayofDocChildren(curDir);
            dirChildArray = getArrayofDirChldren(curDir);

            if (docChildArray.length == 0 && dirChildArray.length == 0) return null;
            output = getDirectoryContents(dirChildArray, docChildArray, "", true, output, listSubDir, curDir);
        }
        else if ((param.length == 1) && param[0].equals("-R")) {
            docChildArray = getArrayofDocChildren(curDir);
            dirChildArray = getArrayofDirChldren(curDir);
            listSubDir = true;
            output = getDirectoryContents(dirChildArray, docChildArray, "", true, output, listSubDir, curDir);
            index = 1;
        }
        else if (param[0].equals("-R"))    {
            listSubDir = true;
            index = 1;
        }

        while (index < param.length) {
            String path = param[index];

            if (path.equals("/")) { //print contents of the root directory
                docChildArray =  getArrayofDocChildren(root);
                dirChildArray = getArrayofDirChldren(root);
                output = getDirectoryContents(dirChildArray, docChildArray, path, false, output, listSubDir, root);
            }
            else { //print contents of the path given
                output = getPathContents(path, root, curDir, output, listSubDir, param.length, index);
            }
            index++;
        }

        if (output.endsWith("\n\n")) output = output.substring(0, output.length() - 1);
        return output;
    }

    /**
    * Returns the path if the path specifies a file, otherwise returns
    * the contents of the path if it specifies a directory
    *
    * @param path - the path string inputed by the user
    * @param root - the root directory
    * @param curDir - the current directory
    * @param output - the current output for the shell
    * @param listSubDir - true if needed to list sub directories, false otherwise
    * @param paramLength - length of the parameter array
    * @param index - the current index
    * @return output - the new output for the shell
    */
    private String getPathContents(String path, Dir root, Dir curDir, String output, Boolean listSubDir, int paramLength, int index) {

        String[] pathArray = path.split("/");
        String currentPath;
        Dir currentDir;

        if (path.startsWith("/")) {
            currentDir = root;
            currentPath = "";
        }
        else {
            currentDir = curDir;
            currentPath = currentDir.getFullPath();
        }

        Document [] docChildArray =  getArrayofDocChildren(currentDir);
        Dir [] dirChildArray = getArrayofDirChldren(currentDir);

        for (int y = 0; y < pathArray.length; y++) {
            currentPath = setCurrentPath(currentPath, pathArray[y]);

            for (int x = 0; x < dirChildArray.length; x++) {
                if (dirChildArray[x].getFullPath().equals(currentPath) && (y == (pathArray.length -1))) {
                    currentDir = dirChildArray[x];
                    docChildArray =  getArrayofDocChildren(dirChildArray[x]);
                    dirChildArray = getArrayofDirChldren(dirChildArray[x]);
                    output = getDirectoryContents(dirChildArray, docChildArray, path, false, output, listSubDir, currentDir);
                }
                else if (dirChildArray[x].getFullPath().equals(currentPath)) {
                    docChildArray =  getArrayofDocChildren(dirChildArray[x]);
                    dirChildArray = getArrayofDirChldren(dirChildArray[x]);
                }
            }

            for (int x = 0; x < docChildArray.length; x++) {
                if ((docChildArray[x].getFullPath()).equals(currentPath) && (y == (pathArray.length -1))) {
                    output = output.concat(path);

                    if (index < paramLength-1) output = output.concat("\n");
                }
            }
        }
        return output;
    }

    /**
    * Attach the next piece of the path to the current path correctly
    * and return the current path
    *
    * @param currentPath - the current path
    * @param pieceOfPath - the next piece of the path
    * @return currentPath - the new, correctly attached, current path
    */
    private String setCurrentPath(String currentPath, String pieceOfPath) {
        if (currentPath.endsWith("/")) {
            currentPath = currentPath + pieceOfPath;
        }
        else {
            currentPath = currentPath + "/" + pieceOfPath;
        }

        return currentPath;
    }

    /**
    * Return the array of the parent directory's document children
    *
    * @param dir - the parent directory
    * @return the array of the parent directory's document children
    */
    private Document[] getArrayofDocChildren(Dir dir) {
        return dir.getDocChildren().toArray(new Document[dir.getDocChildren().toArray().length]);
    }

    /**
    * Return an array of the parent directory's directory children
    *
    * @param dir - the parent directory
    * @return the array of the parent directory's directory children
    */
    private Dir[] getArrayofDirChldren(Dir dir)    {
        return (Dir[]) dir.getDirChildren().toArray(new Dir[dir.getDirChildren().toArray().length]);
    }

    /**
    * Returns the contents of the directory
    *
    * @param dirChildArray - array of directory children
    * @param docChildArray - array of document children
    * @param path - the path as a string
    * @param printCur - boolean indicating whether to print in the format specifically for the current directory
    * @param output - the current output for the shell
    * @param listSubDir - true if needed to list sub directories, false otherwise
    * @param currentDir - the current directory
    * @return output - new output for the shell
    */
    private String getDirectoryContents(Dir[] dirChildArray, Document[] docChildArray, String path, Boolean printCur, String output, Boolean listSubDir, Dir currentDir) {
        if (listSubDir) {
            output = listSubdirectories(output, currentDir, path, printCur);
        }
        else if (printCur) {
            for (int i = 0; i < dirChildArray.length; i++) {
                output = output.concat(dirChildArray[i].getName() + "\n");
            }

            for (int i = 0; i < docChildArray.length; i++) {
                output = output.concat(docChildArray[i].getName() + "\n");
            }

            output = output.substring(0, (output.length() - 1));
        }
        else {
            output = output.concat(path + ": ");

            for (int i = 0; i < dirChildArray.length; i++) {
                output = output.concat(dirChildArray[i].getName() + " ");
            }

            for (int i = 0; i < docChildArray.length; i++) {
                output = output.concat(docChildArray[i].getName() + " ");
            }

            output = output.concat("\n");

            if (!output.endsWith("\n\n")) output = output.concat("\n");
        }

        return output;
    }

    /**
    * Returns the contents of the directory, while getting the contents
    * of the sub directories
    *
    * @param output - current output for the shell
    * @param currentDir - the current directory
    * @param path - the path as a string
    * @param printCur - boolean indicating whether to print in the format specifically for the current directory
    * @return output - output for the shell
    */
    private String listSubdirectories(String output, Dir currentDir, String path, Boolean printCur)
    {
        Document [] docChildArray =  getArrayofDocChildren(currentDir);
        Dir [] dirChildArray = getArrayofDirChldren(currentDir);
        Boolean listSubDir = false;

        if (dirChildArray.length != 0) {
            output = getDirectoryContents(dirChildArray, docChildArray, path, printCur, output, listSubDir, currentDir);

            if (printCur && (dirChildArray.length > 0)) output = output.concat("\n");

            for (int i = 0; i < dirChildArray.length; i++) {
                printCur = false;
                currentDir = dirChildArray[i];
                output = listSubdirectories(output, currentDir, currentDir.getFullPath(), printCur);
            }
        }
        else {
            output = getDirectoryContents(dirChildArray, docChildArray, path, printCur, output, listSubDir, currentDir);
        }

        return output;
    }

    /**
    * Check if the parameter array can be used to execute the ls command
    *
    * @param param - the array of command line arguments
    * @param shell - the running JShell
    * @return true if parameters can be executed, false otherwise
    */
    public boolean checkParameters(String[] param, JShell shell) {
        Dir currentDir = shell.getCurDir();
        Dir rootDir = shell.getRoot();
        int count = 0;
        int i = 0;

        if (param.length >= 1) {
            if (param[0].equals("-R")) {
                    count++;
                    i++;
                }
        }

        while (i < param.length) {
            String path = param[i];
            String[] pathArray = path.split("/");

            if (path.equals("/")) {
                count++; //path is valid
            }
            else {
                if (path.endsWith("/"))    {
                    this.error = "Path(s) do not exist.";
                    return false;
                }

                count = countValidPaths(path, rootDir, currentDir, pathArray, count);
            }
            i++;
        }

        if (count == param.length) {
            return true;
        }
        else {
            this.error = "Path(s) do not exist.";
            return false;
        }
    }

    /**
    * Checks if the path given is an existing path in the mock
    * file system, and if so adds one to the and returns the
    * current number of valid paths
    *
    * @param path - the string of the path
    * @param rootDir - the root directory
    * @param currentDir - the current directory
    * @param pathArray - the array of pieces of the path
    * @param count - the current number of valid paths
    * @return count - the current number of valid paths
    */
    private int countValidPaths(String path, Dir rootDir, Dir currentDir, String [] pathArray, int count) {
        Dir curDir;
        String currentPath;

        if (path.startsWith("/")) {
            curDir = rootDir;
            currentPath = "";
        }
        else {
            curDir = currentDir;
            currentPath = curDir.getFullPath();
        }

        Document[] docChildArray = getArrayofDocChildren(curDir);
        Dir[] dirChildArray = getArrayofDirChldren(curDir);

        for (int y = 0; y < pathArray.length; y++) {
            currentPath = setCurrentPath(currentPath, pathArray[y]);

            for (int x = 0; x < docChildArray.length; x++) {
                if ((docChildArray[x].getFullPath()).equals(currentPath) && (y == (pathArray.length -1))) {
                    count++; //path is valid
                }
            }

            for (int x = 0; x < dirChildArray.length; x++) {
                if (dirChildArray[x].getFullPath().equals(currentPath) && (y == (pathArray.length -1))) {
                    count++; //path is valid
                }
                else if (dirChildArray[x].getFullPath().equals(currentPath)) {
                    curDir = dirChildArray[x];
                    docChildArray = getArrayofDocChildren(curDir);
                    dirChildArray = getArrayofDirChldren(curDir);
                }
            }
        }
        return count;
    }
}