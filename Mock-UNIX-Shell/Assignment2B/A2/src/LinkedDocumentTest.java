/**
 * Class tests the LinkedDocument class
 *
 * @author Sean Gallagher
 */
public class LinkedDocumentTest {
	/**
     * Test the getLink method in LinkedDocument.java
     *
     * @param args - String arguments
     * @return void
     */
    public static void main (String[] args) {
    	System.out.println("Testing getLink...");
        Dir testDir = new Dir("", "");
        Dir dir1 = new Dir("dir1", testDir.getPath());
        testDir.addDir(dir1);
        LinkedDocument link = new LinkedDocument("doc2", "/", dir1, testDir);
        
        try {
            assert(link.getLink() == dir1) : "Link returns incorrect directory";
            testDir.removeDir(dir1);
            assert(link.getLink() == null) : "Link returns something instead of null";
            System.out.println("Testing was successful.");    
        } catch (AssertionError e) {
            System.out.println("Testing failed.");
        }
    }
}