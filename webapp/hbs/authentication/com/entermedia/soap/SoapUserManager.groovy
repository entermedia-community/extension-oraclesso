package com.entermedia.soap;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.dom4j.Element;

import com.openedit.OpenEditException;
import com.openedit.users.Group;
import com.openedit.users.User;
import com.openedit.users.UserManager;
import com.openedit.util.XmlUtil;

public class SoapUserManager {
	private static final String POSTFIX = "</sch:personId></sch:FindUserByPersonIdRequest></soapenv:Body></soapenv:Envelope>";
	private static final String PREFIX = "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' xmlns:sch='http://www.hbs.edu/appnAccess/schemas'>  <soapenv:Header/><soapenv:Body><sch:FindUserByPersonIdRequest><sch:personId>";

	protected XmlUtil fieldXmlUtil;
	protected UserManager fieldUserManager;

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
		User user = getUserManager().getUser(personId);
		Element userElement = root.element("Body")
				.element("FindUserByPersonIdResponse").element("user");
		addUserData(userElement, user);
		addRoles(userElement, user);
		getUserManager().saveUser(user);
		return user;
	}

	protected void addRoles(Element userElement, User inUser) {
		inUser.setFirstName(userElement.elementText("firstName"));
		inUser.setLastName(userElement.elementText("lastName"));
		inUser.setEmail(userElement.elementText("emailAddress"));
	}

	protected void addUserData(Element userElement, User inUser) {
		List roles = userElement.element("personRoles").elements("personRole");
		for (Object element : roles) {
			Element role = (Element) element;
			String groupid = role.elementText("roleCode");
			Group group = getUserManager().getGroup(groupid);
			if (group == null) {
				throw new OpenEditException("Group Not Found. " + groupid);
			}
			inUser.addGroup(group);
		}
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

}
