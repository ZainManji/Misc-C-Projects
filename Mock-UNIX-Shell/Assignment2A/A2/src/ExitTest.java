import java.util.Arrays;

/**
* Class ExitTest
* @author Zain Manji
*/
public class ExitTest {
	
	static boolean manTest = false;
	/**
	* run the ExitTest
	*
	* @param temp the Exit object
	* @param param the parameters for the exit command
	* @param shell the running JShell
	* @return String the return status
	*/
	public static String run(Exit temp, String[] param, JShell shell) {
		if (!manTest) {  // test the printMan() method, but only once
			System.out.println("Printing man page for exit...");
			temp.printMan();
			manTest = true;
		}

		System.out.println("Running checkParmaters for exit...");
		if (!temp.checkParameters(param, shell)) {
			return "Failed checking paramaters on: " + Arrays.toString(param);
		}

		System.out.println("Running execute for exit...");
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
		Exit temp = new Exit();
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

		String[][] tests = {new String[0]}; //should pass

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