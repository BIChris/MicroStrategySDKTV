package com.datameaning.transforms;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

import com.google.gson.Gson;
import com.microstrategy.utils.StringUtils;
import com.microstrategy.utils.serialization.EnumWebPersistableState;
import com.microstrategy.utils.xml.XMLBuilder;
import com.microstrategy.web.app.ProjectInformation;
import com.microstrategy.web.app.beans.AppContext;
import com.microstrategy.web.app.beans.ProjectsBean;
import com.microstrategy.web.app.transforms.ProjectsTransform;
import com.microstrategy.web.app.utils.HTTPHelper;
import com.microstrategy.web.beans.EnumEventElement;
import com.microstrategy.web.beans.MarkupOutput;
import com.microstrategy.web.beans.Transformable;
import com.microstrategy.web.beans.WebEvent;
import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.WebObjectInfo;
import com.microstrategy.web.objects.WebObjectsException;
import com.microstrategy.web.objects.WebObjectsFactory;
import com.microstrategy.web.objects.WebProjectInstance;
import com.microstrategy.web.platform.ParameterBuilder;
import com.microstrategy.web.tags.AnchorTag;
import com.microstrategy.web.tags.Tag;
import com.microstrategy.webapi.EnumDSSXMLObjectFlags;

public class CustomProjectsTransform extends ProjectsTransform {
	private static final String PROPERTIES_FILE_NAME = "projectGroups";
	private ProjectsBean _projectsBean;
	private Random r;
	public void initializeTransform(Transformable paramTransformable)
	{
		super.initializeTransform(paramTransformable);
		this._projectsBean = ((ProjectsBean)paramTransformable);
		r = new Random();

	}

	public void renderProjectGroups(MarkupOutput paramMarkupOutput){

		ResourceBundle groups = ResourceBundle.getBundle(PROPERTIES_FILE_NAME);
	
		Tag parentDiv = getTagsFactory().newDivTag();
		parentDiv.setId("groupParent");


		Enumeration <String> keys = groups.getKeys();
		while (keys.hasMoreElements()) {

			String key = keys.nextElement();
			String value = groups.getString(key);
			String grpNm = value.split("#")[0];
			String grpDesc = value.split("#")[1];

			Tag childDiv = getTagsFactory().newDivTag();
			childDiv.setCssClass("customGroup mstrProjectItem");
			childDiv.setAttribute("parentGrpId", key);

			Tag label = getTagsFactory().newTag("label");
			label.addTextChild("GroupName: ");
			childDiv.addChild(label);

			Tag span = getTagsFactory().newSpanTag();
			span.addTextChild(grpNm);

			childDiv.addChild(span);

			parentDiv.addChild(childDiv);

			Tag desDiv = getTagsFactory().newDivTag();
			Tag desLabel =  getTagsFactory().newTag("label");

			label.addTextChild("  GroupDesc: ");
			desDiv.addChild(desLabel);

			Tag descSpan = getTagsFactory().newSpanTag();
			span.addTextChild(grpDesc);

			desDiv.addChild(descSpan);

			childDiv.addChild(desDiv);


		}
		parentDiv.render(paramMarkupOutput);		
	}

