// Program created on June 23 by Avraham Sherman
// Part of assignment 2a for CSC207
// echo string to standard out or textfile


/**
* Class Echo extends Command
* @author Avraham Sherman
*/
public class Echo extends Command {

	/** @param outType the output method for echo, set by checkParameters */
	private String outType;
	/**
	* constructor
	*/
    public Echo() {
        this.name = "echo";
        this.manual = "Prints input if no file specified with '>'. Creates or replaces " +
        "file prefixed with '>', appends to file prefixed with '>>'.";
    }

	/**
	* Execute the echo command
	* Print text to screen or file specified by '>' (overwrite)
	* or '>>' (append) format example: 'echo String >fileName'
	*
	* @param param the array of command line arguments
	* @param shell the JShell instance
	* @return void
	*/
    public void execute(String[] param, JShell shell) {
        String fileName;
        if (param.length == 0) {  // if no parameters specified
        	System.out.println();
        	return;
        }
        if (outType.equals("")) {
            for (int i = 0; i < param.length; i++) {
                System.out.print(param[i] + " ");
            }
            System.out.println();
        }else {
            fileName = clean(param[param.length - 1]);
            if (outType.equals(">")) { //overwrite
                overwrite(param, getFile(fileName, shell));
            }else {  // append
                append(param, getFile(fileName, shell));
            }
        }
    }

	/**
	* check the parameter array can be used to execute the echo command
	*
	* @param param the array of command line arguments
	* @param shell the current JShell instance
	* @return boolean true if parameters can be executed
	*/
	public boolean checkParameters(String[] param, JShell shell) {
        String fileName = "";
        String paramStr = outQuote(param, null);
        Dir temp = shell.getCurDir();
		int first = 0;
		int last = 0;
        outType = "";
        if (param.length == 0) {
        	return true;  // no parameters
        }
        paramStr = paramStr+ " " + param[param.length - 1];  // outQuote ignores last parameter

		if (paramStr.contains(">")) {
			first = paramStr.indexOf(">");
			last = paramStr.lastIndexOf(">");

			if  (first != last && first != last - 1) {
				this.error = "Exactly one '>' or '>>' per echo call.";
				return false;
			}else if (first == paramStr.length() - 1) {
				this.error = "Must specify file operator when redirecting output.";
				return false;
			}else {
				outType = paramStr.substring(first, last + 1);
				fileName = paramStr.substring(paramStr.lastIndexOf(" "), paramStr.length() - 1);
				fileName = clean(fileName);  // remove '>'

				if (temp.getDirChild(fileName) != null) {
            		this.error = fileName + ": is a directory";
            		return false;  // directory specified
            	}else if (paramStr.contains("/")) {
            		this.error = "Cannot have '/' as part of file name.";
            		return false;  // directory delimiter may not be part of name
            	}
            }
		}
		return true;
    }

	/**
	* return the file in the directory, create one if needed
	*
	* @param fileName the name of the Document
	* @param curDir the current working directory
	* @return newFile a file in the directory
	*/
    private Document getFile(String fileName, JShell shell) {
        Dir temp = shell.getCurDir();
        Document newFile = temp.getDocChild(fileName);

        if (newFile == null) {
        	newFile = new Document(fileName, temp.getPath() + temp.getName());
        	temp.addDoc(newFile);
        }
        return newFile;
    }

	/**
	* overwrite the contents of newFile
	*
	* @param newFile the Document being overwritten
	* @param param an array of text to be written
	* @return void
	*/
    private static void overwrite(String[] param, Document newFile) {
		newFile.overwriteContents(outQuote(param, ">"));
    }

	/**
	* append the contents to newFile
	*
	* @param newFile the Document being appended
	* @param param an array of text to be written
	* @return void
	*/
	private static void append(String[] param, Document newFile) {
		newFile.writeContents(outQuote(param, ">"));
	}

	/**
	* return a string without any '>'
	*
	* @param name a String that may contain '>'
	* @return name a String without any '>'
	*/
	private static String clean(String name) {
		while (name.substring(0, 1).equals(">")) {
			name = name.substring(1, name.length());
		}
		return name;
	}

	/**
	* return a string from param
	* return nothing after 'rem' char has been reached
	*
	* @param param an array of text to be converted
	* @param rem a character to remove, set null if no character to remove
	* @return toPrint a string of the contents of param
	*/
	private static String outQuote(String[] param, String rem) {
	    String toPrint = "";

        for (int i = 0; i < param.length - 1; i++) {
            if (rem != null && param[i].contains(rem)) {
            	break;  // nothing to return past rem
            }
            toPrint += param[i] + " ";
        }

        if  (toPrint.length() - 1 > 0) {  // ensure no index out of bounds error
        	// strip trailing whitespace
	        toPrint = toPrint.substring(0, toPrint.length() - 1) + "";
		}
		return toPrint;
	}
}