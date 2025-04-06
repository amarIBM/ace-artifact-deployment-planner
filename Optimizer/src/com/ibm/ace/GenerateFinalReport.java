package com.ibm.ace;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class GenerateFinalReport {
	public static void main(String[] args)
	{
		String type=args[0];
		String input_file=args[1];
		int count=0;
		ArrayList<String> bar_or_server = new ArrayList<String>();
		ArrayList<String> apps = new ArrayList<String>();
		ArrayList<String> CLR = new ArrayList<String>();
		ArrayList<String> Deploy = new ArrayList<String>();
		ArrayList<String> JVM = new ArrayList<String>();
		ArrayList<String> SSL = new ArrayList<String>();
   	    ArrayList<String> NodeJS = new ArrayList<String>();
	    ArrayList<String> Nodes_Dotnet = new ArrayList<String>();
	    ArrayList<String> Nodes_Aggregation = new ArrayList<String>();
	    ArrayList<String> Nodes_Basic = new ArrayList<String>();
	    ArrayList<String> Nodes_CallableFlow = new ArrayList<String>();
	    ArrayList<String> Nodes_Database_ODBC = new ArrayList<String>();
	    ArrayList<String> Nodes_Connector = new ArrayList<String>();
	    ArrayList<String> Nodes_ESQL = new ArrayList<String>();
	    ArrayList<String> Nodes_Group = new ArrayList<String>();
	    ArrayList<String> Nodes_JMSTransform = new ArrayList<String>();
	    ArrayList<String> Nodes_MQ = new ArrayList<String>();
	    ArrayList<String> Nodes_REST = new ArrayList<String>();
	    ArrayList<String> Nodes_Http = new ArrayList<String>();
	    ArrayList<String> Nodes_SecurityPEP = new ArrayList<String>();
	    ArrayList<String> Nodes_Timer = new ArrayList<String>();		   
	    ArrayList<String> JavaNodes_Adapters = new ArrayList<String>();
	    ArrayList<String> JavaNodes_CICS = new ArrayList<String>();
	    ArrayList<String> JavaNodes_CORBA = new ArrayList<String>();
	    ArrayList<String> JavaNodes_ChangeDataCapture = new ArrayList<String>();
	    ArrayList<String> JavaNodes_Collection = new ArrayList<String>();
	    ArrayList<String> JavaNodes_Email = new ArrayList<String>();
	    ArrayList<String> JavaNodes_File = new ArrayList<String>();
	    ArrayList<String> JavaNodes_IMS = new ArrayList<String>();
	    ArrayList<String> JavaNodes_JDBC = new ArrayList<String>();
	    ArrayList<String> JavaNodes_JMS = new ArrayList<String>();
	    ArrayList<String> JavaNodes_JavaCompute = new ArrayList<String>();
	    ArrayList<String> JavaNodes_JavaDesignerConnectors = new ArrayList<String>();
	    ArrayList<String> JavaNodes_Kafka = new ArrayList<String>();
	    ArrayList<String> JavaNodes_MQTT = new ArrayList<String>();
	    ArrayList<String> JavaNodes_Mapping = new ArrayList<String>();
	    ArrayList<String> JavaNodes_ODM = new ArrayList<String>();
	    ArrayList<String> JavaNodes_SOAP = new ArrayList<String>();
	    ArrayList<String> JavaNodes_TCPIP = new ArrayList<String>();
	    ArrayList<String> JavaNodes_WSSecurity = new ArrayList<String>();
	    ArrayList<String> JavaNodes_WSRR = new ArrayList<String>();
	    ArrayList<String> JavaNodes_XSLT = new ArrayList<String>();
	    ArrayList<String> JavaScriptNodes_LoopBack = new ArrayList<String>();
	    ArrayList<String> JavaScriptNodes_Salesforce = new ArrayList<String>();			   
	    ArrayList<String> ResourceManagers_FTE = new ArrayList<String>();
	    ArrayList<String> ResourceManagers_GlobalCache = new ArrayList<String>();
	    ArrayList<String> ResourceManagers_OpenTelemetry = new ArrayList<String>();
	    ArrayList<String> FlowSecurityProviders_LDAP = new ArrayList<String>();
	    ArrayList<String> FlowSecurityProviders_TFIM = new ArrayList<String>();
	    ArrayList<String> FlowSecurityProviders_WS_Trust = new ArrayList<String>();
		   
		File source_dir = new File(System.getProperty("user.dir"));
		//source_dir = new File("/Users/charithakreddy/Desktop/Project1/Output/ACE1209_250213_183308");
		File[] children = source_dir.listFiles();
        for (File intermediate_report : children) {
            if (!intermediate_report.isHidden()) {	 
            	//System.out.println(intermediate_report.getName());
            	//String server_name=intermediate_report.getName();
            	if(intermediate_report.isFile() && intermediate_report.getName().endsWith(".txt"))
            	{
	            	Scanner sc;
					try {
						sc = new Scanner(intermediate_report);
						while (sc.hasNextLine())
				        {
				        	String curr_line=sc.nextLine();
				        	String category = curr_line.split(":", 2)[0].trim();
				        	if(curr_line.indexOf("[")==-1)
				        	{
				        		if(category.compareToIgnoreCase("Count")==0)
				        		{
				        			count=count+Integer.parseInt(curr_line.split(":", 2)[1].trim());
				        		}
				        		else if(category.compareToIgnoreCase("Input")==0)
				        		{
				        			bar_or_server.add(curr_line.split(":", 2)[1].trim());
				        		}
				        		continue;
				        	}
				        	if(curr_line.indexOf("[")+1 == curr_line.indexOf("]"))
				        		continue;
				        	String[] items=curr_line.substring(curr_line.indexOf("[")+1, curr_line.indexOf("]")).split(",");
				        	//System.out.println(category+items.length+"\t"+items[0]);
				        	switch(category) {	
				        	case "List of apps":
				        		Collections.addAll(apps, items);
				        		break;
				        	case "CLR":
				        		Collections.addAll(CLR, items);
				        		break;
				        	case "Deploy":
				        		Collections.addAll(Deploy, items);
				        		break;
				        	case "JVM":
				        		Collections.addAll(JVM, items);
				        		break;
				        	case "SSL":
				        		Collections.addAll(SSL, items);
				        		break;
				        	case "NodeJS":
				        		Collections.addAll(NodeJS, items);
				        		break;
				        	case "Nodes_Dotnet":
				        		Collections.addAll(Nodes_Dotnet, items);
				        		break;
				        	case "Nodes_Aggregation":
				        		Collections.addAll(Nodes_Aggregation, items);
				        		break;
				        	case "Nodes_Basic":
				        		Collections.addAll(Nodes_Basic, items);
				        		break;
				        	case "Nodes_CallableFlow":
				        		Collections.addAll(Nodes_CallableFlow, items);
				        		break;
				        	case "Nodes_Database_ODBC":
				        		Collections.addAll(Nodes_Database_ODBC, items);
				        		break;
				        	case "Nodes_Connector":
				        		Collections.addAll(Nodes_Connector, items);
				        		break;
				        	case "Nodes_ESQL":
				        		Collections.addAll(Nodes_ESQL, items);
				        		break;
				        	case "Nodes_Group":
				        		Collections.addAll(Nodes_Group, items);
				        		break;
				        	case "Nodes_JMSTransform":
				        		Collections.addAll(Nodes_JMSTransform, items);
				        		break;
				        	case "Nodes_MQ":
				        		Collections.addAll(Nodes_MQ, items);
				        		break;
				        	case "Nodes_REST":
				        		Collections.addAll(Nodes_REST, items);
				        		break;
				        	case "Nodes_Http":
				        		Collections.addAll(Nodes_Http, items);
				        		break;
				        	case "Nodes_SecurityPEP":
				        		Collections.addAll(Nodes_SecurityPEP, items);
				        		break;
				        	case "Nodes_Timer":
				        		Collections.addAll(Nodes_Timer, items);
				        		break;
				        	case "JavaNodes_Adapters":
				        		Collections.addAll(JavaNodes_Adapters, items);
				        		break;
				        	case "JavaNodes_CICS":
				        		Collections.addAll(JavaNodes_CICS, items);
				        		break;
				        	case "JavaNodes_CORBA":
				        		Collections.addAll(JavaNodes_CORBA, items);
				        		break;
				        	case "JavaNodes_ChangeDataCapture":
				        		Collections.addAll(JavaNodes_ChangeDataCapture, items);
				        		break;
				        	case "JavaNodes_Collection":
				        		Collections.addAll(JavaNodes_Collection, items);
				        		break;
				        	case "JavaNodes_Email":
				        		Collections.addAll(JavaNodes_Email, items);
				        		break;
				        	case "JavaNodes_File":
				        		Collections.addAll(JavaNodes_File, items);
				        		break;
				        	case "JavaNodes_IMS":
				        		Collections.addAll(JavaNodes_IMS, items);
				        		break;
				        	case "JavaNodes_JDBC":
				        		Collections.addAll(JavaNodes_JDBC, items);
				        		break;
				        	case "JavaNodes_JMS":
				        		Collections.addAll(JavaNodes_JMS, items);
				        		break;
				        	case "JavaNodes_JavaCompute":
				        		Collections.addAll(JavaNodes_JavaCompute, items);
				        		break;
				        	case "JavaNodes_JavaDesignerConnectors":
				        		Collections.addAll(JavaNodes_JavaDesignerConnectors, items);
				        		break;
				        	case "JavaNodes_Kafka":
				        		Collections.addAll(JavaNodes_Kafka, items);
				        		break;
				        	case "JavaNodes_MQTT":
				        		Collections.addAll(JavaNodes_MQTT, items);
				        		break;
				        	case "JavaNodes_Mapping":
				        		Collections.addAll(JavaNodes_Mapping, items);
				        		break;
				        	case "JavaNodes_ODM":
				        		Collections.addAll(JavaNodes_ODM, items);
				        		break;
				        	case "JavaNodes_SOAP":
				        		Collections.addAll(JavaNodes_SOAP, items);
				        		break;
				        	case "JavaNodes_TCPIP":
				        		Collections.addAll(JavaNodes_TCPIP, items);
				        		break;
				        	case "JavaNodes_WS-Security":
				        		Collections.addAll(JavaNodes_WSSecurity, items);
				        		break;
				        	case "JavaNodes_WSRR":
				        		Collections.addAll(JavaNodes_WSRR, items);
				        		break;
				        	case "JavaNodes_XSLT":
				        		Collections.addAll(JavaNodes_XSLT, items);
				        		break;
				        	case "JavaScriptNodes_LoopBack":
				        		Collections.addAll(JavaScriptNodes_LoopBack, items);
				        		break;
				        	case "JavaScriptNodes_Salesforce":
				        		Collections.addAll(JavaScriptNodes_Salesforce, items);
				        		break;
				        	case "ResourceManagers_FTE":
				        		Collections.addAll(ResourceManagers_FTE, items);
				        		break;
				        	case "ResourceManagers_GlobalCache":
				        		Collections.addAll(ResourceManagers_GlobalCache, items);
				        		break;
				        	case "ResourceManagers_OpenTelemetry":
				        		Collections.addAll(ResourceManagers_OpenTelemetry, items);
				        		break;
				        	case "FlowSecurityProviders_LDAP":
				        		Collections.addAll(FlowSecurityProviders_LDAP, items);
				        		break;
				        	case "FlowSecurityProviders_TFIM":
				        		Collections.addAll(FlowSecurityProviders_TFIM, items);
				        		break;
				        	case "FlowSecurityProviders_WS_Trust":
				        		Collections.addAll(FlowSecurityProviders_WS_Trust, items);
				        		break;
				        	}
				        } 
					} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					}
            	}
            }
        }  
        try {
            FileWriter writer = new FileWriter(source_dir+ System.getProperty("file.separator")+"Consolidated_"+input_file+"_OptimizerReport.txt");
            writer.write("Type: " + type);
            writer.write("\nInput: " + input_file);
            if(type.compareTo("Zip")==0)
            	writer.write("\nList of servers: " + bar_or_server);
            else if(type.compareTo("Bar")==0)
            	writer.write("\nList of bar files: " + bar_or_server);
            writer.write("\nList of apps: " + apps);
            writer.write("\nCount: " + count);
            writer.write("\nCLR: " + CLR);
	        writer.write("\nDeploy: " + Deploy);
	        writer.write("\nJVM: " + JVM);
	        writer.write("\nSSL: " + SSL);
	        writer.write("\nNodeJS: " + NodeJS);
	        writer.write("\nNodes_Dotnet: " + Nodes_Dotnet);
			writer.write("\nNodes_Aggregation: " + Nodes_Aggregation);
			writer.write("\nNodes_Basic: " + Nodes_Basic);
			writer.write("\nNodes_CallableFlow: " + Nodes_CallableFlow);
			writer.write("\nNodes_Database_ODBC: " + Nodes_Database_ODBC);
			writer.write("\nNodes_Connector: " + Nodes_Connector);
			writer.write("\nNodes_ESQL: " + Nodes_ESQL);
			writer.write("\nNodes_Group: " + Nodes_Group);
			writer.write("\nNodes_JMSTransform: " + Nodes_JMSTransform);
			writer.write("\nNodes_MQ: " + Nodes_MQ);
			writer.write("\nNodes_REST: " + Nodes_REST);
			writer.write("\nNodes_Http: " + Nodes_Http);
			writer.write("\nNodes_SecurityPEP: " + Nodes_SecurityPEP);
			writer.write("\nNodes_Timer: " + Nodes_Timer);
			writer.write("\nJavaNodes_Adapters: " + JavaNodes_Adapters);
			writer.write("\nJavaNodes_CICS: " + JavaNodes_CICS);
			writer.write("\nJavaNodes_CORBA: " + JavaNodes_CORBA);
			writer.write("\nJavaNodes_ChangeDataCapture: " + JavaNodes_ChangeDataCapture);
			writer.write("\nJavaNodes_Collection: " + JavaNodes_Collection);
			writer.write("\nJavaNodes_Email: " + JavaNodes_Email);
			writer.write("\nJavaNodes_File: " + JavaNodes_File);
			writer.write("\nJavaNodes_IMS: " + JavaNodes_IMS);
			writer.write("\nJavaNodes_JDBC: " + JavaNodes_JDBC);
			writer.write("\nJavaNodes_JMS: " + JavaNodes_JMS);
			writer.write("\nJavaNodes_JavaCompute: " + JavaNodes_JavaCompute);
			writer.write("\nJavaNodes_JavaDesignerConnectors: " + JavaNodes_JavaDesignerConnectors);
			writer.write("\nJavaNodes_Kafka: " + JavaNodes_Kafka);
			writer.write("\nJavaNodes_MQTT: " + JavaNodes_MQTT);
			writer.write("\nJavaNodes_Mapping: " + JavaNodes_Mapping);
			writer.write("\nJavaNodes_ODM: " + JavaNodes_ODM);
			writer.write("\nJavaNodes_SOAP: " + JavaNodes_SOAP);
			writer.write("\nJavaNodes_TCPIP: " + JavaNodes_TCPIP);
			writer.write("\nJavaNodes_WS-Security: " + JavaNodes_WSSecurity);
			writer.write("\nJavaNodes_WSRR: " + JavaNodes_WSRR);
			writer.write("\nJavaNodes_XSLT: " + JavaNodes_XSLT);	        
			writer.write("\nJavaScriptNodes_LoopBack: " + JavaScriptNodes_LoopBack);
	        writer.write("\nJavaScriptNodes_Salesforce: " + JavaScriptNodes_Salesforce);
	        writer.write("\nResourceManagers_FTE: " + ResourceManagers_FTE);
	        writer.write("\nResourceManagers_GlobalCache: " + ResourceManagers_GlobalCache);
	        writer.write("\nResourceManagers_OpenTelemetry: " + ResourceManagers_OpenTelemetry);
	        writer.write("\nFlowSecurityProviders_LDAP: " + FlowSecurityProviders_LDAP);    
	        writer.write("\nFlowSecurityProviders_TFIM: " + FlowSecurityProviders_TFIM);    
	        writer.write("\nFlowSecurityProviders_WS_Trust: " + FlowSecurityProviders_WS_Trust);
	        writer.close();
            System.out.println("Output written to "+source_dir+ System.getProperty("file.separator")+"Consolidated_"+input_file+"_OptimizerReport.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}
