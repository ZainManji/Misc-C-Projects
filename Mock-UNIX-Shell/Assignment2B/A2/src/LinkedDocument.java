/**
 * Class handles symbolic linking. If accessed, returns the document or
 * directory linked to this one.
 *
 * @author Avraham Sherman, Sean Gallagher
 */
public class LinkedDocument extends Dir {
    /**
     * @param link
     *            the document to be linked
     * @param rootDir
     *            the root directory of the file system
     */
    private Document link = null;
    private Dir rootDir;

    /**
     * constructor, adds link to file system
     *
     * @param docName
     *            a string containing the name of the new LinkedDocument
     * @param docPath
     *            a string containing the path to the new LinkedDocument
     * @param linkedDoc
     *            the document or directory that this object will link to
     * @param rootDir
     *            the root directory of the file system
     */
    public LinkedDocument(String docName, String docPath, Document linkedDoc,
            Dir rootDir) {

        super(docName, docPath);
        this.link = linkedDoc;
        this.rootDir = rootDir;
        Dir parentDir = rootDir.searchDir(docPath, rootDir);
        
        if (parentDir.searchDoc(linkedDoc.getFullPath(), rootDir) != null)
            parentDir.addDoc(this);
        else
            parentDir.addDir(this);
    }

    /**
     * returns the document or directory linked through the symbolic link
     *
     * @return link the linked document/directory, or null if it no longer exists
     */
    public Document getLink() {
        if (rootDir.checkPathExists(this.link.getFullPath(), rootDir))
            return this.link;
        else
            return null;
    }

}