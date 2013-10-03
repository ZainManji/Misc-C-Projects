import java.util.*;
import java.io.*;

/**
 * Class functions as a virtual terminal/file system. Takes input parameters and
 * parses them, then gives the parameters to JSLibrary.
 */
public class JShell implements Serializable{
    /**
     * @param root
     *            the root directory of the virtual file system
     * @param currentDir
     *            the directory current working directory of the terminal
     * @param outPath
     *            the string output redirection peth, null for standard out
     * @param outType
     *              the type of output redirection chosen: '>' overwrite, '>>' append
     */
    private Dir root;
    private Dir curDir;
    private String outPath = null;
    private String outType = null;

    /**
     * constructor creates the root directory of the virtual file system, and
     * sets the current directory to root.
     *
     * @return void
     */
    public JShell() {
        this.root = new Dir("", "");
        this.curDir = root;
    }

    /**
     * accepts user input and parses it split it and remove all spaces, then
     * returns the input as a list
     *
     * @param in
     *            the Scanner used to read user input
     * @Excepton
     *             JShellException throw an exception if an invalid paramater is provided
     * @return input a list containing the parsed user input
     */
    private List<String> parseParameters(Scanner in) throws JShellException {
        List<String> input;
        int first = 0;
        int last = 0;
        String param = in.nextLine();

        outPath = null;
        outType = null;

        if (param.length() != 0 && param.contains(">")) {
            first = param.indexOf(">");
            last = param.lastIndexOf(">");

            if (first != last && first != last - 1) {
                throw new JShellException("Exactly one '>' or '>>' may be used.");
            }else if (last == param.length() - 1) {
                throw new JShellException("Must specify path when redirecting output.");
            }else {
                outType = param.substring(first, last + 1);
                outPath = clean(param.substring(last + 1, param.length()));  // remove '>' and ' ' if attached
                if (outPath.contains("/") && outPath.indexOf("/") == 0 &&
                            Dir.searchDir(outPath, this.getRoot()) != null ||
                            Dir.searchDir(outPath, this.getCurDir()) != null) {
                    throw new JShellException(outPath + ": is a directory");  // directory specified exists
                }
                this.create(outPath);
            }
            if (param.indexOf(">") > param.indexOf("<")) {
                first = param.indexOf(">");
            }else if (param.contains(">")) {
                first = param.indexOf("<");
            }
            param = param.substring(0, first);  // disregard redirection for command calls

        }

         input = new LinkedList<String>(Arrays.asList(param
                                         .replaceAll("\\s+", " ").split(" ")));

        for (int x = input.size() - 1; x >= 0; x--) {
            if (input.get(x).equals(""))
                input.remove(x);
            else
                input.set(x, input.get(x).trim());
        }
        return input;
    }

    /**
     * Processes user input, and sends it to JSLibrary. If the input is a valid
     * command with valid arguements, it is executed, otherwise an error is
     * raised.
     *
     * @param cmd
     *            the command given by the user
     * @param args
     *            the arguements given by the user
     * @param lib
     *            an instance of JSLibrary
     *
     * @return void
     */
    private void executeCommand(String cmd, List<String> args, JSLibrary lib) throws JShellException {
        String[] params;
        String execution;
        boolean result;
        Dir root = this.getRoot();
        Dir cur = this.getCurDir();
        Command command = lib.checkCommand(cmd);

        if (cmd.equals("man")) { // man is a system level command that is
                                    // independent
                                    // of JSLibrary command inclusions.
            manCommand(cmd, args, lib);
        } else if (command != null) {
            params = (String[]) args.toArray(new String[args.size()]);
            result = command.checkParameters(params, this);

            if (result) {
                execution = command.execute(params, this);
                if (this.outType == null && execution != null) {
                    System.out.println(execution);
                }else if (execution == null) {  //skip
                }else if (outType.equals(">") && execution != null) {
                    if (outPath.contains("/") && outPath.indexOf("/") == 0) {
                        Dir.searchDoc(this.outPath, root).overwriteContents(execution);
                    }else if (outPath.contains("/")) {
                        Dir.searchDoc(this.outPath, cur).overwriteContents(execution);
                    }else {
                        cur.getDocChild(this.outPath).overwriteContents(execution);
                    }
                }else if (execution != null) {
                    if (outPath.contains("/") && outPath.indexOf("/") == 0) {
                        Dir.searchDoc(this.outPath, root).writeContents(execution);
                    }else if (outPath.contains("/")) {
                        Dir.searchDoc(this.outPath, cur).writeContents(execution);
                    }else {
                        cur.getDocChild(this.outPath).writeContents(execution);
                    }
                }
            } else {
                System.out.println(cmd +": " + command.getError());
            }
        } else if (!cmd.equals("")) {
            System.out.println(cmd + " is not a valid command.");
        }
    }

    /**
     * handles the processing of the man command
     *
     * @param cmd
     *            the command given by the user
     * @param args
     *            the arguements given by the user
     * @param lib
     *            an instance of JSLibrary
     *
     * @return void
     */
    private void manCommand(String cmd, List<String> args, JSLibrary lib) {
        Command command = lib.checkCommand(args.get(0));
        if (command != null && !args.get(0).equals("man")) {
            System.out.println(command.getMan());
        } else if (args.get(0).equals("man")) {
            System.out.println("Prints the documentation for a given command.");
        } else if (!cmd.equals("")) {
            System.out.println(args.get(0) + " is not a valid command.");
        }
    }

