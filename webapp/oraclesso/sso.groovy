import java.util.Iterator

import javax.servlet.http.HttpServletRequest

import com.google.gson.Gson
import com.openedit.modules.update.Downloader
import com.openedit.users.Group
import com.openedit.users.User
import com.openedit.users.UserManager

public class Team
{
	public String id;
	public String location;
	public String publicName;
	public String prettyPublicName;
}

public class Status
{
	public String desc;
	public String ok;
}

public class TeamInfo
{
	public List<Team> teams;
	public Status status;
}

//go through list of teams in teaminfo
protected void generateTeams(User inUser, TeamInfo inTeamInfo)
{
	for (Iterator iterator = inTeamInfo.teams.iterator(); iterator.hasNext();) {
		Team team = (Team) iterator.next();
		//look up the group by name
		UserManager um = userManager;
		Group group = um.getGroup(team.id);
		if(group == null)
		{
			group = um.createGroup(team.id, team.publicName);
		}
		inUser.addGroup(group);
	}
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
	//pid = "641292";
	//pid = "615538";
	context.putPageValue("personid", pid);
	//String tid = requestHeader.getHeader("teamid");  //could be multiple teamids, guess need to split these
	//String teaminfo = "http://pine-stage.hbs.edu/teamMgmt/internal/ws.htm?action=getTeamInfo&teamId=498211";
	String personinfo = "http://pine-stage.hbs.edu/teamMgmt/internal/ws.htm?action=getTeamInfo&personId=";
	
	String jsonteaminfo;
	try
	{
		jsonteaminfo = getContent(personinfo + pid);
	}
	catch(Exception e)
	{
		context.putPageValue("ssoerror", "Failed to logon using SSO");
	}
	
	context.putPageValue("jsonresponse", jsonteaminfo);
	
	Gson gson = new Gson();
	TeamInfo teams = gson.fromJson(jsonteaminfo, TeamInfo.class);
		
	//search for a user
	UserManager um = userManager;
	User user = um.getUser(pid);
	if(user == null)
	{
		user = um.createUser(pid, null);
		Group guestgroup = um.getGroup("guest");
		user.addGroup(guestgroup);
	}
	
	generateTeams(user, teams);
	um.saveUser(user);
	
	//auto login user
	context.putSessionValue("user", user);
	context.redirect("/eml/index.html");
}

oracleSsoLogin();