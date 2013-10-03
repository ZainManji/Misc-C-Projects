//Program created on July 8, 2012
//Authors Shih-Chin Liang
//c2liangt
//Assignment2a CSC207

import java.util.Arrays;

public class CdTest {
	
	/**
	 *@param manTest boolean set to true after manual test has been run 
	 */
	private static boolean manTest = false;
	
	/**
	 * run the CdTest
	 *
	 * @param temp the Cd object
	 * @param param the parameters for the cd command
	 * @param shell JShell instance, the current working shell
	 * @return String the return status
	 */
	public static String run(Cd temp, String[] param, JShell shell, String expected) {
		if (!manTest) {
			System.out.println("Printing man page for cd...");
			temp.printMan();
			manTest = true;
		}
		System.out.println("Running checkParmaters for cd...");
		if (!temp.checkParameters(param, shell)) {
			return "Failed checking paramaters on: " + Arrays.toString(param);
		}
		System.out.println("Running execute for cd...");
		temp.execute(param, shell);
		if (shell.getCurDir().getFullPath().equals(expected)) {
			return "Successful on paramaters: " + Arrays.toString(param);
		}
		else {
			return "Failed on paramaters: " + Arrays.toString(param);
		}
	}
	
	/**
	 * Setup a virtual file system for testing
	 *
     * @param shell JShell instance, the current working shell
	 * @return void
	 */
	public static void setup(JShell shell){
		Dir rootDir = shell.getRoot();
		Dir dir1 = new Dir("dir1", "/");
		Dir dir2 = new Dir("dir2", "/dir1");
		rootDir.addDir(dir1);
		dir1.addDir(dir2);
	}
	
	/**
	 * main method
	 *
	 * @param args optional String arguments have no effect
	 * @return void
	 */
	public static void main(String[] args) {
		Cd temp = new Cd();
		String result = "";
		JShell shell = new JShell();
		setup(shell);
		// test cases
		String[][] tests = {{"."},//should pass
							{"dir1"}, // should pass
							{".."}, //should pass
							{"/"}, //should pass
							{"d#/"},//should failed in checkParameter for invalid variable
							{"dir1/dir6"},//should failed in checkParameter since dir6 doesn't exist
							{"/dir1/dir2"}};//should pass
		//expected value
		String [][] expected = {{"/"},
								{"/dir1"},
								{"/"},
								{"/"},
								{""},
								{"/dir1/dir6"},
								{"/dir1/dir2"}};
		
		for (int i = 0; i < tests.length; i++) {
			try {
				System.out.println("~~~~~~~~~~~~~~~~~~");
				System.out.println("Running test #" + Integer.toString(i+1));
				result = run(temp, tests[i], shell, expected[i][0]);
				System.out.println(result);
			}catch(Exception e) {
				System.out.println("Error with test case: " + Arrays.toString(tests[i]));
				System.out.println(e.getMessage());
			}
		}
	}
}
