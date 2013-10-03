//Program created on June 29, 2012
//Authors Avraham Sherman
//c2sherma
//Assignment2a CSC207

/**
* Class PWDTest
* @author Avraham Sherman
*/
public class PWDTest
{
	/**
	* main method
	*
	* @param args optional String arguments have no effect
	* @return void
	*/
	public static void main(String[] args) {

		Command temp = new PWD();
		Dir root = new Dir("start", "");
		Dir middle = new Dir("middle", root.getPath() + root.getName());
		Dir cur = new Dir("end", middle.getPath() + middle.getName());

		JShell shell = new JShell();
		shell.setCurDir(cur);
		shell.setRoot(root);

		System.out.println("Printing man page for PWD.");
		temp.printMan();
		System.out.println("Executing command PWD. Should read: " +
											cur.getPath() + cur.getName());
		temp.execute(new String[0], shell);
	}

}