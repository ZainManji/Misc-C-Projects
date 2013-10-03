import java.util.*;
/**
* class contains tests for Cp class
* @author Sean Gallagher
*/
public class MvTest {
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
	 * run the tests for Mv
	 * 
	 * @param temp a Mv instance used for testing
	 * @param param an array containing the testing parameters
	 * @param s a JShell object used to manage the file system
	 * 
	 * @return void
	 */
	public static void run(Mv temp, String[] param, JShell s) {
		currentDir = s.getCurDir();
		rootDir = s.getRoot();

		param = Arrays.copyOf(param, 2);

		if (!manTest) {
			System.out.println("Printing man page for mv...");
			temp.printMan();
			manTest = true;
		}
		
		System.out.println("Running checkParameters for mv...");
		assert (temp.checkParameters(param, s)) : "Valid parameters were not accepted";
		
		System.out.println("Running execute for mv...");
		temp.execute(param, s);

		System.out.println("Checking original no longer exists...");
		assert (!currentDir.checkPathExists(param[0], rootDir)) : "Original directory/file still exists";
		
		System.out.println("Checking file/directory was successfully moved...");
		assert (currentDir.checkPathExists(
				param[1] + "/"
						+ param[0].substring(param[0].lastIndexOf("/") + 1),
				rootDir)) : "File/directory was not moved";
	}

	/**
	 * test that invalid parameters are caught
	 * 
	 * @param temp
	 *            a Mv instance used for testing
	 * @param param
	 *            an array containing the testing parameters
	 * @param s
	 *            a JShell object used to manage the file system
	 * 
	 * @return void
	 */
	public static void runInvalid(Mv temp, String[] param, JShell s) {
		System.out.println("Running checkParameters for mv...");
		assert (!temp.checkParameters(param, s)) : "Did not catch invalid parameters";
	}
	
	/**
	 * creates the virtual file system for use in testing
	 * 
	 * @param s a JShell object used to manage the file system
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
	 * sets up and runs the tests for Mv, and prints test statuses
	 * 
	 * @param args standard main parameter
	 * 
	 * @return void
	 */
	public static void main(String[] args) {
		Mv temp = new Mv();

		JShell s = new JShell();
		setupFileSystem(s);

		// test cases
		String[][] tests = { { "/dir1", "/dir2", "pass" },
				{ "/dir1", "/dir3", "fail" },
				{ "/dir2/dir1/doc1", "/dir2", "pass" },
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