import java.util.Arrays;

//Program created on July 16, 2012
//Authors Shih-Chin Liang
//c2liangt
//Assignment2b CSC207


public class GrepTest {

	
	/**
	 *@param manTest boolean set to true after manual test has been run 
	 */
	private static boolean manTest = false;
	
	/**
	 * run the GrepTest
	 *
	 * @param temp the Grep object
	 * @param param the parameters for the Grep command
	 * @param shell the JShell object(current working shell)
	 * @return String the return status
	 */
	private static String run(Grep temp, String[] param, JShell shell, String expected) {
		String result;
		if (!manTest) {
			System.out.println("Printing man page for Grep...");
			System.out.println(temp.getMan());
			manTest = true;
		}
		System.out.println("Running checkParmaters for grep...");
		if (!temp.checkParameters(param, shell)) {
			System.out.println(temp.error);
			return "Failed checking paramaters on: " + Arrays.toString(param);
		}
		
		System.out.println("Running execute for grep...");
		result = temp.execute(param, shell);
		if (result.equals(expected))
			return "Successful on paramaters: " + Arrays.toString(param);
		else
			return "Failed on paramaters: " + Arrays.toString(param);
	}
	
	/**
	 * Setup a virtual file system for testing
	 * Since it is for Grep, therefore it includes multiple files.
	 *
     * @param shell the JShell object(current working shell)
	 * @return void
	 */
	private static void setup(JShell shell){
		Dir rootDir = shell.getRoot();
		Dir dir1 = new Dir("dir1", "/");
		Dir dir2 = new Dir("dir2", "/dir1");
		Document file1 = new Document("hello", "/");
		Document file2 = new Document("world", "/dir1");
		Document file3 = new Document("yep", "/dir1/dir2");
		rootDir.addDir(dir1);
		rootDir.addDoc(file1);
		dir1.addDir(dir2);
		dir1.addDoc(file2);
		dir2.addDoc(file3);
		file1.writeContents("The");
		file1.writeContents("Lord");
		file1.writeContents("is");
		file1.writeContents("my");
		file1.writeContents("sheppard;");
		file2.writeContents("I");
		file2.writeContents("shall");
		file2.writeContents("not");
		file2.writeContents("want");
		file2.writeContents("hea!@$!WADS");
	}
	
	/**
	 * main method
	 *
	 * @param args optional String arguments have no effect
	 * @return void
	 */
	public static void main(String[] args) {
		Grep temp = new Grep();
		String result = "";
		JShell shell = new JShell();
		String content = "/dir1/world : I\n" +
				"/dir1/world : shall\n" +
				"/dir1/world : not\n" +
				"/dir1/world : want\n" +
				"/dir1/world : hea!@$!WADS";
		String content2 = "/hello : The\n" +
				"/hello : Lord\n" +
				"/hello : is\n" +
				"/hello : my\n" +
				"/hello : sheppard;";
		setup(shell);
		//test cases
		String[][] tests = {{""},//parameter check 1 should fail
							{"-R"}, // parameter check 2 should fail
							{"-r", "/dir1"}, //parameter check 3 should fail
							{"-R", "/dir1"}, // parameter check 4 should fail
							{"-R", "?","/dir1"}, // parameter check 5 should fail
							{"/dir1"}, //parameter check 6 should fail
							{"/dir1/hello"}, //parameter check 7 should fail
							{"?", "/dir1/hello" },//parameter check 8 should fail
							{"-R", "[a-z]*","/"}, //output check 1 normal case -R
							{"[a-z]*","/dir1/world" },//output check 2 normal case
							{"yep", "/dir1/dir2/yep"},//output check 3 empty file
							};
		//expected value
		String [][] expected = {{""},//parameter check 1
								{""},//parameter check 2
								{""},//parameter check 3
								{""},//parameter check 4
								{""},//parameter check 5
								{""},//parameter check 6
								{""},//parameter check 7
								{""},//parameter check 8
								{content2 +"\n" + content},//output check 1
								{content},//output check 2
								{"No match found"},//output check 3
								};
		
		for (int i = 0; i < tests.length; i++) {
			try {
				System.out.println("~~~~~~~~~~~~~~~~~~");
				System.out.println("Running test #" + Integer.toString(i+1));
				result = run(temp, tests[i], shell, expected[i][0]);
				System.out.println(result);
			}catch(final Exception e) {
				System.out.println("Error with test case: " + Arrays.toString(tests[i]));
				System.out.println(e.getMessage());
			}
		}
	}
}
