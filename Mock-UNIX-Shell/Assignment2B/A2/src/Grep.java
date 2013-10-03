import java.util.List;

// Program created on July 13 by Shih-Chin Liang
// Part of assignment 2b for CSC207


public class Grep extends Command {
	/**
	 *constructor
	 */
	public Grep() {
		this.name = "grep";
		this.manual = "-If -R is present and path is a directory traverse " +
				"the directory, for all lines in all files that contain REGEX, " +
				"print the path to the file (including the filename), " +
				"then a colon, " +
				"then the line that contained REGEX." +
				"-If -R is not present print any lines " +
				"containing REGEX in PATH, which must be a file.";
	}
	
	/**
	 * Execute the Grep command
	 * Recursively search path for file names containing
	 * the regular expression input by the user and print
	 * them one per line.
	 * 
	 * @param param the array of command line arguments
	 * @param shell JShell instance, the current working shell
	 * @return String result
	 */
	public String execute(String[] param, JShell shell) {
		String path;
		String result="";
		Document docs;
		Dir dir = shell.getRoot();
		String regex;
		if (param[0].equals("-R")){
			regex = param[1];
			for (int i = 2; i < param.length; i ++){
				path = dir.createPath(param[i], shell);
				dir = Dir.searchDir(path, shell.getRoot());
				result = traverse(result, regex, dir, shell);
			}
		}
		else{
			regex = param[0];
			for (int i = 1; i < param.length; i ++){
				path = dir.createPath(param[i], shell);
				docs = Dir.searchDoc(path, shell.getRoot());
				result = read(docs.getContents().split("\n"), regex, path, result);
			}
		}
		if (result.contains("\n")){
			result = result.substring(0, result.length()-1);
		}
		else if (result.equals("")){
			result = "No match found";
		}
		return result;
	}
	
	/**
	 * Read the content of the file
	 * Go through the content of the file and find any line that matches the input 
	 * regular expression
	 * 
	 * @param contents the String array of contents in a file
	 * @param regex String the regular expression
	 * @param path String current path
	 * @param result String the collection of line that matches regular expression
	 * @return String result
	 */
	private String read(String[] contents, String regex, String path, String result){
		for (int j = 0; j < contents.length; j ++){
			if (Regex.comparePart(regex, contents[j])){
				result += path + " : " + contents[j] + "\n";
			}
		}
		return result;
	}
	
	/**
	 * Traverse through the directory
	 * Recursively go through the sub-directory or only directory and 
	 * record the filename that matches the regular expression. 
	 * 
	 * @param result String the result of all line containing regular expression 
	 * @param regex String the regular expression
	 * @param dir Dir the current directory
	 * @param shell JShell instance, the current working shell
	 * @return String result
	 */
	private String traverse(String result, String regex, Dir dir, JShell shell){
		List <Document> doc;
		List <Dir> child;
		String path;
		doc = dir.getDocChildren();
		if (dir != null){
			doc = dir.getDocChildren();
			for (int j = 0; j < doc.size(); j ++){
				if (dir.getFullPath().equals("/"))
					path = dir.getFullPath() + doc.get(j).getName();
				else
					path = dir.getFullPath() + "/" + doc.get(j).getName();
				result = read(doc.get(j).getContents().split("\n"), regex, path, result);
			}
			child = dir.getDirChildren();
			for (int i = 0; i < child.size(); i ++){
				if (child.get(i).getDirChildren() != null)
					result = traverse(result, regex, child.get(i), shell);
			}
		}
		return result;
	}
	
	/**
	 * Check the parameter array used to execute the Grep command
	 * valid parameter meaning the input(s) 
	 * - the path have to exists
	 * - only -R is accepted
	 * - the regular expression have to be valid
	 *  
	 * @param param the array of command line arguments
	 * @param shell JShell instance, the current working shell
	 * @return boolean true if parameters can be executed
	 */
	public boolean checkParameters(String[] param, JShell shell) {
		Dir rootDir = shell.getRoot();
		String path;
		
		if (param[0].equals("-R")){
			if (!(param.length > 2)){
				this.error = "Grep can't take an empty directory path";
				return false;
			}
			if (!Regex.isRegex(param[1])){
				this.error = "Invalid regular expression " + param[1];
				return false;
			}
			for (int i = 2; i < param.length; i ++){
					path = rootDir.createPath(param[i], shell);
					if (Dir.searchDir(path, rootDir) == null){
						this.error = "The directory " + param[i] + " can't be found";
						return false;
					}
			}
		}
		else{
			if (!(param.length > 1)){
				this.error="Grep can't take an empty file path";
				return false;
			}
			if (!Regex.isRegex(param[0])){
				this.error = "Invalid regular expression " + param[1];
				return false;
			}
			for (int i = 1; i < param.length; i ++){
				path = rootDir.createPath(param[i], shell);
				if (Dir.searchDoc(path,rootDir) == null){
					if (Dir.searchDir(path, rootDir) != null)
						this.error = param[i] + " is a directory not a file";
					else
						this.error = "The file " + param[i] + " can't be found";
					return false;
				}
			}
		}
		return true;
	}
}
