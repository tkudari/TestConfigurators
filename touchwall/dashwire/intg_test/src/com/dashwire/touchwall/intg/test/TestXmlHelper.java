package com.dashwire.touchwall.intg.test;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;

import org.xmlpull.v1.XmlSerializer;

import android.os.Environment;
import android.util.Log;
import android.util.Xml;


public class TestXmlHelper {
	
	XmlSerializer xmlSerializer;
	StringWriter xmlReportContent;
	final String TAG = "QA test";
	
	
	public TestXmlHelper() {
		super();
		Log.d(TAG,"TestXmlHelper");

	//	xmlSerializer = Xml.newSerializer();
	//	xmlReportContent = new StringWriter();		
		
		try {
		//	xmlSerializer.setOutput(xmlReportContent);
			// start DOCUMENT
		//	xmlSerializer.startDocument("UTF-8", true);
			
		} catch (IllegalArgumentException e) {
			Log.e(TAG,"Error creating XML file IllegalArgumentException " + e.toString());
		} catch (IllegalStateException e) {
			Log.e(TAG,"Error creating XML fileIllegalStateException" + e.toString());
		}

	}

	public void LogTestResultXML(String testCaseTitle, String expectedValue, String resultValue, boolean testResult){
		String xmlReport ;
		
		try {
			
//			xmlSerializer.setOutput(xmlReportContent);
			// start DOCUMENT
//			xmlSerializer.startDocument("UTF-8", true);			
			//XML content
			xmlSerializer.startTag("", "testSuite");
			xmlSerializer.startTag("", "testCase");
			xmlSerializer.attribute("", "title", testCaseTitle);						
			addTestCaseData(expectedValue, resultValue, testCaseTitle, testResult);			
			xmlSerializer.endTag("", "testCase");	
			xmlSerializer.endTag("", "testSuite");			
		    // end DOCUMENT
		    xmlSerializer.endDocument();
		} catch (IllegalArgumentException e) {
			Log.e(TAG,"Error creating XML file IllegalArgumentException " + e.toString());
		} catch (IllegalStateException e) {
			Log.e(TAG,"Error creating XML fileIllegalStateException" + e.toString());
		} catch (IOException e) {
			Log.e(TAG,"Error creating XML file " + e.toString());
		}	
			
		xmlReport = xmlReportContent.toString();
		Log.e(TAG,"CONTENT " + xmlReport);
		DWall_intg_test_helper.createXMlfile(testCaseTitle + ".xml", xmlReport);
		
	}
	
	
	
	public void addTestCaseData(String expectedValue, String resultValue, String imageFile, boolean testResult) throws IllegalArgumentException, IllegalStateException, IOException{
		Log.e(TAG, "addTestCaseData " + xmlSerializer.getName());
		
		try{
		// open tag
		xmlSerializer.startTag("", "param");
		xmlSerializer.attribute("", "name", "expectedValue");
		xmlSerializer.text(expectedValue);
		// close tag
		xmlSerializer.endTag("", "param");
		// open tag
		xmlSerializer.startTag("", "param");
		xmlSerializer.attribute("", "name", "resultValue");
		xmlSerializer.text(resultValue);
		// close tag
		xmlSerializer.endTag("", "param");	
		// open tag
		xmlSerializer.startTag("", "param");
		xmlSerializer.attribute("", "name", "testResult");
		xmlSerializer.text( new Boolean(testResult).toString());		
		// close tag
		xmlSerializer.endTag("", "param");			
		// open tag
		xmlSerializer.startTag("", "param");
		xmlSerializer.attribute("", "name", "imgFile");
		xmlSerializer.text("QAResultImages" + File.separator + imageFile + ".jpg");		
		// close tag
		xmlSerializer.endTag("", "param");
		}catch(Exception e){
			Log.e(TAG, "Exeception " + e.toString());
		}
	}
	
	public void startXMLfile(){

		Log.d(TAG,"startXMLfile");	
		
		try {
			
			xmlSerializer = Xml.newSerializer();
			xmlReportContent = new StringWriter();			
			
			xmlSerializer.setOutput(xmlReportContent);
			// start DOCUMENT
			xmlSerializer.startDocument("UTF-8", true);
			
		} catch (IllegalArgumentException e) {
			Log.e(TAG,"Error creating XML file IllegalArgumentException " + e.toString());
		} catch (IllegalStateException e) {
			Log.e(TAG,"Error creating XML fileIllegalStateException" + e.toString());
		} catch (IOException e) {
			Log.e(TAG,"Error creating XML file " + e.toString());
		}
		
		Log.d(TAG,"xmlSerializer = " + xmlSerializer.getClass().toString());
		Log.d(TAG,"xmlReportContent = " + xmlReportContent.getClass().toString());
	}
	
	
	public void closeXMLfile(){	

//		try {
//		    // end DOCUMENT
//		    xmlSerializer.endDocument();		
//		} catch (IllegalArgumentException e) {
//			Log.e(TAG,"Error creating XML file IllegalArgumentException " + e.toString());
//		} catch (IllegalStateException e) {
//			Log.e(TAG,"Error creating XML fileIllegalStateException" + e.toString());
//		} catch (IOException e) {
//			Log.e(TAG,"Error creating XML file " + e.toString());
//		}			
//		
	}

}
