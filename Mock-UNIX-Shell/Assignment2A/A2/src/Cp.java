import java.util.*;

/**
 * class handles copying of files and directories within a virtual file system
 * 
 * @author Sean Gallagher
 */
public class Cp extends Command {
	/**
	 * @param oldPath
	 *            the path of the directory or file to be copied
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
	public Cp() {
		this.name = "cp";
		this.manual = "Copies an item from one path to another. If the item is"
				+ " a directory, its contents are copied recursively.";
		this.error = "invalid arguements";
	}

	/**
	 * executes the copy functionality on the given parameters
	 * 
	 * @param param
	 *            the parameters passed to Cp: param[0] is the path of the
	 *            file/directory to be copied, param[1] is the destination
	 *            directory.
	 * @param s
	 *            the current shell calling the command
	 * @return void
	 */
	public void execute(String[] param, JShell s) {
		String oldName = oldPath.split("/")[oldPath.split("/").length - 1];
		String oldPathNoName = oldPath.substring(0,
				oldPath.length() - oldName.length() - 1);

		Dir oldParDir = currentDir.findDir(oldPathNoName, rootDir);
		if (oldParDir.getDocChild(oldName) != null) {
			// triggered if oldPath refers to a document
			Document oldDoc = currentDir.findDoc(oldPath, rootDir);
			Document newDoc = new Document(oldDoc.getName(), newPath);
			newDoc.writeContents(oldDoc.getContents());
			Dir parentDir = currentDir.findDir(newPath, rootDir);
			parentDir.addDoc(newDoc);
		} else {
			Dir oldDir = currentDir.findDir(oldPath, rootDir);
			Dir newDir = new Dir(oldName, newPath);
			Dir parentDir = currentDir.findDir(newPath, rootDir);
			Dir checkExists = parentDir.getDirChild(oldName);
			if (checkExists == null)
				parentDir.addDir(newDir);
			duplicateTree(oldDir, newDir);
		}
	}

	/**
	 * recursively create a copy of oldDir's contents into newDir
	 * 
	 * @param oldDir
	 *            directory to be copied
	 * @param newDir
	 *            destination directory
	 * @return void
	 */
	public void duplicateTree(Dir oldDir, Dir newDir) {
		List<Document> newDocChildren = oldDir.getDocChildren();
		List<Dir> newDirChildren = oldDir.getDirChildren();

		for (int x = 0; x < newDocChildren.size(); x++) {
			if (newDir.getDocChild(newDocChildren.get(x).getName()) == null) {
				// Checks if directory already has a document with this name
				Document newDoc = new Document(newDocChildren.get(x).getName(),
						newDir.getFullPath());
				newDoc.writeContents(newDocChildren.get(x).getContents());
				newDir.addDoc(newDoc);
			}
		}

		for (int x = 0; x < newDirChildren.size(); x++) {
			Dir childDir = new Dir(newDirChildren.get(x).getName(),
					newDir.getFullPath());
			if (newDir.getDirChild(newDirChildren.get(x).getName()) == null) {
				// Checks if directory already has a document with this name
				newDir.addDir(childDir);
			}
			duplicateTree(oldDir.getDirChild(newDirChildren.get(x).getName()),
					childDir);
		}
	}

	/**
	 * sets the oldPath and newPath for Cp class based on the parameters given
	 * 
	 * @param param
	 *            the parameters passed to Cp: param[0] is the path of the
	 *            file/directory to be copied, param[1] is the destination
	 *            directory.
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
				&& !(newPath.equals("/"))) {
			newPath = newPath.substring(0, newPath.length() - 1);
		}
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
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * finds and returns whether or not a file to be copied already exists at
	 * the destination
	 * 
	 * @return boolean returns true if the file already exists at the
	 *         destination and false otherwise
	 */
	public boolean checkFileExists() {
		String oldName = oldPath.split("/")[oldPath.split("/").length - 1];
		String oldPathNoName = oldPath.substring(0,
				oldPath.length() - oldName.length() - 1);
		Dir oldParDir;

		if (currentDir.checkPathExists(oldPathNoName, rootDir)) {
			oldParDir = currentDir.findDir(oldPathNoName, rootDir);
			if (oldParDir.getDocChild(oldName) != null) {
				// triggered if oldPath refers to a document
				if (newPath.equals("/")
						&& currentDir.checkPathExists(newPath + "/" + oldName,
								rootDir)) {
					return true;
				} else if (currentDir.checkPathExists(newPath + "/" + oldName,
						rootDir)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * finds and returns whether or not the parameters given in param are valid
	 * for copying
	 * 
	 * valid parameters are paths that exist within the file system meeting the
	 * following conditions: 
	 * -the path of the source directory is not the root directory. 
	 * -the destination path is not a file
	 * -the destination is not the source
	 * -the source does not contain the destination
	 * -the destination file does not already exist
	 * 
	 * @param param
	 *            the parameters passed to Cp: param[0] is the path of the
	 *            file/directory to be copied, param[1] is the destination
	 *            directory.
	 * @param s
	 *            the current shell calling the command
	 * @return void
	 */
	public boolean checkParameters(String[] param, JShell s) {

		currentDir = s.getCurDir();
		rootDir = s.getRoot();

		if (param.length != 2) {
			error = "cp takes exactly 2 parameters.";
			return false;
		}
		if (param[0].equals("/")) {
			error = "cannot copy root directory.";
			return false;
		}

		setPathsFromParameters(param);

		if (oldPath.equals(newPath)) {
			error = "the destination is the same as the source.";
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
		if (checkFileExists()) {
			error = "file already exists";
			return false;
		}
		if (currentDir.checkPathExists(oldPath, rootDir)
				&& currentDir.checkPathExists(newPath, rootDir)) {
			return true;
		}
		return false;
	}
}
