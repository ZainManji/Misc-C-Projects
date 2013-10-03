import java.util.Arrays;

/**
* Class LsTest
* @author Zain Manji
*/
public class LsTest {

    static boolean manTest = false;
    /**
    * run the LsTest
    *
    * @param temp the Ls object
    * @param param the parameters for the ls command
    * @param shell the running JShell
    * @return String the return status
    */
    public static String run(Ls temp, String[] param, JShell shell) {
        if (!manTest) {  // test the printMan() method, but only once
            System.out.println("Printing man page for ls...");
            System.out.println(temp.getMan());
            manTest = true;
        }

        System.out.println("Running checkParameters for ls...");
        if (!temp.checkParameters(param, shell)) {
            return "Failed checking parameters on: " + Arrays.toString(param);
        }

        System.out.println("Running execute for ls...");
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
        Ls temp = new Ls();
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

        String[][] tests = { new String[0], {"/dir1"}, {"/dir1/dir3"},   // should pass
                            {"-R"}, {"-R", "/dir1"}, {"-R", "/dir1/dir3"}, // should pass
                            {"-R", "dir3"}, {"-R", "/dir1/dir3/dir4"}, {"-R", "/dir1/doc1"}, //should pass
                            {"dir3"}, {"/dir1/dir3/dir4"}, {"/dir1/doc1"},    // should pass
                            {"-R", "dir3/doc3"}, {"-R", "dir3/dir4/doc4"}, {"-R", "/dir1", "/", "doc1"},
                            {"dir3/doc3"}, {"dir3/dir4/doc4"}, {"/dir1", "/", "doc1"}, // should pass
                            {"dir3/dir4", "-R", "/dir1/dir3/doc3", },// should fail
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