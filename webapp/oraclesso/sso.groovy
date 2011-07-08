//check for a value in the header
//use that value to request information from from a url should return json
//parse the returned json

import javax.servlet.http.HttpServletRequest

import com.openedit.modules.update.Downloader

protected String getContent(String inUrl)
{
	Downloader dl = new Downloader();
	return dl.downloadToString(inUrl);
}

protected void oracleSsoLogin()
{
	HttpServletRequest requestHeader = context.getRequest();
	String pid = requestHeader.getHeader("HBS_PERSON_ID");
	//String tid = requestHeader.getHeader("teamid");  //could be multiple teamids, guess need to split these
	//String teaminfo = http://pine-stage.hbs.edu/teamMgmt/internal/ws.htm?action=getTeamInfo&teamId=498211;
	String personinfo = "http://pine-stage.hbs.edu/teamMgmt/internal/ws.htm?action=getTeamInfo&personId=615538";
	
	String jsonteaminfo;
	if(pid)
	{
		jsonteaminfo = getContent(personinfo + pid);
	}
	else
	{
		jsonteaminfo = getContent(personinfo);
	}
	//String jsonteam = getContent(teaminfo + tid);
	context.putPageValue("jsonresponse", jsonteaminfo);
}

oracleSsoLogin();