package Task;


import com.microstrategy.web.app.tasks.GetSessionStateTask;
import com.microstrategy.web.beans.MarkupOutput;
import com.microstrategy.web.beans.RequestKeys;
import com.microstrategy.web.objects.SimpleList;
import com.microstrategy.web.objects.WebFolder;
import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.WebObjectSource;
import com.microstrategy.web.objects.WebObjectsException;
import com.microstrategy.web.objects.WebObjectsFactory;
import com.microstrategy.web.objects.WebSearch;
import com.microstrategy.web.objects.admin.users.WebStandardLoginInfo;
import com.microstrategy.web.objects.admin.users.WebUser;
import com.microstrategy.web.objects.admin.users.WebUserGroup;
import com.microstrategy.web.objects.admin.users.WebUserList;
import com.microstrategy.web.tasks.TaskException;
import com.microstrategy.web.tasks.TaskRequestContext;

public class DemoGetSessionStateTask extends GetSessionStateTask
{
  private static WebIServerSession adminsession;

  public void processRequest(TaskRequestContext context, MarkupOutput markupOutput)
    throws TaskException
  {
    RequestKeys keys = context.getRequestKeys();
    String oldSession = keys.getValue("oldSessionInfo");
    String uid = keys.getValue("uid");
    String pwd = keys.getValue("pwd");
    String project = keys.getValue("project");
    String server = keys.getValue("server");

    
    System.out.println("user : " + uid + "pass : "+ pwd);
    
    adminsession = WebObjectsFactory.getInstance().getIServerSession().createCopy();
    adminsession.setServerName("localhost");
    adminsession.setServerPort(0);
    adminsession.setProjectName("MicroStrategy Tutorial");
    adminsession.setLogin("administrator");
    adminsession.setPassword("");
    adminsession.setApplicationType(8);
    try {
      System.out.println(adminsession.getSessionID());
    }
    catch (WebObjectsException e1) {
      e1.printStackTrace();
    }
    WebSearch search = adminsession.getFactory().getObjectSource().getNewSearchObject();
    search.setNamePattern(uid);
    search.setAsync(false);
    search.types().add(Integer.valueOf(8704));
    search.setDomain(5);

    boolean nonExistantUsr = false;
    try {
      search.submit();

      WebFolder folder = search.getResults();
      WebUser user = null;

      if (folder.size() > 0) {
        user = (WebUser)folder.get(0);
      }
      else {
        user = createUser(uid, project);
        user.populate();
        user.getStandardLoginInfo().setPassword(pwd);
        user.setEnabled(true);
        adminsession.getFactory().getObjectSource().save(user);
        nonExistantUsr = true;
      }

      if (nonExistantUsr)
      {
        WebUserGroup group = (WebUserGroup)adminsession.getFactory().getObjectSource().getObject("E589334343775FE787AC3FA88C107052", 34);

        group.getMembers().add(user);

        adminsession.getFactory().getObjectSource().save(group);
      }
    }
    catch (WebObjectsException e)
    {
      e.printStackTrace();
    }
    try
    {
      adminsession.closeSession();
    }
    catch (WebObjectsException e) {
      e.printStackTrace();
    }

    super.processRequest(context, markupOutput);
  }

  private WebUser createUser(String uid, String project) {
    WebUser newusr = (WebUser)adminsession.getFactory().getObjectSource().getNewObject(34, 8704);
    newusr.setLoginName(uid);
    newusr.setFullName(uid);
    newusr.setLoginName(uid);

    return newusr;
  }
}