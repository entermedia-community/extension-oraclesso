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
	HttpServletRequest requestHeader = context.getHeader();
	String pid = requestHeader.getHeader("HBS_PERSON_ID");
	//String tid = requestHeader.getHeader("teamid");  //could be multiple teamids, guess need to split these
	String personinfo = "http://rana1-stage.hbs.edu:8097/teamMgmt/external/ws.htm?action=getTeamInfo&personId=";
	//String teaminfo = "http://rana1-stage.hbs.edu:8097/teamMgmt/external/ws.htm?action=getTeamInfo&teamId=";
	
	String jsonteaminfo = getContent(personinfo + pid);
	//String jsonteam = getContent(teaminfo + tid);
	context.putPageValue("jsonresponse", jsonteaminfo);
}

oracleSsoLogin();