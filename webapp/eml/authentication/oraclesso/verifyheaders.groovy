import javax.servlet.http.HttpServletRequest

protected void verifyHeaders()
{
	String pid = context.getRequestParameter("HBS_PERSON_ID");
	if( pid == null)
	{
		HttpServletRequest requestHeader = context.getRequest();
		pid = requestHeader.getHeader("HBS_PERSON_ID");
	}
	//check if matches logged in user
	String loggedinid = context.getUser().getId();
	if(pid != loggedinid)
	{
		sso autologin = new sso();
		autologin.oracleSsoLogin();
	}
}

verifyHeaders();