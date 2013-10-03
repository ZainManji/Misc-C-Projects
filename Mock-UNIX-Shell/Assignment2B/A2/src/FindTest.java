import java.util.Arrays;

//Program created on July 16, 2012
//Authors Shih-Chin Liang
//c2liangt
//Assignment2b CSC207


public class FindTest {

	
	/**
	 *@param manTest boolean set to true after manual test has been run 
	 */
	private static boolean manTest = false;
	
	/**
	 * run the FindTest
	 *
	 * @param temp the Find object
	 * @param param the parameters for the Find command
	 * @param shell the JShell object(current working shell)
	 * @return String the return status
	 */
	private static String run(Find temp, String[] param, JShell shell, String expected) {
		String result;
		if (!manTest) {
			System.out.println("Printing man page for Find...");
			System.out.println(temp.getMan());
			manTest = true;
		}
		System.out.println("Running checkParmaters for Find...");
		if (!temp.checkParameters(param, shell)) {
			return "Failed checking paramaters on: " + Arrays.toString(param);
		}
		System.out.println("Running execute for Find...");
		result = temp.execute(param, shell);
		if (expected == null && result == null){
			return "Successful on paramaters: " + Arrays.toString(param);
		}
		else if (result.equals(expected)){
			return "Successful on paramaters: " + Arrays.toString(param);
		}
		else{
			return "Failed on paramaters: " + Arrays.toString(param);
		}
	}
	
	/**
	 * Setup a virtual file system for testing
	 *
	 * @param shell the JShell object(current working shell)
	 * @return void
	 */
	private static void setup(JShell shell){
		Dir rootDir = shell.getRoot();
		Dir dir1 = new Dir("dir1", "/");
		Dir dir11 = new Dir("dir11", "/");
		Dir dir111 = new Dir("dir111", "/");
		Dir dir2 = new Dir("dir2", "/dir1");
		Dir dir22 = new Dir("dir22", "/dir11");
		Dir dir3 = new Dir("dir3", "/dir1/dir2");
		Dir dir33 = new Dir("dir33", "/dir11/dir22");
		Document file1 = new Document("f1", "/");
		Document file2 = new Document("QQQ", "/dir1");
		Document file3 = new Document("qqq", "/dir111");
		Document file4 = new Document("file my1love", "/dir11/dir22");
		Document file5 = new Document("file your2love", "/dir11/dir22/dir33");
		//build directory
		rootDir.addDir(dir1);
		rootDir.addDir(dir11);
		rootDir.addDir(dir111);
		dir1.addDir(dir2);
		dir11.addDir(dir22);
		dir2.addDir(dir3);
		dir22.addDir(dir33);
		//add file
		rootDir.addDoc(file1);
		dir1.addDoc(file2);
		dir111.addDoc(file3);
		dir22.addDoc(file4);
		dir33.addDoc(file5);
	}
	
	
	/**
	 * main method
	 *
	 * @param args optional String arguments have no effect
	 * @return void
	 */
	public static void main(String[] args) {
		Find temp = new Find();
		String result = "";
		JShell shell = new JShell();
		setup(shell);
		// test cases
		String[][] tests = {{""},//parameter check 1 should fail
							{"[a-z]*"}, //parameter check 2 should fail
							{"[a-z]*", "/dir"}, //parameter check 3 should fail
							{"[a-z]*", "/dir1/dir"},//parameter check 4 should fail
							{"?", "/dir1/dir2"},//parameter check 5 should fail(wrong regular expression)
							{"[a-z]*", "/dir1"}, //parameter check 6 normal case should pass
							{"[a-z]*","/dir1/dir2"},//parameter check 7 normal case should pass
							{"@", "/dir1"},//output1 invalid filename
							{"Q","/","/dir111"},//output2 case sensitive or not
							{"file", "/"},//output3 ability to handle multiple file names match 
							{"f", "/dir1"}, //output 4 an existed filename in a different directory
							{"1", "/", "/dir1"},//output 5 multiple file path
							};
		//expected value
		String [][] expected = {{""},// parameter check 1
								{""},// parameter check 2
								{""},// parameter check 3
								{""},// parameter check 4
								{""},// parameter check 5
								{"/dir1/QQQ"},// parameter check 6
								{"No such file is found"},// parameter check 7
								{"No such file is found"},// output check 1 should be blank
								{"/dir1/QQQ"},//output check 2 should pass
								{"/dir11/dir22/file my1love\n/dir11/" +
										"dir22/dir33/file your2love"},//output check 3 should pass
								{"No such file is found"},//output 4should be blank
								{"/f1\n/dir11/dir22/file my1love"},//output 5 
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
