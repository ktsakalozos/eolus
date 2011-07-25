package eolus.server;



import javax.xml.parsers.*;
import org.xml.sax.InputSource;
import org.w3c.dom.*;
import java.io.*;


public class Parser 
{

	public static String[] parseVMInfo(String xml) throws Exception
	{
		DocumentBuilderFactory dbf =
			DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(xml));

		Document doc = db.parse(is);

		String s0=doc.getElementsByTagName("NAME").item(0).getFirstChild().getNodeValue();
		String s1=doc.getElementsByTagName("STATE").item(0).getFirstChild().getNodeValue();
		String s2=doc.getElementsByTagName("MEMORY").item(0).getFirstChild().getNodeValue();
		String s3=doc.getElementsByTagName("CPU").item(0).getFirstChild().getNodeValue();
		String s4=doc.getElementsByTagName("IP").item(0).getFirstChild().getNodeValue();
		String s5=doc.getElementsByTagName("HOSTNAME").item(0).getFirstChild().getNodeValue();

		String[] s = {s0,s1,s2,s3,s4,s5};


		return s;
	}

	public static String[] parseHostInfo(String xml) throws Exception
	{
		DocumentBuilderFactory dbf =
			DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(xml));

		Document doc = db.parse(is);
		String s0=doc.getElementsByTagName("NAME").item(0).getFirstChild().getNodeValue();
		String s1=doc.getElementsByTagName("RUNNING_VMS").item(0).getFirstChild().getNodeValue();
		String s2=doc.getElementsByTagName("TOTALCPU").item(0).getFirstChild().getNodeValue();
		String s3=doc.getElementsByTagName("FREE_CPU").item(0).getFirstChild().getNodeValue();
		String s4=doc.getElementsByTagName("TOTALMEMORY").item(0).getFirstChild().getNodeValue();
		String s5=doc.getElementsByTagName("FREE_MEM").item(0).getFirstChild().getNodeValue();
		String s6=doc.getElementsByTagName("STATE").item(0).getFirstChild().getNodeValue();
		s2=s2+" %";
		s3=s3+" %";
		long mem = Long.parseLong(s4);
		long temp=(mem/1024);
		s4 =  temp+" MB";

		long fmem = Long.parseLong(s5);
		temp=(fmem/1024);
		s5 = temp +" MB";



		String[] s = {s0,s1,s2,s3,s4,s5,s6};


		return s;
	}

	public static String[] parseClusterInfo(String xml) throws Exception
	{
		DocumentBuilderFactory dbf =
			DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(xml));

		Document doc = db.parse(is);
		String s0=doc.getElementsByTagName("MIGRATE").item(0).getFirstChild().getNodeValue();
		String s1=doc.getElementsByTagName("LIVEMIGRATE").item(0).getFirstChild().getNodeValue();


		String[] s = {s0,s1};


		return s;
	}




}