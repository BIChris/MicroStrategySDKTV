package com.microstrategy.web.portlets.liferay;

import com.microstrategy.web.portlets.CredentialMapper;
import com.microstrategy.web.portlets.LoginInfo;
import javax.portlet.PortletRequest;

public class CustomCredentialMapper
  implements CredentialMapper
{
  public LoginInfo mapLogin(Object portletRequest, LoginInfo loginInfo)
  {
    PortletRequest request = (PortletRequest)portletRequest;

    String portalUserID = request.getRemoteUser();

    loginInfo.setLogin("d1");
    loginInfo.setPassword("d1");

    loginInfo.setAuthMode(1);

    return loginInfo;
  }
}