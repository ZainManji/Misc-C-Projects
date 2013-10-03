import java.util.Arrays;

/**
* Class CatTest
* @author Zain Manji
*/
public class CatTest {
	
	static boolean manTest = false;
	/**
	* run the CatTest
	*
	* @param temp the Cat object
	* @param param the parameters for the cat command
	* @param shell the running JShell
	* @return String the return status
	*/
	public static String run(Cat temp, String[] param, JShell shell) {
		if (!manTest) {  // test the printMan() method, but only once
			System.out.println("Printing man page for cat...");
			temp.printMan();
			manTest = true;
		}

		System.out.println("Running checkParameters for cat...");
		if (!temp.checkParameters(param, shell)) {
			return "Failed checking parameters on: " + Arrays.toString(param);
		}

		System.out.println("Running execute for cat...");
		temp.execute(param, shell);

		// only returns if no exception thrown until this point
		return "Successful on parameters: " + Arrays.toString(param);
	}
	
	/**
	* main method
	*
	* @param args optional String arguments have no effect
	* @return void
	*/
	public static void main(String[] args) {
		Cat temp = new Cat();
		JShell shell = new JShell();
		Dir root = new Dir("", "");  // create a file system
		Dir dir1 = new Dir("dir1", "/");
		Dir dir2 = new Dir("dir2", "/");
		Dir dir3 = new Dir("dir3", "/dir1");
		Dir dir4 = new Dir("dir4", "/dir1/dir3");
		Document doc1 = new Document("doc1", "/dir1");
		Document doc2 = new Document("doc2", "/dir2");
		Document doc3 = new Document("doc3", "/dir1/dir3");
		Document doc4 = new Document("doc4", "/dir1/dir3/dir4");
		doc1.overwriteContents("Doc1 Contents.");
		doc2.overwriteContents("Doc2 Contents.");
		doc3.overwriteContents("Doc3 Contents.");
		doc4.overwriteContents("Doc4 Contents.");
		dir4.addDoc(doc4);
		dir3.addDoc(doc3);
		dir1.addDoc(doc1);
		dir2.addDoc(doc2);
		dir3.addDir(dir4);
		dir1.addDir(dir3);
		root.addDir(dir1);
		root.addDir(dir2);		
		shell.setCurDir(dir1);
		shell.setRoot(root);

		String[][] tests = { new String[0], {"/dir1"}, {"/dir1/dir3"},   // should fail
							{"/dir1/dir3/doc3"}, {"/dir1/dir3/dir4/doc4"}, // should pass
							{"dir3/doc3"}, {"dir3/dir4/doc4"} , {"/dir1/doc1"}, {"doc1"}, //should pass
							{"dir3"}, {"/dir1/dir3/dir4"}, {"/dir1", "/", "doc1"}, // should fail
							{"dir3", "FAIL", "doc1"}, {"dir1", "/dir3"}, {"/dir3/dir4"},  // should fail
							{"dir3/dir4/"}}; // should fail

		for (int i = 0; i < tests.length; i++) {
			try {
				System.out.println("~~~~~~~~~~~~~~~~~~");
				System.out.println("Running test #" + Integer.toString(i));
				System.out.println(run(temp, tests[i], shell));
			}catch(Exception e) {
				System.out.println("Error with test case: " + Arrays.toString(tests[i]));
				System.out.println(e.getMessage());
			}
		}
	}
}