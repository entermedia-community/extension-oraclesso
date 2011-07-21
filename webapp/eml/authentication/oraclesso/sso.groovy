import java.util.Iterator;

import java.util.Iterator

import javax.servlet.http.HttpServletRequest;

import org.openedit.data.Searcher;
import org.openedit.data.SearcherManager;

import com.openedit.modules.update.Downloader
import com.openedit.users.Group
import com.openedit.users.User
import com.openedit.users.UserManager
import com.entermedia.soap.SoapUserManager;
import com.google.gson.Gson 

public class Student
{
	public String lastName;
	public String email;
	public String personId;
	public String firstName;
}

public class Team
{
	public String id;
	public String location;
	public String publicName;
	public String prettyPublicName;
	public List<Student> studentList;
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

protected SearcherManager getSearcherManager()
{
	SearcherManager sm = (SearcherManager)moduleManager.getBean("searcherManager");
	return sm;
}

//go through list of teams in teaminfo
protected void generateTeams(User inUser, TeamInfo inTeamInfo)
{
	Searcher groupSearcher = getSearcherManager().getSearcher("system", "group");
	Searcher userSearcher = getSearcherManager().getSearcher("system", "user");
	for (Iterator iterator = inTeamInfo.teams.iterator(); iterator.hasNext();) 
	{
		Team team = (Team) iterator.next();
		//look up the group by name
		UserManager um = userManager;
		Group group = um.getGroup(team.id);
		if(group == null)
		{
			group = um.createGroup(team.id, team.publicName);
			groupSearcher.saveData(group, null);
		}
		inUser.addGroup(group);
		
		//create users for the team members if necessary
		for (Iterator iterator2 = team.studentList.iterator(); iterator2.hasNext();)
		{
			Student student = (Student) iterator2.next();
			//try to load the user
			User member = um.getUser(student.personId);
			if(member == null)
			{
				member = um.createUser(student.personId, null);
				member.setEmail(student.email);
				member.setFirstName(student.firstName);
				member.setLastName(student.lastName);
				member.addGroup(group);
				userSearcher.saveData(member, null);
			}
			else if( !member.isInGroup(group))
			{
				member.addGroup(group);
				userSearcher.saveData(member, null);
			}
		}
	}
}

protected String getContent(String inUrl)
{
	Downloader dl = new Downloader();
	return dl.downloadToString(inUrl);
}

protected void oracleSsoLogin()
{
	
	String pid = context.getRequestParameter("HBS_PERSON_ID");
	if( pid == null)
	{
		HttpServletRequest requestHeader = context.getRequest();
		pid = requestHeader.getHeader("HBS_PERSON_ID");
	}
		//pid = "641292" cburkey;
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
	saveUserData(pid);
	//um.saveUser(user);
	
	//auto login user
	context.putSessionValue("user", user);
	context.redirect("/hbs/index.html");
}

protected void saveUserData(String id){
//	GroovyClassLoader loader = engine.getGroovyClassLoader();
//	Class groovyClass = loader.loadClass("com.entermedia.soap.SoapUserManager");
	SoapUserManager mgr =   new SoapUserManager();// groovyClass.newInstance();	
	mgr.setUserManager(userManager);
	mgr.setSearcherManager( getSearcherManager() );
	mgr.setXmlUtil(moduleManager.getBean("xmlUtil"));
	mgr.setContext( context );
	mgr.updateUserByPersonId(id);
}

oracleSsoLogin();


