package SDKTV;

import com.microstrategy.web.app.transforms.AbstractReportDataVisualizationTransform;
import com.microstrategy.web.beans.MarkupOutput;

public class DemoAjaxVizT2 extends
AbstractReportDataVisualizationTransform { {

}

@Override
public void renderVisualization(MarkupOutput out) {
	// TODO Auto-generated method stub
	
	String xml = getCustomReportXML();
	System.out.println(xml);
	out.append("My First Custom AJax Viz");
	out.append(xml);
}
	
}