    /**
     * processes commands and arguements from user input, and passes them to
     * JSLibrary
     *
     * @return void
     */
    public void run() {
        List<String> input;
        String cmd;
        List<String> args;
        final Scanner in = new Scanner(System.in);
        JSLibrary lib = new JSLibrary();

        while (true) {
            System.out.print(this.getCurDir().getFullPath() + " # ");
            try {
                input = parseParameters(in);
            }catch (JShellException e) {
                System.out.println(e.getMessage());
                continue;  // get a new command
            }
            if (input.size() > 0) {
                cmd = input.get(0);
            } else {
                cmd = "";
            }

            if (input.size() > 1) {
                args = input.subList(1, input.size());
            } else {
                args = new ArrayList<String>();
            }
            try {
                executeCommand(cmd, args, lib);
            }catch (JShellExceptionQuit e) {
                return;
            }catch (JShellException e) {
                System.out.println(e.getMessage());
            }
        }
    }
    /**
     * create a file if it does not exist
     *
     * @param fileName name of Document to create
     * @return void
     */
    private void create (String fileName) throws JShellException {
        Document temp = null;
        Dir parentDir;
        String parentName;
        int lastSlash = 0;

        if (fileName.contains("/")) {
            temp = Dir.searchDoc(fileName, this.getRoot());
        }else {
            temp = curDir.getDocChild(fileName);
        }
        if (temp != null) {  // race case this
            return;
        }else {
            if (fileName.contains("/") && fileName.indexOf("/") == 0) {
                parentDir = this.getRoot();
                lastSlash = fileName.lastIndexOf("/");
                parentName = fileName.substring(0, lastSlash + 1);
                parentDir = Dir.searchDir(parentName, parentDir);
                if (parentDir != null) {
                    parentDir.addDoc(new Document(fileName.substring(lastSlash + 1,
                                                fileName.length()), parentDir.getFullPath()));
                }else {
                    throw new JShellException("Path " + parentName + " does not exist.");
                }
            }else if (fileName.contains("/")) {
                parentDir = this.getCurDir();
                lastSlash = fileName.lastIndexOf("/");
                parentName = fileName.substring(0, lastSlash + 1);
                parentDir = Dir.searchDir(parentName, parentDir);
                if (parentDir != null) {
                    parentDir.addDoc(new Document(fileName.substring(lastSlash + 1,
                                                fileName.length()), parentDir.getFullPath()));
                }else {
                    throw new JShellException("Path " + parentName + " does not exist.");
                }
            }else {
                parentDir = this.getCurDir();
                parentDir.addDoc(new Document(fileName, parentDir.getFullPath()));
            }
        }
    }

    /**
     * return the current directory
     *
     * @return this.curDir the current directory of the shell
     */
    public Dir getCurDir() {
        return this.curDir;
    }

    /**
     * return the root directory of the file system
     *
     * @return this.root the root directory of the file system
     */
    public Dir getRoot() {
        return this.root;
    }

    /**
     * sets the current directory
     *
     * @param t
     *            the desired current directory
     * @return void
     */
    public void setCurDir(Dir t) {
        this.curDir = t;
    }

    /**
     * sets the root directory
     *
     * @param t
     *            the desired root directory
     * @return void
     */
    public void setRoot(Dir t) {
        this.root = t;
    }

    /**
     * Creates a new JShell instance, and starts the terminal.
     *
     * @param args
     *            standard main parameter
     * @return void
     */

    /**
     * return a string without any '>'
     *
     * @param name a String that may contain '>'
     * @return name a String without any '>'
     */
    private static String clean(String name) {
        while (name.substring(0, 1).equals(">") || name.substring(0, 1).equals(" ") ) {
            name = name.substring(1, name.length());
        }
        return name;
    }

    public static void main(String[] args) {
        JShell shell = null;
        String toSerial = "n";

        // Aspects of Serializing taken from
        // http://www.java-samples.com/showtutorial.php?tutorialid=398
        BufferedReader in= new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.print("Would you like to load the previous JShell? y/n: ");
            toSerial = in.readLine();
        }catch (Exception e){}

        if (toSerial.equals("y")) {
            try {
                FileInputStream oldSerial = new FileInputStream("JShellSerialized");
                ObjectInputStream oldSerialStream = new ObjectInputStream(oldSerial);
                shell = (JShell)oldSerialStream.readObject();
                oldSerialStream.close();
            }catch (Exception e) {
                System.out.println("Error reading from JShellSerialized. The JShell initialized.");
            }
        }
        if (shell == null) {
            shell = new JShell();
        }
        shell.run();
        try {
            System.out.println("Serializing the JShell to JShellSerialized...");
            FileOutputStream newSerial = new FileOutputStream("JShellSerialized");
            ObjectOutputStream newSerialStream = new ObjectOutputStream(newSerial);
            newSerialStream.writeObject(shell);
            newSerialStream.flush();
            newSerialStream.close();
            System.out.println("Serialization complete.");
        }catch (Exception e) {
            System.out.println("Error serializing.");
        }
    }
}
