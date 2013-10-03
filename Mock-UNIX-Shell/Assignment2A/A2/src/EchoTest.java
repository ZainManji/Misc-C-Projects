//Program created on June 29, 2012
//Authors Avraham Sherman
//c2sherma
//Assignment2a CSC207

import java.util.Arrays;

/**
* Class EchoTest
* @author Avraham Sherman
*/
public class EchoTest
{
	/** @param manTest boolean set to true after manual test has been run*/
	static boolean manTest = false;
	/**
	* run the EchoTest
	*
	* @param temp the Echo object
	* @param param the parameters for the Echo command
	* @param main the current working directory
	* @param root the root directory
	* @return String the return status
	*/
	public static String run(Echo temp, String[] param, JShell shell) {
		if (!manTest) {  // test the printMan() method, but only once
			System.out.println("Printing man page for Echo...");
			temp.printMan();
			manTest = true;
		}

		System.out.println("Running checkParameters for Echo...");
		if (!temp.checkParameters(param, shell)) {
			return "Failed checking parameters on: " + Arrays.toString(param);
		}

		System.out.println("Running execute for Echo...");
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
		Echo temp = new Echo();
		String result = "";

		Dir root = new Dir("start", "");  // create a file system
		Dir middle = new Dir("middle", root.getPath() + root.getName());
		Dir cur = new Dir("end", middle.getPath() + middle.getName());

		JShell shell = new JShell();
		shell.setCurDir(cur);
		shell.setRoot(root);

  		// test cases
		String[][] tests = {{"hihi", "ok", ">>file"},   // should pass
							{"hihi", "ok", ">file"},    // should pass
							{"hihi", "ok", "file"},     // should pass
							{"hihi", ">ok", ">>file"},  // should fail
							{"hihi", "ok", ">file/k"}}; // should fail

		for (int i = 0; i < tests.length; i++) {  // run the tests
			try {
				System.out.println("~~~~~~~~~~~~~~~~~~");
				System.out.println("Running test #" + Integer.toString(i));
				result = run(temp, tests[i], shell);
				// test the contents of file if one was created
				if (cur.getDocChild("file") != null &&
						!cur.getDocChild("file").getContents().equals("hihi ok")) {
					System.out.println("File contents failed to print on: " +
														 Arrays.toString(tests[i]));
				}else {
					System.out.println(result);
				}
			}catch(Exception e) {
				System.out.println("Error with test case: " + Arrays.toString(tests[i]));
				System.out.println(e.getMessage());
			}
		}
	}



}