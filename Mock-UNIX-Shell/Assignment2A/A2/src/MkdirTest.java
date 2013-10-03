//Program created on July 8, 2012
//Authors Shih-Chin Liang
//c2liangt
//Assignment2a CSC207

import java.util.Arrays;

public class MkdirTest {
	
	/**
	 *@param manTest boolean set to true after manual test has been run 
	 */
	private static boolean manTest = false;
	
	/**
	 * run the MkdirTest
	 *
	 * @param temp the Mkdir object
	 * @param param the parameters for the mkdir command
	 * @param shell JShell instance, the current working shell
	 * @return String the return status
	 */
	public static String run(Mkdir temp, String[] param, JShell shell, String expected) {
		String path = shell.getRoot().createPath(param[0], shell);
		if (!manTest) {
			System.out.println("Printing man page for mkdir...");
			temp.printMan();
			manTest = true;
		}
		System.out.println("Running checkParmaters for mkdir...");
		if (!temp.checkParameters(param, shell)) {
			return "Failed checking paramaters on: " + Arrays.toString(param);
		}
		System.out.println("Running execute for mkdir...");
		temp.execute(param, shell);
		if (shell.getRoot().findDir(path, shell.getRoot()).getFullPath().equals(expected)){
			return "Successful on paramaters: " + Arrays.toString(param);
		}
		else{
			return "Failed on paramaters: " + Arrays.toString(param);
		}
	}
	
	/**
	 * main method
	 *
	 * @param args optional String arguments have no effect
	 * @return void
	 */
	public static void main(String[] args) {
		Mkdir temp = new Mkdir();
		String result = "";
		JShell shell = new JShell();
		// test cases
		String[][] tests = {{"dir1"},//should pass
							{"/dir2/"}, // should pass
							{"dir3/"}, //should pass
							{"/dir4"}, //should pass
							{"/dir?/"},//should failed invalid variable
							{"dir5/dir6"},//should failed since dir5 doesn't exist
							{"/dir1/dir2"}};//should pass
		//expected value
		String [][] expected = {{"/dir1"},
								{"/dir2"},
								{"/dir3"},
								{"/dir4"},
								{""},
								{"/dir5/dir6"},
								{"/dir1/dir2"}};
		
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