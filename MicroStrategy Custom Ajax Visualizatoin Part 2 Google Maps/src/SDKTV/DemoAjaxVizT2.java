package SDKTV;

import com.microstrategy.utils.log.Level;
import com.microstrategy.web.app.transforms.AbstractReportDataVisualizationTransform;
import com.microstrategy.web.beans.MarkupOutput;
import com.microstrategy.web.beans.Messages;
import com.microstrategy.web.beans.ReportBean;
import com.microstrategy.web.beans.WebBeanException;
import com.microstrategy.web.objects.WebGridHeaders;
import com.microstrategy.web.objects.WebGridTitles;
import com.microstrategy.web.objects.WebHeader;
import com.microstrategy.web.objects.WebHeaders;
import com.microstrategy.web.objects.WebObjectsException;
import com.microstrategy.web.objects.WebReportGrid;
import com.microstrategy.web.objects.WebVisualizationSettings;
import com.sun.xml.internal.ws.util.JAXWSUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;



public class DemoAjaxVizT2 extends AbstractReportDataVisualizationTransform { 

	/**
	 * Method used to obtain data from an Element object
	 * @param e
	 * @return
	 */
	public static String getCharacterDataFromElement(Element e)
	{
		Node child = e.getFirstChild();
		if ((child instanceof CharacterData)) {
			CharacterData cd = (CharacterData)child;
			return cd.getData();
		}
		return "nodata";
	}

	

	public String BuildObjArray(String xml){
		System.out.println(xml);
	
		//Here we will build a javascript array to be read later
	
		String __result = "var locations = [";
	
		NodeList n = null;
		try {		
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));
			Document doc = db.parse(is);
			NodeList nodes = doc.getElementsByTagName("parent");
			n = nodes.item(0).getChildNodes();
		}
		catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//generate style for pin and star
		//PinStrut(baloonDoc);
		//StarStrut(starDoc);
	
		/*
		 * Loop through every row of the XML
		 */
		for (int i = 0; i < n.getLength(); i++)
		{
			Element row = (Element)n.item(i);
			NodeList rowCell = row.getChildNodes();
			Element childLine = null;
			String Name = "nodata";
			String Lat = "nodata"; 
			String Long = "nodata"; 
			for (int c = 0; c < rowCell.getLength(); c++)
			{
				childLine = (Element)rowCell.item(c);
				if (c == 0) {
					String tmp = getCharacterDataFromElement(childLine);
					Name = tmp;
					if(Name.equalsIgnoreCase("")){
						//continue;
					}
				}
				if (c == 1) {
					String tmp = getCharacterDataFromElement(childLine);
					Lat = tmp;
				}
				if (c == 2) {
					String tmp = getCharacterDataFromElement(childLine);
					Long = tmp;
				}
			}
			__result = __result + " { id:" + i + ",  position: new google.maps.LatLng("+ Lat + ", " + Long + "), Name: '"+ Name + "'},";
	
		}
		return __result + "];";
	}

	@Override
	public void renderVisualization(MarkupOutput out) {
		// TODO Auto-generated method stub
	
		String xml = getCustomReportXML();
		String tmp = BuildObjArray(xml.replaceAll("(\r\n|\n)", ""));
		//System.out.println(xml);
		System.out.println(tmp);
		out.append(tmp);
	}

}
