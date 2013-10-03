import java.util.Arrays;

/**
* Class GetTest
* @author Zain Manji
*/
public class GetTest {

    static boolean manTest = false;
    /**
    * run the GetTest
    *
    * @param temp the Get object
    * @param param the parameters for the get command
    * @param shell the running JShell
    * @return String the return status
    */
    public static String run(Get temp, String[] param, JShell shell) {
        if (!manTest) {  // test the printMan() method, but only once
            System.out.println("Printing man page for get...");
            System.out.println(temp.getMan());
            manTest = true;
        }

        System.out.println("Running checkParameters for get...");
        if (!temp.checkParameters(param, shell)) {
            return "Failed checking parameters on: " + Arrays.toString(param);
        }

        System.out.println("Running execute for get...");
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
        Get temp = new Get();
        JShell shell = new JShell();
        Dir root = new Dir("", "");  // create a file system
        Dir dir1 = new Dir("dir1", "/");
        root.addDir(dir1);
        shell.setCurDir(dir1);
        shell.setRoot(root);

        String[][] tests = { new String[0], {"/dir1"}, {"www.google.ca"}, // should fail
                            {"http://jahmalgittens.com/", "https://www.google.ca/"}, // should fail
                            {"http://jahmalgittens.com/"}, {"https://www.google.ca/"}}; // should pass

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