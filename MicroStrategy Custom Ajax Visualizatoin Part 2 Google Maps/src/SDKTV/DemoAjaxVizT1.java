package SDKTV;

import com.microstrategy.utils.xml.XMLUtils;
import com.microstrategy.web.app.beans.AppContext;
import com.microstrategy.web.app.transforms.AbstractReportXMLTransform;
import com.microstrategy.web.beans.MarkupOutput;
import com.microstrategy.web.beans.WebBeanException;
import com.microstrategy.web.objects.WebGridData;
import com.microstrategy.web.objects.WebGridRows;
import com.microstrategy.web.objects.WebHeaders;
import com.microstrategy.web.objects.WebObjectsException;
import com.microstrategy.web.objects.WebRow;
import com.microstrategy.web.objects.WebRowValue;
import com.microstrategy.web.tags.Tag;
import com.microstrategy.web.tags.TagsFactory;

public class DemoAjaxVizT1 extends AbstractReportXMLTransform {

	
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}


	
public void renderXml(MarkupOutput out){
		if(_bean.getBeanContext() instanceof AppContext)
            ((AppContext)_bean.getBeanContext()).getContainerServices().setContentType("text/xml");
		((AppContext)_bean.getBeanContext()).getContainerServices().setCodePage(65001);

		    
		TagsFactory tf = TagsFactory.getInstance();
		// Create the <rows> tag
		Tag RowsTag = tf.newTag("parent");
		RowsTag.setIsContentInline(false);		
		
        try
        {             
        	//how many rows in the report? this will tell us how many <row> nodes to have  
        	
            int rowcount = getWebGridData().getGridTotalRows();            
          
            
            WebGridRows gridRows = getWebReportGrid().getGridRows(); 
            	
            for(int y=0;y<gridRows.size();y++){
            	//Create a new row <row>
            	Tag RowTag = tf.newTag("row");
            	//RowTag.setAttribute("id", XMLUtils.encodeXMLAttribute(Integer.toString(y+1)));
                RowsTag.addChild(RowTag);        
                
//              Get the current row...
                WebHeaders webHeaders = getWebReportGrid().getRowHeaders().get(y);	
            	WebRow webrow = gridRows.get(y);
            	
//            	now we need to append the cell child tags for each cell in the row
            	
            	//get the rows headers for each row
            
            	for (int j=0; j<webHeaders.size(); j++) {            		
            		Tag CellTag = RowTag.addChild("rowCell");
            		CellTag.addTextChild(XMLUtils.encodeXMLAttribute(webHeaders.get(j).getDisplayName()));
            		                   
                }
            	
            	//get row values for each row
            	for (int i=0;i<webrow.size();i++){
            		Tag CellTag = RowTag.addChild("rowCell");
            		//also addTextChild seemed to work...
            		CellTag.addTextChild(XMLUtils.encodeXMLAttribute((webrow.get(i).getValue())));
            		
            	}             		
            }            
        }
        catch(Exception e)
        {
        	e.printStackTrace();            
        }  
        
        
                
        RowsTag.render(out);
        
        //System.out.println(out.getCopyAsString());
	}
 
@Override
protected WebGridData getGridData() throws WebBeanException,
		WebObjectsException {
	// TODO Auto-generated method stub
	
	WebGridData wgd = getWebGridData();
	System.out.println(wgd.getGridTotalRows());
	WebGridRows newGridRows = wgd.getWebReportGrid().getGridRows();
	WebRow newWebRow = null;


	return super.getGridData();
}

}
