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
	 * @return cur the document or null if no exists
	 */
	public Document getDocChild(String name) { // returns the child document
												// named name
		Document cur;
		for (int i = 0; i < this.docChildren.size(); i++) {
			cur = docChildren.get(i);
			if (cur.getName().equals(name)) {
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
	 * @return cur the dir or null if no exists
	 */
	public Dir getDirChild(String name) { // returns the child directory named
											// name
		Dir cur;
		for (int i = 0; i < this.dirChildren.size(); i++) {
			cur = dirChildren.get(i);
			if (cur.getName().equals(name)) {
				return cur;
			}
		}
		return null;
	}

	/**
	 * check whether a path exists within the file system
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
					currentDir = dirChildList.get(x);
					dirChildList = currentDir.getDirChildren();
					found = true;
				}
			}
			if ((y == pathArray.length - 1) && !found) {
				List<Document> docChildList = currentDir.getDocChildren();
				for (int z = 0; z < docChildList.size(); z++) {
					if (docChildList.get(z).getFullPath().equals(path)) {
						found = true;
					}
				}
			}
		}

		if (path.equals("/"))
			found = true;
		return found;
	}

	/**
	 * Finds and returns a directory given the path to the directory. If the
	 * directory cannot be found, the directory with the closest path to the
	 * original will be returned
	 *
	 * @param path
	 *            the path of the desired directory
	 * @param rootDir
	 *            the root directory of the file system
	 * @return dirPath the directory with the given path, or the closest found
	 *         directory
	 */
	public Dir findDir(String path, Dir rootDir) {
		Dir curr = rootDir;
		Dir dirPath = curr;
		String currPath = "";
		String[] dirName = path.split("/");
		List<Dir> childArray;
		for (int j = 1; j < dirName.length; j++) {
			childArray = curr.getDirChildren();
			currPath = currPath + "/" + dirName[j];
			for (int i = 0; i < childArray.size(); i++) {
				if ((childArray.get(i)).getFullPath().equals(currPath)) {
					curr = childArray.get(i);
					dirPath = curr;
					break;
				}
			}
		}
		return dirPath;
	}

	/**
	 * finds and returns a document given the path to the document, or null if
	 * the document does not exist
	 *
	 * @param path
	 *            the path of the desired document
	 * @param rootDir
	 *            the root directory of the file system
	 * @return childArray[y] the document with the given path, or null otherwise
	 *
	 */
	public Document findDoc(String path, Dir rootDir) {
		String[] pathArray = path.split("/");
		int index = path.length() - pathArray[pathArray.length - 1].length()
				- 1;
		String parentPath = path.substring(0, index);
		Dir parentDir = rootDir.findDir(parentPath, rootDir);
		Document[] childArray = (Document[]) parentDir
				.getDocChildren()
				.toArray(
						new Document[parentDir.getDocChildren().toArray().length]);
		for (int y = 0; y < childArray.length; y++) {
			if (childArray[y].getFullPath().equals(path)) {
				return childArray[y];
			}
		}
		return null;
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
}
