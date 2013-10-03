import java.util.*;

/**
 * Class functions as a virtual terminal/file system. Takes input parameters and
 * parses them, then gives the parameters to JSLibrary.
 */
public class JShell {
	/**
	 * @param root
	 *            the root directory of the virtual file system
	 * @param currentDir
	 *            the directory current working directory of the terminal
	 */
	private Dir root;
	private Dir curDir;

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
	 * 
	 * @return input a list containing the parsed user input
	 */
	public List<String> parseParameters(Scanner in) {
		List<String> input = new LinkedList<String>(Arrays.asList(in.nextLine()
				.replaceAll("\\s+", " ").split(" ")));
		for (int x = input.size() - 1; x >= 0; x--) {
			if (input.get(x).equals("")) {
				input.remove(x);
			} else {
				input.set(x, input.get(x).trim());
			}
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
	public void executeCommand(String cmd, List<String> args, JSLibrary lib) {
		String[] params;
		boolean result;
		Command command = lib.checkCommand(cmd);

		if (cmd.equals("man")) { // man is a system level command that is
									// independent
									// of JSLibrary command inclusions.
			manCommand(cmd, args, lib);
		} else if (command != null) {
			params = (String[]) args.toArray(new String[args.size()]);
			result = command.checkParameters(params, this);
			if (result) {
				command.execute(params, this);
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
	public void manCommand(String cmd, List<String> args, JSLibrary lib) {
		Command command = lib.checkCommand(args.get(0));
		if (command != null && !args.get(0).equals("man")) {
			command.printMan();
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
			System.out.print(this.curDir.getFullPath() + " # ");
			input = parseParameters(in);

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

			executeCommand(cmd, args, lib);
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
	public static void main(String[] args) {
		JShell shell = new JShell();
		shell.run();
	}
}
