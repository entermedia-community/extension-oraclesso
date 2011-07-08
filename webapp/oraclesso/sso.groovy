//check for a value in the header
//use that value to request information from from a url should return json
//parse the returned json

import javax.servlet.http.HttpServletRequest

import com.google.gson.Gson
import com.openedit.modules.update.Downloader
import com.openedit.users.User
import com.openedit.users.UserManager

public class Person
{
	public String email;
}

protected User createNewUser(Person inPerson)
{
	//create a user object
	//go through a list of teams they are on
	//look up each team
	//create team if it doesn't exist
	//add the user to the team/group
	return null;
}

protected String getContent(String inUrl)
{
	Downloader dl = new Downloader();
	return dl.downloadToString(inUrl);
}

protected void oracleSsoLogin()
{
	HttpServletRequest requestHeader = context.getRequest();
	String pid = requestHeader.getHeader("HBS_PERSON_ID");
	context.putSessionValue("personid", pid);
	//String tid = requestHeader.getHeader("teamid");  //could be multiple teamids, guess need to split these
	//String teaminfo = http://pine-stage.hbs.edu/teamMgmt/internal/ws.htm?action=getTeamInfo&teamId=498211;
	String personinfo = "http://pine-stage.hbs.edu/teamMgmt/internal/ws.htm?action=getTeamInfo&personId=615538";
	
	String jsonteaminfo;
	try
	{
		if(pid)
		{
			jsonteaminfo = getContent(personinfo + pid);
		}
		else
		{
			jsonteaminfo = getContent(personinfo);
		}
	}
	catch(Exception e)
	{
		context.putPageValue("ssoerror", "Failed to logon using SSO");
	}
	
	context.putPageValue("jsonresponse", jsonteaminfo);
	
	//Gson gson = new Gson();
	//Person person = gson.fromJson(jsonteaminfo, Person.class);
	
	//search for a user
	//UserManager um = userManager;
	//User user = um.getUserByEmail(person.email);
	//if(user == null)
	//{
	//	user = createNewUser(person);	
	//}
	
	//auto login user
	//context.putSessionValue("user", user);
}

oracleSsoLogin();