	public void renderProjectJson(MarkupOutput paramMarkupOutput) throws WebObjectsException {
		ArrayList projectList = this._projectsBean.getProjectList();
		String _result = "";
		
		/* Rendering back img */
		Tag backImg = getTagsFactory().newImageTag();
		backImg.setId("projctGroupBack");
		backImg.setCssClass("hidden");
		backImg.setAttribute("src", "../plugins/ProjectsGrouping/style/images/icon.png");
		backImg.render(paramMarkupOutput);

		for(int i = 0; i<projectList.size(); i++){

			ProjectInformation tmp = (ProjectInformation) projectList.get(i);
			ProjectObj tmpObj = buildProjectJson(tmp);

			
			Tag parentDiv = getTagsFactory().newDivTag();
			parentDiv.setCssClass("custProj mstrProjectItem hidden");
			parentDiv.setAttribute("grpId", tmpObj.customGroupid);
			
			WebEvent localWebEvent = getOpenProjectEvent(tmp);
		    AnchorTag basicA = generateAnchor(localWebEvent);
		    basicA.setCssClass("mstrLargeIconViewItemLink");
			
			
			Tag projImg = getTagsFactory().newDivTag();
			projImg.setCssClass("projIcon");
			projImg.addChild(basicA);
			
			projImg.addChild(basicA);
			
			Tag imgSpan = getTagsFactory().newSpanTag();
			imgSpan.setCssClass("mstrIcon");
			imgSpan.setId("mstrIconProject");
			imgSpan.setAttribute("name", "mstrIconProject");
			projImg.addChild(imgSpan);
			
		
			Tag cntDiv = getTagsFactory().newDivTag();
			cntDiv.setCssClass("container");

			Tag projIcon = getTagsFactory().newDivTag();
			projIcon.setCssClass("mstrLargeIconViewItemName");
			projIcon.setAttribute("name", "projName");
			
			AnchorTag anchor = generateAnchor(localWebEvent);
		    anchor.addTextChild(tmpObj.name);
		
			projIcon.addChild(anchor);
			cntDiv.addChild(projIcon);
			
			
			Tag projDesc = getTagsFactory().newDivTag();
			projDesc.setCssClass("mstrProjectDescription");
			projDesc.addTextChild(tmpObj.projectDesc);
			

			Tag serverNm = getTagsFactory().newDivTag();
			projDesc.setCssClass("mstrserver");
			projDesc.addTextChild("Server name: ");
			projDesc.addChild(serverNm);
			
			Tag serverSpan = getTagsFactory().newSpanTag();
			serverSpan.addTextChild(tmpObj.serverName);
			projDesc.addChild(serverSpan);
			
			cntDiv.addChild(projDesc);

			parentDiv.addChild(projImg);
			parentDiv.addChild(cntDiv);
			
			parentDiv.render(paramMarkupOutput);

		}
	}

	/*
	 * Build json object for each project
	 */
	private ProjectObj buildProjectJson(ProjectInformation pi) throws WebObjectsException{
		String id = String.valueOf(pi.getProjectDetails().getID());
		String nm = pi.getProjectName();
		String srvnm = pi.getServerAlias();
		int projStatus = pi.getProjectStatus();
		String projectDesc = "";

		//check if project desc is available
		if(isProjectDescriptionAvailable(pi)){
			projectDesc = getProjectDescription(pi);
		}

		//int customGroupID = ;
		String customGroupID = String.valueOf(r.nextInt(3 - 0 + 1) + 0);
		
		//now get the custom group indicator from project desc!
		if(projectDesc.contains("{custom")){
			//do something here

		}


		//TO-DO finish building json, maybe implement using GSON
		ProjectObj tmpObj = new ProjectObj(id, nm, srvnm, projStatus, customGroupID, projectDesc);


		return tmpObj;

	}

	
	private String getProjectDescription(ProjectInformation paramProjectInformation)
	{
		WebProjectInstance localWebProjectInstance = HTTPHelper.getProject(createSession(paramProjectInformation));
		return localWebProjectInstance != null ? localWebProjectInstance.getProjectDescription() : paramProjectInformation.getProjectDetails().getProjectDescription();
	}

	private WebIServerSession createSession(ProjectInformation paramProjectInformation)
	{
		WebIServerSession localWebIServerSession = WebObjectsFactory.getInstance().getIServerSession();
		localWebIServerSession.setServerName(paramProjectInformation.getServerName());
		localWebIServerSession.setServerPort(paramProjectInformation.getPortNumber());
		localWebIServerSession.setProjectName(paramProjectInformation.getProjectDetails().getProjectName());
		localWebIServerSession.setMetadataLocale(paramProjectInformation.getLocale());
		return localWebIServerSession;
	}

}

