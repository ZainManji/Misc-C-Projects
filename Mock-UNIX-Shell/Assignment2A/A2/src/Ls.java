/**
* Class Ls extends Command
* @author Zain Manji
*/
public class Ls extends Command {
	/**
	* constructor
	*/
	public Ls()	{
		this.name = "ls";
		this.manual = "If no paths are given, prints the contents of the" +
		" current directory. If a path specifies a file, prints the " +
				"path. If a path specifies a directory, prints the path and the contents of that directory.";
	}
	
	/**
	* Execute the ls command
	* Prints contents of current directory, if no paths specified,
	* or for each path specified in the order listed, prints the path 
	* if it specifies a file, otherwise prints the contents of the path
	* if it is a directory
	*
	* @param param - the array of command line arguments
	* @param shell - the running shell
	* @return void
	*/
	public void execute(String [] param, JShell shell) {
		
		Dir curDir = shell.getCurDir();
		Dir root = shell.getRoot();
		Dir[] dirChildArray;
		Document[] docChildArray;
		
		if (param.length == 0) { //print contents of the current directory
			docChildArray = getArrayofDocChildren(curDir);
			dirChildArray = getArrayofDirChldren(curDir);
			printDirectory(dirChildArray, docChildArray, "", true);
		}
		
		for (int index = 0; index < param.length; index++) {		
			String path = param[index];
			
			if (path.equals("/")) { //print contents of the root directory
				docChildArray =  getArrayofDocChildren(root);
				dirChildArray = getArrayofDirChldren(root);
				printDirectory(dirChildArray, docChildArray, path, false);
			}
			else { //print contents of the path given
				printPathContents(path, root, curDir);
			}
		}		
	}
	
	/**
	* Prints the path if the path specifies a file, otherwise prints 
	* the contents of the path if it specifies a directory
	*
	* @param path - the path string inputed by the user
	* @param root - the root directory
	* @param curDir - the current directory
	* @return void
	*/
	private void printPathContents(String path, Dir root, Dir curDir) {
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
					docChildArray =  getArrayofDocChildren(dirChildArray[x]);
					dirChildArray = getArrayofDirChldren(dirChildArray[x]);
					printDirectory(dirChildArray, docChildArray, path, false);
				}
				else if (dirChildArray[x].getFullPath().equals(currentPath)) {
					docChildArray =  getArrayofDocChildren(dirChildArray[x]);
					dirChildArray = getArrayofDirChldren(dirChildArray[x]);
				}						
			}
				
			for (int x = 0; x < docChildArray.length; x++) {
				if ((docChildArray[x].getFullPath()).equals(currentPath) && (y == (pathArray.length -1))) {
					System.out.println(path);
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
	private Dir[] getArrayofDirChldren(Dir dir)	{
		return (Dir[]) dir.getDirChildren().toArray(new Dir[dir.getDirChildren().toArray().length]);
	}
	
	/**
	* Print the contents of the directory
	*
	* @param dirChildArray - array of directory children
	* @param docChildArray - array of document children
	* @param path - the path as a string
	* @param printCur - boolean indicating whether to print in the format specifically for the current directory
	* @return void
	*/
	private void printDirectory(Dir[] dirChildArray, Document[] docChildArray, String path, Boolean printCur) {
		if (printCur) {
			for (int i = 0; i < dirChildArray.length; i++) {
				System.out.println(dirChildArray[i].getName());
			}
			
			for (int i = 0; i < docChildArray.length; i++) {
				System.out.println(docChildArray[i].getName());
			}
		}
		else {
			System.out.print(path + ": ");
			
			for (int i = 0; i < dirChildArray.length; i++) {
				System.out.print(dirChildArray[i].getName() + " ");
			}
				
			for (int i = 0; i < docChildArray.length; i++) {
				System.out.print(docChildArray[i].getName() + " ");
			}
			
			System.out.println("\n");
		}
	}
	
	/**
	* Check if the parameter array can be used to execute the ls command
	*
	* @param param - the array of command line arguments
	* @param shell - the running JShell
	* @return boolean true if parameters can be executed
	*/
	public boolean checkParameters(String[] param, JShell shell) {  
		Dir currentDir = shell.getCurDir();
		Dir rootDir = shell.getRoot();
		int count = 0;
		
		for (int i = 0; i < param.length; i++) {
			String path = param[i];
			String[] pathArray = path.split("/");

			if (path.equals("/")) {
				count++; //path is valid
			}
			else {
				if (path.endsWith("/"))	{
					this.error = "Path(s) do not exist.";
					return false;
				}
				
				count = countValidPaths(path, rootDir, currentDir, pathArray, count);
			}
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