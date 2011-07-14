package com.entermedia.soap;

import org.junit.Test;
import org.openedit.entermedia.BaseEnterMediaTest;

import com.openedit.WebPageRequest;

public class RolesTest extends BaseEnterMediaTest {

	@Test
	public void testAddRoles() 
	{
		WebPageRequest req = getFixture().createPageRequest("/eml/authentication/nopermissions.html");
		req.setRequestParameter("HBS_PERSON_ID","641292");
		getFixture().getEngine().executePathActions(req);
		
	}

//	@Test
//	public void XtestAddUserData() {
//		fail("Not yet implemented");
//	}

}
