/**
* Class JSLibraryTest
* @author Zain Manji
*/
public class JSLibraryTest {
	
	/**
     * Test the checkCommand method in JSLibrary.java
     *
     * @param args optional String arguments have no effect
     * @return void
     */
	public static void main (String[] args) {
		JSLibrary lib = new JSLibrary();
		System.out.println("Testing checkCommand in JSLibrary...");
		String[] testcases = {"cat", "cd", "cp", "echo", //should pass
								"exit", "ls", "mkdir", "mv", //should pass
								"pwd", "rm", "ln", "grep", //should pass
								"find", "get", //should pass
								"fail command", "anyotherstring", //should fail
								""}; //should fail
		for (int i = 0; i < testcases.length; i++) {
			System.out.println("~~~~~~~~~~~~~~~~~~");
			System.out.println("Running test #" + Integer.toString(i));
			Command result = lib.checkCommand(testcases[i]);
			if (result == null) {
				System.out.println("Error with test case: " + testcases[i]);
				System.out.println("Invalid command.");
			}
			else System.out.println("Test case: " + testcases[i] + " succesful.");
		}
	}
}