import java.net.*;
import java.io.*;

/**
* Class Get extends Command
* @author Zain Manji
*/
public class Get extends Command {
    /**
     *constructor
     */
    public Get() {
        this.name = "get";
        this.manual = "Retrieve the file at the specified URL and add it to the current directory.";
    }


    /**
    * Execute the get command
    * Retrieve the file at the specified URL and add it to the current directory
    *
    * @param param - the array of command line arguments
    * @param shell - the running shell
    * @return output - shell output as a string
    */
    public String execute(String[] param, JShell shell) {

        String contents = "";
        Dir currentDir = shell.getCurDir();

        try {
            URL website = new URL(param[0]);
            String [] urlSegments = param[0].split("/");
            String filename = urlSegments[urlSegments.length-1];
            BufferedReader in = new BufferedReader(new InputStreamReader(website.openStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) contents = contents + inputLine + "\n";

            in.close();
            Document newDoc = new Document(filename, currentDir.getFullPath());
            newDoc.overwriteContents(contents);
            currentDir.addDoc(newDoc);
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    /**
    * Check if the parameter array can be used to execute the get command
    *
    * @param param - the array of command line arguments
    * @param shell - the running JShell
    * @return true if parameters can be executed, false otherwise
    */
    public boolean checkParameters(String[] param, JShell shell) {

        if (param.length > 1) return false;

        try {
            URL website = new URL(param[0]);
        } catch (Exception e) {
            this.error = "Specified URL is invalid.";
            return false;
        }

        return true;
    }
}
