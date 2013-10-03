import java.util.*;


// Program created on June 23 by Shih-Chin Liang
//Part of assignment 2a for CSC207
//Create directory by its relative path or absolute path 

public class Mkdir extends Command {
	
	/**
	 *constructor
	 */
	public Mkdir() {
		this.name = "mkdir";
		this.manual = "Create directories, " +
				"each of which may be relative" +
				" to the current directory or may be a full path.";
	}
	
	/**
	 * Execute the Mkdir command
	 * Create directory specified in relative path i.e test or test/apple(if test exist)
	 * or specified in full path i.e /test or /test/apple (if test exist)
	 * 
	 * @param param the array of command line arguments
	 * @param shell JShell instance, the current working shell
	 * @return void
	 */
	public void execute(String[] param, JShell shell) {
		String path;
		Dir root = shell.getRoot();
		
		for (int i = 0; i < param.length; i ++){
			if (param[i].contains("/")){
				path = root.createPath(param[i], shell);
				create(path, shell);
			}
			else{
				create(param[i], shell);
			}
		}	
	}
	
	/**
	 * Create the directory into the file system
	 *
	 * @param path String, the full path of the directory the user want to create
	 * @param shell JShell instance, the current working shell
	 * @return void
	 */
	public void create(String path, JShell shell) {
		String cur;
		String dirName;
		Dir dir;
		Dir curr = shell.getCurDir();
		
		if (path.contains("/")){//handle absolute path
			cur =  path.substring(0, path.lastIndexOf("/"));
			dirName = path.substring(path.lastIndexOf("/")+1);
			curr = curr.findDir(cur, shell.getRoot());
			dir = new Dir(dirName, cur);
			curr.addDir(dir);
		}
		else{//handle relative path
			cur = shell.getCurDir().getFullPath();
			dir = new Dir(path, cur);
			curr.addDir(dir);
		}
	}
	
	/**
	 * Check the parameter array used to execute the mkdir command
	 * valid parameter meaning the input(s) 
	 * - can't contain character other than a-z or A-Z and 1-9
	 * - have to be in the form of relative path (no "/" included)
	 * - have to be in the form of absolute path (/a1/a2) or (a1/a2) Note: a1 has to exist
	 * - can't have the same name as a file in the directory
	 * - the directory have not exist in the file system
	 *  
	 * @param param the array of command line arguments
	 * @param shell JShell instance, the current working shell
	 * @return boolean true if parameters can be executed
	 */
	public boolean checkParameters(String[] param, JShell shell) {
		String fullPath;
		String path;
		String dirName;
		List<String> paths = new ArrayList<String>();
		Dir curr = shell.getCurDir();
		
		for(int i = 0; i < param.length; i ++){
			fullPath = curr.createPath(param[i], shell);
			path = fullPath.substring(0,fullPath.lastIndexOf("/"));
			if (path.equals(""))
				path = "/";
			dirName = fullPath.substring(fullPath.lastIndexOf("/")+1);
			if((curr.findDir(path, curr)).getDocChild(dirName) != null){
				this.error = "There exists a file with the same name " + dirName;
				return false;
			}
			else if (!dirName.matches("[a-zA-Z1-9]*")){
				this.error = "The input " + dirName +  " contain invalid character";
				return false;
			}
			else if (i >= 1){
				if (!curr.checkPathExists(path, shell.getRoot()) && !paths.contains(path)){
					this.error = "The directory " + path.substring(path.lastIndexOf("/")+1) + " doesn't exist";
					return false;
				}
			}
			else if ((curr.checkPathExists(fullPath, shell.getRoot()))){
				this.error = "The directory " + dirName + " already exists.";
				return false;
			}
			else if (!curr.checkPathExists(path, shell.getRoot())){
				this.error = "The directory " + path.substring(path.lastIndexOf("/")+1) + " doesn't exist";
				return false;
			}
			paths.add(fullPath);
		}
		return true;
	}
}

