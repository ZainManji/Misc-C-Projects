//Program created on June 26, 2012
//Authors Avraham Sherman
//c2sherma
//Assignment2a CSC207

/**
* Class JSLibrary
* Maintain an array of commands
*
* @author Avraham Sherman
*/
public class JSLibrary
{
	/** @param cmdList the array of commands */
	public Command[] cmdList = new Command[9];
	/**
	* constructor
	* add 9 commands to cmdList
	*/
	public JSLibrary()
	{
		cmdList[0] = new Cat();
		cmdList[1] = new Cd();
		cmdList[2] = new Cp();
		cmdList[3] = new Echo();
		cmdList[4] = new Exit();
		cmdList[5] = new Ls();
		cmdList[6] = new Mkdir();
		cmdList[7] = new Mv();
		cmdList[8] = new PWD();
	}

	/**
	* check the command exists in cmdList
	*
	* @param cmd the name of the command
	* @return cmdList[i] the command or null depending on existence
	*/
	public Command checkCommand(String cmd)  // If the command object exists, return it.
	{
		for (int i = 0; i<cmdList.length; i++)
		{
			if ((cmdList[i].getName()).equals(cmd))
			{
				return cmdList[i];
			}
		}
		return null;  // if command does not exist
	}
}
