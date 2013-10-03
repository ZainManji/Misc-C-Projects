//Program created on July 23, 2012
//Authors Avraham Sherman
//c2sherma
//Assignment2b CSC207

import java.util.*;

/**
* Class DirTest
* @author Avraham Sherman
*/
public class DirTest {
    /**
     * run the DirTest
     *
     * @return boolean true if successful, false otherwise
     */
    public static boolean run() {
        Dir testDir;
        System.out.println("Running tests on Dir.java...");
        boolean pass = true;
        testDir = new Dir("", "");
        pass = pass && testChildSetAndGet(testDir);
        pass = pass && testSearchDocAndDir(testDir);
        pass = pass && testGetDocAndDirChildren(testDir);
        pass = pass && testCheckPathExists(testDir);
        return pass;
    }

    /**
     * test the getDocChild and getDirChild Dir.java functions
     *
     * @param testDir the root directory with which tests are done
     * @return boolean true if successful, false otherwise
     */
    private static boolean testChildSetAndGet(Dir testDir) {
        boolean retVal= true;
        testDir.addDoc(new Document("newDoc", testDir.getPath()));
        testDir.addDir(new Dir("newDir", testDir.getPath()));

        if (!testDir.getDocChild("newDoc").getName().equals("newDoc")) {
            System.out.println("Error setting or getting newDoc.");
            retVal= false;
        }
        if (!testDir.getDirChild("newDir").getName().equals("newDir")) {
            System.out.println("Error setting or getting newDir.");
            retVal= false;
        }
        return retVal;
    }

    /**
     * test the searchDoc and searchDir Dir.java functions
     *
     * @param testDir the root directory with which tests are done
     * @return boolean true if successful, false otherwise
     */
    private static boolean testSearchDocAndDir(Dir testDir) {
        boolean retVal= true;
        Dir nested = testDir.getDirChild("newDir");
        nested.addDoc(new Document("nestedDoc", nested.getPath()));
        if (!Dir.searchDoc("/newDir/nestedDoc", testDir).getName().equals("nestedDoc")) {
            System.out.println("Error searching for nestedDoc in newDir.");
            retVal= false;
        }
        nested.addDir(new Dir("nestedDir", nested.getPath()));
        if (!Dir.searchDir("/newDir/nestedDir", testDir).getName().equals("nestedDir")) {
            System.out.println("Error searchign for nestedDir in newDir.");
            retVal= false;
        }
        return retVal;
    }

    /**
     * test the getDocChildren and getDirChildren Dir.java functions
     *
     * @param testDir the root directory with which tests are done
     * @return boolean true if successful, false otherwise
     */
    private static boolean testGetDocAndDirChildren(Dir testDir) {
        boolean retVal= true;
        List<Document> docChildren = testDir.getDocChildren();
        List<Dir> dirChildren = testDir.getDirChildren();
        if (!docChildren.get(0).getName().equals("newDoc")) {
            System.out.println("Inconsistency with docChildren.");
            retVal= false;
        }
        if (!dirChildren.get(0).getName().equals("newDir")) {
            System.out.println("Inconsistency with dirChildren.");
            retVal= false;
        }
        return retVal;
    }
    
    /**
     * test the checkPathExists function in Dir.java
     *
     * @param testDir the root directory with which tests are done
     * @return boolean true if successful, false otherwise
     */
    private static boolean testCheckPathExists(Dir testDir) {
        boolean retVal = true;
        try {
            testDir.addDoc(new Document("doc1", testDir.getPath()));
            Dir dir1 = new Dir("dir1", testDir.getPath());
            testDir.addDir(dir1);
            Dir dir2 = new Dir("dir2", dir1.getFullPath());
            dir1.addDir(dir2);
            dir1.addDoc(new Document("doc2", dir1.getFullPath()));
            
            assert(testDir.checkPathExists("/", testDir)) : "Did not find root";
            assert(testDir.checkPathExists("/dir1", testDir)) : "Can't find directories in root";
            assert(testDir.checkPathExists("/doc1", testDir)) : "Can't find documents in root";
            assert(testDir.checkPathExists("/dir1/dir2", testDir)) : "Can't find nested directories";
            assert(testDir.checkPathExists("/dir1/doc2", testDir)) : "Can't find nested documents";
            assert(!testDir.checkPathExists("/dir3", testDir)) : "Returned wrong boolean, directory does not exist";
            
            } catch (AssertionError e) {
                retVal = false;
            }
        return retVal;
    }

    /**
     * main method
     *
     * @param args optional String array not used
     */
    public static void main(String[] args) {
        if (!run()) {
            System.out.println("One or more tests failed.");
        }else {
            System.out.println("Tests successful.");
        }
    }

}