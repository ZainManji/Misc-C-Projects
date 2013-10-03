import java.util.*;

/**
 * Class Dir extends Document
 *
 * @author Sean Gallagher, Kent Liang, Avraham Sherman, Zain Manji
 */
public class Dir extends Document {
    /**
     * @param docChildren the documents contained in the directory
     * @param dirChildren the directories contain in the directory
     */
    private List<Document> docChildren = new ArrayList<Document>();
    private List<Dir> dirChildren = new ArrayList<Dir>();

    /**
     * constructor
     *
     * @param dirName
     *            a string containing the name of the new directory
     * @param dirPath
     *            a string containing the path to the new directory
     */
    public Dir(String dirName, String dirPath) {
        super(dirName, dirPath);
    }

    /**
     * remove a document from the children list
     *
     * @param doc
     *            the document to remove
     * @return void
     */
    public void removeDoc(Document doc) {
        this.docChildren.remove(doc);
    }

    /**
     * remove a dir from the children list
     *
     * @param directory
     *            the directory to remove
     * @return void
     */
    public void removeDir(Dir dir) {
        this.dirChildren.remove(dir);
    }

    /**
     * add a document to the children list
     *
     * @param doc
     *            the document to add
     * @return void
     */
    public void addDoc(Document doc) {
        this.docChildren.add(doc);
    }

    /**
     * add a directory to the children list
     *
     * @param dir
     *            the directory to add
     * @return void
     */
    public void addDir(Dir dir) {
        this.dirChildren.add(dir);
    }

    /**
     * return the list of document children
     *
     * @return get the docChildren list
     */
    public List<Document> getDocChildren() {
        return this.docChildren;
    }

    /**
     * return the list of directory children
     *
     * @return get the dirChildren list
     */
    public List<Dir> getDirChildren() {
        return this.dirChildren;
    }

    /**
     * return the document child if it exists
     *
     * @param name
     *            the name of the target document
     * @return cur the document, or the linked document if child is a symlink, or null if no exists
     */
    public Document getDocChild(String name) { // returns the child document
                                                // named name
        Document cur;
        for (int i = 0; i < this.docChildren.size(); i++) {
            cur = docChildren.get(i);
            if (cur.getName().equals(name)) {
                if (cur instanceof LinkedDocument) { //checks if document is a symlink
                    LinkedDocument sym = (LinkedDocument)cur;
                    return sym.getLink();
                }
                return cur;
            }
        }
        return null;
    }

    /**
     * return the dir child if it exists
     *
     * @param name
     *            the name of the target dir
     * @return cur the dir, or the linked directory if child is a symlink or null if no exists
     */
    public Dir getDirChild(String name) { // returns the child directory named
                                            // name
        Dir cur;
        for (int i = 0; i < this.dirChildren.size(); i++) {
            cur = dirChildren.get(i);
            if (cur.getName().equals(name)) {
                if (cur instanceof LinkedDocument) { //checks if document is a symlink
                    LinkedDocument sym = (LinkedDocument)cur;
                    cur = (Dir)sym.getLink();
                }
                return cur;
            }
        }
        return null;
    }

    /**
     * finds and returns if the object is a no longer working symbolic link
     * 
     * @param input the directory or document to be checked
     * @return boolean false if input is a broken symbolic link and true otherwise
     */
    private boolean checkSymLink(Document input) {
        if (input instanceof LinkedDocument) {
            LinkedDocument sym = (LinkedDocument)input;
            
            if (sym.getLink() == null) {
                System.out.println("Warning: path exists but is invalid");
                return false;
            }
        }
        return true;
    }
    
    /**
     * check whether a path exists within the file
     *
     * @param path
     *            the path to be checked
     * @param rootDir
     *            the root directory of the file system
     * @return boolean returns true if path exists and false otherwise
     */
    public boolean checkPathExists(String path, Dir rootDir) {
        String[] pathArray = path.split("/");
        String currentPath = "";
        Dir currentDir = rootDir;
        List<Dir> dirChildList = currentDir.getDirChildren();
        boolean found = false;

        for (int y = 1; y < pathArray.length; y++) {
            found = false;
            currentPath = currentPath + "/" + pathArray[y];
            
            for (int x = 0; x < dirChildList.size(); x++) {
                if (dirChildList.get(x).getFullPath().equals(currentPath)) {
                    found = checkSymLink(dirChildList.get(x));
                    currentDir = dirChildList.get(x);
                    dirChildList = currentDir.getDirChildren();
                }
            }
            if ((y == pathArray.length - 1) && !found) {
                List<Document> docChildList = currentDir.getDocChildren();
                
                for (int z = 0; z < docChildList.size(); z++) {
                    if (docChildList.get(z).getFullPath().equals(path))
                        found = checkSymLink(docChildList.get(z));
                }
            }
        }
        if (path.equals("/"))
            found = true;

        return found;
    }

    /**
     * Return a path from user input
     *
     * @param param String, command line argument
     * @param shell JShell instance, the current working shell
     * @return String path after execution
     */
    public String createPath(String param, JShell shell){
        String [] filename;
        String fullPath;
        if (param.contains("/")){
            if (param.substring(0,1).equals("/")){
                fullPath = shell.getRoot().getFullPath();
                param = param.substring(1,param.length());
            }
            else{
                fullPath = shell.getCurDir().getFullPath();
            }
        }
        else{
            fullPath = shell.getCurDir().getFullPath();
        }
        filename = param.split("/");
        for (int j = 0; j < filename.length; j++){
            if (fullPath.equals("/"))
                fullPath = fullPath + filename[j] + "/";
            else
                fullPath = fullPath + "/" + filename[j] + "/";
            fullPath = fullPath.substring(0, fullPath.lastIndexOf("/"));
        }
        return fullPath;
    }
    /**
     * finds and returns a Dir given the path to the Dir, or null if
     * the document does not exist
     * recursive function
     * must be called with a "/" in path
     *
     * @param path
     *            the path of the desired document
     * @param root
     *            the root directory to be searched
     * @return Dir or null depending on found status
     *
     */
    public static Dir searchDir(String path, Dir root) {
        String[] pathArray;
        Dir comp;
        if (path.equals("/")) {
            return root;
        }

        if (path.indexOf("/") == 0) {
            path = path.substring(1, path.length());  // remove leading "/"
        }

        pathArray = path.split("/");

        comp = root.getDirChild(pathArray[0]);
        if (comp != null && pathArray.length > 1) {  // recursively search
            return searchDir(path.substring(pathArray[0].length(), path.length()), comp);
        }else if (comp == null) {
            return null;  // not found
        }else {  // base case (comp != null && path.Array.length == 1)
            return comp;
        }
    }
    /**
     * finds and returns a document given the path to the document, or null if
     * the document does not exist
     * must be called with a "/" in path
     *
     * @param path
     *            the path of the desired document
     * @param root
     *            the root directory of the file system
     * @return Document or null depending on found status
     *
     */
    public static Document searchDoc(String path, Dir root) {
        Dir comp = searchDir(path.substring(0, path.lastIndexOf("/") + 1), root); //need +1??
        if (comp == null) {
            return null;
        }
        return comp.getDocChild(path.substring(path.lastIndexOf("/") + 1, path.length()));

    }
}
