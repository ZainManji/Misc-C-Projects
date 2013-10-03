import java.io.*;
/**
* Class Document
* @author Sean Gallagher, Kent Liang, Avraham Sherman, Zain Manji
*/
public class Document implements Serializable{
    /**
    * @param name the name of the Document
    * @param path the path of the Document
    * @param contents the contents of the Document - default ""
    */
    private String name;
    private String path;
    private String contents = "";
    /**
    * constructor
    *
    * @param docName a string containing the name of the new Document
    * @param docPath a string containing the path to the new Document
    */
    public Document(String docName, String docPath) {
        name = docName;
        if (!docPath.equals("/")) {  // root case
            path = docPath + "/";
        } else {
            path = "/";
        }
    }

    /**
    * get the name of the document
    *
    * @return name a string containing the document name
    */
    public String getName() {  // return the name
        return this.name;
    }

    /**
    * get the path of the document
    *
    * @return path a string containing the document path
    */
    public String getPath() {  // return just the path to this object
        return this.path;
    }

    /**
    * get the full path of the document
    *
    * @return a string containing the path and name concatenated
    */
    public String getFullPath() {  // return the path + the name
        if ((path + name).equals("/")) {  // root
            return this.path;
        }else {
            return this.path + this.name;
        }
    }

    /**
    * set the path of the document
    *
    * @param docPath a string containing the document path
    * @return void
    */
    public void setPath(String docPath) {  // set the path
        this.path = docPath;
    }

    /**
    * set the contents of the document
    *
    * @param content the desired contents to be put in the document
    * @return void
    */
    public void setContents(String content) {
        this.contents = content;
    }

    /**
    * get the contents of the document
    *
    * @return contents a string containing the contents document
    */
    public String getContents() {  // return the contents
        return this.contents;
    }

    /**
    * overwrite the contents of document
    *
    * @param contents a string containing the new contents
    * @return void
    */
    public void overwriteContents(String contents){  // replace this.contents with contents
        this.contents = contents;
    }

    /**
    * append the contents of document
    * add a newline to the document and append the contents
    *
    * @param contents a string containing contents to append
    * @return void
    */
    public void writeContents(String contents){  // append contents to this.contents
        if (this.contents.length() > 0) {  // don't add new line if no contents
            this.contents += "\n" + contents;
        }else {
            this.contents = contents;
        }
    }

}
