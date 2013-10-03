import java.util.*;

/**
 * class contains tests for Cp class
 * 
 * @author Sean Gallagher
 */
public class CpTest {
	/**
	 * @param currentDir
	 *            the current directory of the virtual file system
	 * @param rootDir
	 *            the root directory of the virtual file system
	 * @param manTest
	 *            boolean set to true after manual test has been run
	 */
	private static Dir currentDir;
	private static Dir rootDir;
	private static boolean manTest = false;

	/**
	 * run tests for Cp with valid parameters
	 * 
	 * @param temp
	 *            a Cp instance used for testing
	 * @param param
	 *            an array containing the testing parameters
	 * @param s
	 *            a JShell object used to manage the file system
	 * 
	 * @return void
	 */
	public static void run(Cp temp, String[] param, JShell s) {
		currentDir = s.getCurDir();
		rootDir = s.getRoot();

		param = Arrays.copyOf(param, 2);

		if (!manTest) {
			System.out.println("Printing man page for cp...");
			temp.printMan();
			manTest = true;
		}

		System.out.println("Running checkParameters for cp...");
		assert (temp.checkParameters(param, s)) : "Valid parameters were not accepted";

		System.out.println("Running execute for cp...");
		temp.execute(param, s);

		System.out.println("Checking original still exists...");
		assert (currentDir.checkPathExists(param[0], rootDir)) : "Original directory/file no longer exists";

		System.out.println("Checking copy was created...");
		assert (currentDir.checkPathExists(
				param[1] + "/"
						+ param[0].substring(param[0].lastIndexOf("/") + 1),
				rootDir)) : "Copy does not exist";
	}

	/**
	 * test that invalid parameters are caught
	 * 
	 * @param temp
	 *            a Cp instance used for testing
	 * @param param
	 *            an array containing the testing parameters
	 * @param s
	 *            a JShell object used to manage the file system
	 * 
	 * @return void
	 */
	public static void runInvalid(Cp temp, String[] param, JShell s) {
		System.out.println("Running checkParameters for cp...");
		assert (!temp.checkParameters(param, s)) : "Did not catch invalid parameters";
	}

	/**
	 * creates the virtual file system for use in testing
	 * 
	 * @param s
	 *            a JShell object used to manage the file system
	 * 
	 * @return void
	 */
	public static void setupFileSystem(JShell s) {
		Dir root = new Dir("", ""); // create a file system
		Dir dir1 = new Dir("dir1", root.getFullPath());
		Dir dir2 = new Dir("dir2", root.getFullPath());
		Document doc1 = new Document("doc1", "/dir1");
		Document doc2 = new Document("doc2", "/dir2");
		dir1.addDoc(doc1);
		dir2.addDoc(doc2);
		root.addDir(dir1);
		root.addDir(dir2);
		s.setCurDir(root);
		s.setRoot(root);
	}

	/**
	 * sets up and runs the tests for Cp, and prints test statuses
	 * 
	 * @param args
	 *            standard main parameter
	 * 
	 * @return void
	 */
	public static void main(String[] args) {
		Cp temp = new Cp();

		JShell s = new JShell();

		setupFileSystem(s);

		// test cases
		String[][] tests = { { "/dir1", "/dir2", "pass" },
				{ "/dir1", "/dir3", "fail" },
				{ "/dir1/doc1", "/dir2", "pass" },
				{ "/dir1/doc1", "/dir2/doc1", "fail" } };

		for (int i = 0; i < tests.length; i++) {
			System.out.println("~~~~~~~~~~~~~~~~~~");
			System.out.println("Running test #" + Integer.toString(i));
			if (tests[i][2].equals("pass")) {
				run(temp, tests[i], s);
			} else {
				runInvalid(temp, tests[i], s);
			}
			System.out.println("Test #" + i + " passed.");
		}
	}

}