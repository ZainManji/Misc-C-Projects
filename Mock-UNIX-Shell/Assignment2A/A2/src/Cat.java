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
	public void execute(String[] param, JShell s) {

		Dir currentDir = s.getCurDir();
		Dir root = s.getRoot();
		String path = param[0];
		String[] pathArray = path.split("/");

		if (pathArray.length < 2) {
			for (int i = 0; i < currentDir.getDocChildren().size(); i++) {
				if  (currentDir.getDocChildren().get(i).getName().equals(path)) { //file found
					System.out.println(currentDir.getDocChildren().get(i).getContents());
				}
			}
		}
		else {
			printAbsolutePathContents(pathArray, currentDir, path, root);
		}
	}
	
	/**
	* Print the contents of the absolute path given by the user
	*
	* @param pathArray - array of the pieces of the path string
	* @param currentDir - the current directory
	* @param path - the string of the path inputed by the user
	* @param root - the root directory
	* @return void
	*/
	private void printAbsolutePathContents(String [] pathArray, Dir currentDir, String path, Dir root) {
		Dir curDir;
		String currentPath;
		
		if (path.startsWith("/")) {
			curDir = root;
			currentPath = "";
		}
		else {
			curDir = currentDir;
			currentPath = curDir.getFullPath();
		}
		
		Dir[] dirChildArray = getArrayofDirChldren(curDir);
		Document[] docChildArray = getArrayofDocChildren(curDir);

		for (int y = 0; y < pathArray.length; y++) {
			currentPath = setCurrentPath(currentPath, pathArray[y]);

			for (int x = 0; x < docChildArray.length; x++) {
				if (docChildArray[x].getFullPath().equals(currentPath) && (y == (pathArray.length -1))) {
					System.out.println(docChildArray[x].getContents());
				}
			}

			for (int x = 0; x < dirChildArray.length; x++) {
				if (dirChildArray[x].getFullPath().equals(currentPath)) {
					docChildArray = getArrayofDocChildren(dirChildArray[x]);
					dirChildArray = getArrayofDirChldren(dirChildArray[x]);
				}
			}
		}
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
	private Dir[] getArrayofDirChldren(Dir dir) {
		return (Dir[]) dir.getDirChildren().toArray(new Dir[dir.getDirChildren().toArray().length]);
	}

	/**
	* Check if the parameter array can be used to execute the cat command
	*
	* @param param - the array of command line arguments
	* @param shell - the running JShell
	* @return boolean true if parameters can be executed
	*/
	public boolean checkParameters(String[] param, JShell shell) {

		Dir currentDir = shell.getCurDir();
		Dir rootDir = shell.getRoot();
		String currentPath;
		Dir curDir;
		
		if (param.length != 1) {
			this.error = "Must specify one File.";
			return false;
		}
		else {
			String path = param[0];
			String[] pathArray = path.split("/");
		
			if (path.startsWith("/")) {
				curDir = rootDir;
				currentPath = "";
			}
			else {
				curDir = currentDir;
				currentPath = curDir.getFullPath();
			}
			
			Dir[] dirChildArray = getArrayofDirChldren(curDir);
			Document[] docChildArray = getArrayofDocChildren(curDir);

			for (int y = 0; y < pathArray.length; y++) {
				currentPath = setCurrentPath(currentPath, pathArray[y]);

				for (int x = 0; x < docChildArray.length; x++) {
					if (docChildArray[x].getFullPath().equals(currentPath) && (y == (pathArray.length -1))) {
						return true; //path is valid
					}
				}

				for (int x = 0; x < dirChildArray.length; x++) {
					if (dirChildArray[x].getFullPath().equals(currentPath) && (y == (pathArray.length -1))) {
						this.error = path + " is a directory, not a file.";
						return false;
					}
					else if (dirChildArray[x].getFullPath().equals(currentPath)) {
						docChildArray = getArrayofDocChildren(dirChildArray[x]);
						dirChildArray = getArrayofDirChldren(dirChildArray[x]);
					}
				}
			}
			this.error = "No such file or directory.";
			return false;
		}
	}
}