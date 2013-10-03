import java.util.List;

/**
 * class handles moving of files and directories within a virtual file system
 * 
 * @author Sean Gallagher
 */
public class Mv extends Command {
	/**
	 * @param oldPath
	 *            the path of the directory or file to be moved
	 * @param newPath
	 *            the path of the destination directory
	 * @param currentDir
	 *            the current directory of the shell
	 * @param rootDir
	 *            the root directory of the file system
	 */
	private String oldPath;
	private String newPath;
	private Dir currentDir;
	private Dir rootDir;

	/**
	 * constructor sets command specific variables
	 */
	public Mv() {
		this.name = "mv";
		this.manual = "Moves an item from one path to another. If the item is"
				+ " a directory, its contents are moved recursively.";
		this.error = "invalid arguements";
	}

	/**
	 * executes the move functionality on the given parameters
	 * 
	 * @param param
	 *            the parameters passed to Mv: param[0] is the path of the
	 *            file/directory to be moved, param[1] is the destination
	 *            directory
	 * @param s
	 *            the current shell calling the command
	 * @return void
	 */
	public void execute(String[] param, JShell s) {

		String[] splitOldPath = oldPath.split("/");
		String oldName = splitOldPath[splitOldPath.length - 1];
		String oldPathNoName = oldPath.substring(0,
				oldPath.length() - oldName.length() - 1);

		Dir oldParDir = currentDir.findDir(oldPathNoName, rootDir);
		if (oldParDir.getDocChild(oldName) != null) { // if oldPath refers to a
														// document
			Document oldDoc = currentDir.findDoc(oldPath, rootDir);
			oldDoc.setPath(newPath + "/");
			Dir oldParentDir = currentDir.findDir(oldPathNoName, rootDir);
			Dir newParentDir = currentDir.findDir(newPath, rootDir);
			oldParentDir.removeDoc(oldDoc);
			newParentDir.addDoc(oldDoc);
		} else {
			Dir oldDir = currentDir.findDir(oldPath, rootDir);
			Dir oldParentDir = currentDir.findDir(oldPathNoName, rootDir);
			oldParentDir.removeDir(oldDir);
			Dir newParentDir = currentDir.findDir(newPath, rootDir);
			newParentDir.addDir(oldDir);
			correctPaths(oldDir, newPath + "/");
		}
	}

	/**
	 * sets the oldPath and newPath for Mv class based on the parameters given
	 * 
	 * @param param
	 *            the parameters passed to Mv: param[0] is the path of the
	 *            file/directory to be moved, param[1] is the destination
	 *            directory
	 * @param currentDir
	 *            the current directory of the shell
	 * @return void
	 * 
	 */
	public void setPathsFromParameters(String[] param) {
		oldPath = param[0];
		newPath = param[1];

		if (param[0].charAt(0) != '/') {
			if (currentDir.getFullPath().equals("/")) {
				oldPath = "/" + param[0];
			} else {
				oldPath = currentDir.getFullPath() + "/" + param[0];
			}
		}
		if (param[1].charAt(0) != '/') {
			if (currentDir.getFullPath().equals("/")) {
				newPath = "/" + param[1];
			} else {
				newPath = currentDir.getFullPath() + "/" + param[1];
			}
		}
		if (param[0].charAt(param[0].length() - 1) == '/') {
			oldPath = oldPath.substring(0, oldPath.length() - 1);
		}
		if (param[1].charAt(param[1].length() - 1) == '/'
				&& !newPath.equals("/")) {
			newPath = newPath.substring(0, newPath.length() - 1);
		}
	}

	/**
	 * finds are returns whether or not the destination path already exists
	 * 
	 * @return boolean true if path already exists and false otherwise
	 */
	public boolean checkDestinationExists() {
		String oldName = oldPath.substring(oldPath.lastIndexOf("/") + 1,
				oldPath.length());

		if (newPath.equals("/")
				&& currentDir.checkPathExists("/" + oldName, rootDir)) {
			return true;
		} else if (currentDir.checkPathExists(newPath + "/" + oldName, rootDir)) {
			return true;
		}
		return false;
	}

	/**
	 * finds and returns whether or not the destination path is a directory
	 * 
	 * @return boolean true if destination is a directory and false otherwise
	 */
	public boolean checkDestinationIsDir() {
		String newPathNoName;
		String newName;

		if (!newPath.equals("/")) {
			newPathNoName = newPath.substring(0, newPath.lastIndexOf("/") + 1);
			if (newPathNoName.length() == 0) {
				newPathNoName = "/";
			}

			newName = newPath.substring(newPath.lastIndexOf("/") + 1,
					newPath.length());

			if (currentDir.checkPathExists(newPathNoName, rootDir)) {
				Dir newParDir = currentDir.findDir(newPathNoName, rootDir);
				if (newParDir.getDocChild(newName) != null) {
					error = "the destination must be a directory.";
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * finds and returns whether or not the parameters given in param are valid
	 * for moving
	 * 
	 *  valid parameters are paths that exist within the file system
	 * meeting the following conditions: 
	 * -the path of the source directory is not the root directory 
	 * -the destination path is not a file 
	 * -the destination is not the source 
	 * -the path of the moved file/directory is not a currently existing path 
	 * -the source does not contain the destination
	 * 
	 * @param param
	 *            the parameters passed to Mv: param[0] is the path of the
	 *            file/directory to be moved, param[1] is the destination
	 *            directory.
	 * @param s
	 *            the current shell calling the command
	 * @return void
	 */
	public boolean checkParameters(String[] param, JShell s) {

		currentDir = s.getCurDir();
		rootDir = s.getRoot();

		if (param.length != 2) {
			error = "mv takes exactly 2 parameters.";
			return false;
		}
		if (param[0].equals("/")) {
			error = "cannot move root directory.";
			return false;
		}

		setPathsFromParameters(param);

		if (oldPath.equals(newPath)) {
			error = "the destination is the same as the source.";
			return false;
		}
		if (checkDestinationExists()) {
			error = "the destination already exists.";
			return false;
		}
		if (!checkDestinationIsDir()) {
			error = "the destination must be a directory.";
			return false;
		}
		if (newPath.contains(oldPath)) {
			error = "cannot copy a parent directory into its child.";
			return false;
		}
		if (currentDir.checkPathExists(oldPath, rootDir)
				&& currentDir.checkPathExists(newPath, rootDir)) {
			return true;
		}
		return false;
	}

	/**
	 * recursively changes the paths of all files and sub-directories of
	 * currentDir
	 * 
	 * @param currentDir
	 *            directory with path that needs to be corrected
	 * @param parentPath
	 *            the path to be applied to the directory
	 * @return void
	 */
	public void correctPaths(Dir currentDir, String parentPath) {
		currentDir.setPath(parentPath);
		List<Document> docChildren = currentDir.getDocChildren();
		List<Dir> dirChildren = currentDir.getDirChildren();

		for (int x = 0; x < docChildren.size(); x++) {
			docChildren.get(x).setPath(parentPath + currentDir.getName() + "/");
		}

		for (int x = 0; x < dirChildren.size(); x++) {
			correctPaths(dirChildren.get(x), parentPath + currentDir.getName()
					+ "/");
		}
	}
}