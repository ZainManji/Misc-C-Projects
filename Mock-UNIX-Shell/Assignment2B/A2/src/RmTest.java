import java.util.*;

/**
 * class contains tests for Rm class
 *
 * @author Sean Gallagher
 */
public class RmTest {
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
     * run tests for Rm with valid parameters
     *
     * @param temp
     *            a Rm instance used for testing
     * @param param
     *            an array containing the testing parameters
     * @param s
     *            a JShell object used to manage the file system
     *
     * @return void
     */
    private static void run(Rm temp, String[] param, JShell s) {
        currentDir = s.getCurDir();
        rootDir = s.getRoot();

        param = Arrays.copyOf(param, 1);

        if (!manTest) {
            System.out.println("Printing man page for rm...");
            System.out.println(temp.getMan());
            manTest = true;
        }

        System.out.println("Running checkParameters for rm...");
        assert (temp.checkParameters(param, s)) : "Valid parameters were not accepted";

        System.out.println("Running execute for rm...");
        temp.execute(param, s);

        System.out.println("Checking path no longer exists...");
        assert (!currentDir.checkPathExists(param[0], rootDir)) : "Path still exists";
    }

    /**
     * test that invalid parameters are caught
     *
     * @param temp
     *            a Rm instance used for testing
     * @param param
     *            an array containing the testing parameters
     * @param s
     *            a JShell object used to manage the file system
     *
     * @return void
     */
    private static void runInvalid(Rm temp, String[] param, JShell s) {
        System.out.println("Running checkParameters for rm...");
        assert (!temp.checkParameters(param, s)) : "Did not catch invalid parameters";
    }

    /**
     * creates the virtual file system for use in testing
     *
     * @param s
     *            a JShell object used to manage the file system
     *
     * @return void
     */
    private static void setupFileSystem(JShell s) {
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
     * sets up and runs the tests for Rm, and prints test statuses
     *
     * @param args
     *            standard main parameter
     *
     * @return void
     */
    public static void main(String[] args) {
        Rm temp = new Rm();

        JShell s = new JShell();

        setupFileSystem(s);

        // test cases
        String[][] tests = { { "/dir1", "pass" },
                             { "/dir3", "fail" },
                             { "/dir2/doc2", "pass" } };

        for (int i = 0; i < tests.length; i++) {
            System.out.println("~~~~~~~~~~~~~~~~~~");
            System.out.println("Running test #" + Integer.toString(i));
            if (tests[i][1].equals("pass")) {
                run(temp, tests[i], s);
            } else {
                runInvalid(temp, tests[i], s);
            }
            System.out.println("Test #" + i + " passed.");
        }
    }

}