package com.entermedia.soap;

import com.openedit.users.User;

public class SoapUserManager {
	private static final String POSTFIX = "</sch:personId></sch:FindUserByPersonIdRequest></soapenv:Body></soapenv:Envelope>";
	private static final String PREFIX = "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' xmlns:sch='http://www.hbs.edu/appnAccess/schemas'>  <soapenv:Header/><soapenv:Body><sch:FindUserByPersonIdRequest><sch:personId>";

	protected XmlUtil fieldXmlUtil;
	protected UserManager fieldUserManager;
	protected SearcherManager fieldSearcherManager;
	 
	
	public User updateUserByPersonId(String personId) throws IOException {
		URL url = new URL("https://teak.hbs.edu/appnAccessWS/services");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type",
				"application/soap+xml; charset=utf-8");

		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setDoOutput(true);

		String soapRequest = PREFIX + personId + POSTFIX;

		connection.setRequestProperty("Content-Length",
				Integer.toString(soapRequest.getBytes("UTF-8").length));
		// Send request
		OutputStreamWriter owr = new OutputStreamWriter(
				connection.getOutputStream(), "UTF-8");

		owr.write(soapRequest);
		owr.close();

		// Get Response
		InputStream is = connection.getInputStream();
		Element root = getXmlUtil().getXml(is, "UTF-8");
		if (root==null){
			throw new OpenEditException("Unable to get root element for " + url);
		}
		User user = getUserManager().getUser(personId);
		Element userElement = root.element("Body")
				.element("FindUserByPersonIdResponse").element("user");
		addUserData(userElement, user);
		addRoles(userElement, user);
		getUserManager().saveUser(user);
		return user;
	}

	protected void addRoles(Element userElement, User inUser) 
	{
		inUser.setFirstName(userElement.elementText("firstName"));
		inUser.setLastName(userElement.elementText("lastName"));
		inUser.setEmail(userElement.elementText("emailAddress"));
	}

	protected void addUserData(Element userElement, User inUser) 
	{
		List roles = userElement.element("personRoles").elements("personRole");
		for (Object element : roles) 
		{
			Element role = (Element) element;
			String hbssettingsgroup = role.elementText("roleCode");
			
			MediaArchive archive = getMediaArchive();
			
			Searcher userprofilesearcher = getSearcherManager().getSearcher(archive.getCatalogId(), inDetail)
			Data profile = userprofilesearcher.searchByField("userid", inUser.getId());
			if( profile == null)
			{
				profile = userprofilesearcher.createNewData();
				profile.setProperty("userid",inUser.getId());
			}
			if( profile.get("settingsgroup") != hbssettingsgroup)
			{
				profile.setProperty("settingsgroup",hbssettingsgroup);
				userprofilesearcher.saveData(profile,null);
			}
		}
	}
	protected MediaArchive getMediaArchive()
	{
		MediaArchive archive = (MediaArchive)context.getPageValue("mediaArchive");
		return archive;
	}
	public XmlUtil getXmlUtil() {
		return fieldXmlUtil;
	}

	public void setXmlUtil(XmlUtil inXmlUtil) {
		fieldXmlUtil = inXmlUtil;
	}

	public UserManager getUserManager() {
		return fieldUserManager;
	}

	public void setUserManager(UserManager inUserManager) {
		fieldUserManager = inUserManager;
	}

	public SearcherManager getSearcherManager()
	{
		return fieldSearcherManager;
	}
	public void setSearcherManager(SearcherManager inSearcherManager)
	{
		fieldSearcherManager  = inSearcherManager;
	}
}
