/********************************************************* {COPYRIGHT-TOP} ***
* Copyright 2022 IBM Corporation
*
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the MIT License
* which accompanies this distribution
 ********************************************************** {COPYRIGHT-END} **/

package com.ibm.ace;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

public class AnalyzeOptimizations {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		
		   String type=args[0];
		   String input_file=args[1];
		   int count=0;
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
		   boolean http=false,soap=false,rest=false,kafka=false,tcpip=false;
		   
		   
		    File currentdir = new File(System.getProperty("user.dir"));
		    //currentdir = new File("/Users/charithakreddy/IBM/ACET13/workspace/TEST_SERVER2");
	        File[] children = currentdir.listFiles();
	        for (File subdir : children) {
	            if (subdir.isDirectory()) {	  
	            	File serverconfyaml = new File(subdir+ System.getProperty("file.separator") + "server.components.yaml");
	            	if (serverconfyaml.exists()) { 
	            		count++;
	            		String app_name=subdir.getName().substring(5);
	            		System.out.println(app_name);
	            		File curr_app=new File(subdir+ System.getProperty("file.separator") +"run"+System.getProperty("file.separator")+app_name);
	            		app_name=input_file+"|"+app_name;
	            		apps.add(app_name);
	            		if(AnalyzeOptimizationsUtils.http_check(curr_app))
	            		{
	            			http=true;
	            			Nodes_Http.add(app_name);
	            		}
	            		if(AnalyzeOptimizationsUtils.database_check(curr_app))
	            			Nodes_Database_ODBC.add(app_name);
	            		Yaml yaml = new Yaml();
	            		InputStream inputStream;
	            		try {
	        			   inputStream = new FileInputStream(serverconfyaml);		
	        			   Map<String, Object> obj = yaml.load(inputStream);
	        			   
	        			   // Record matches for CLR, Deploy, JVM, NodeJS
	        			   if (obj.get("CLR").equals(true)) {CLR.add(app_name);}
	        			   if (obj.get("Deploy").equals(true)) {Deploy.add(app_name);}
	        			   if (obj.get("JVM").equals(true)) {JVM.add(app_name);}
	        			   
	        			   if (obj.get("NodeJS").equals(true)) {NodeJS.add(app_name);}
	        			   
	        			   // Record matches for Nodes
	        			   Map<String, Object> NodesObject = (Map<String, Object>) obj.get("Nodes");
	        			   if (NodesObject.get(".NET").equals(true)) {Nodes_Dotnet.add(app_name);}	        			   
	        			   if (NodesObject.get("Aggregation").equals(true)) {Nodes_Aggregation.add(app_name);}
	        			   if (NodesObject.get("Basic").equals(true)) {Nodes_Basic.add(app_name);}
	        			   if (NodesObject.get("CallableFlow").equals(true)) {Nodes_CallableFlow.add(app_name);}
	        			   if (NodesObject.get("Connector").equals(true)) {Nodes_Connector.add(app_name);}
	        			   if (NodesObject.get("ESQL").equals(true)) {Nodes_ESQL.add(app_name);}
	        			   if (NodesObject.get("Group").equals(true)) {Nodes_Group.add(app_name);}
	        			   if (NodesObject.get("JMSTransform").equals(true)) {Nodes_JMSTransform.add(app_name);}
	        			   if (NodesObject.get("MQ").equals(true)) {Nodes_MQ.add(app_name);}
	        			   if (NodesObject.get("REST").equals(true)) {rest=true; Nodes_REST.add(app_name);}
	        			   if (NodesObject.get("SecurityPEP").equals(true)) {Nodes_SecurityPEP.add(app_name);}
	        			   if (NodesObject.get("Timer").equals(true)) {Nodes_Timer.add(app_name);}		   

	        			   // Record matches for JavaNodes	        			   
	        			   Map<String, Object> JavaNodesObject = (Map<String, Object>) obj.get("JavaNodes");
	        			   if (JavaNodesObject.get("Adapters").equals(true)) {JavaNodes_Adapters.add(app_name);}	        			   
	        			   if (JavaNodesObject.get("CICS").equals(true)) {JavaNodes_CICS.add(app_name);}
	        			   if (JavaNodesObject.get("CORBA").equals(true)) {JavaNodes_CORBA.add(app_name);}
	        			   if (JavaNodesObject.get("ChangeDataCapture").equals(true)) {JavaNodes_ChangeDataCapture.add(app_name);}
	        			   if (JavaNodesObject.get("Collection").equals(true)) {JavaNodes_Collection.add(app_name);}
	        			   if (JavaNodesObject.get("Email").equals(true)) {JavaNodes_Email.add(app_name);}
	        			   if (JavaNodesObject.get("File").equals(true)) {JavaNodes_File.add(app_name);}
	        			   if (JavaNodesObject.get("IMS").equals(true)) {JavaNodes_IMS.add(app_name);}
	        			   if (JavaNodesObject.get("JDBC").equals(true)) {JavaNodes_JDBC.add(app_name);}
	        			   if (JavaNodesObject.get("JMS").equals(true)) {JavaNodes_JMS.add(app_name);}
	        			   if (JavaNodesObject.get("JavaCompute").equals(true)) {JavaNodes_JavaCompute.add(app_name);}
	        			   if (JavaNodesObject.get("JavaDesignerConnectors").equals(true)) {JavaNodes_JavaDesignerConnectors.add(app_name);}
	        			   if (JavaNodesObject.get("Kafka").equals(true)) {kafka=true; JavaNodes_Kafka.add(app_name);}
	        			   if (JavaNodesObject.get("MQTT").equals(true)) {JavaNodes_MQTT.add(app_name);}
	        			   if (JavaNodesObject.get("Mapping").equals(true)) {JavaNodes_Mapping.add(app_name);}
	        			   if (JavaNodesObject.get("ODM").equals(true)) {JavaNodes_ODM.add(app_name);}
	        			   if (JavaNodesObject.get("SOAP").equals(true)) {soap=true; JavaNodes_SOAP.add(app_name);}
	        			   if (JavaNodesObject.get("TCPIP").equals(true)) {tcpip=true; JavaNodes_TCPIP.add(app_name);}
	        			   if (JavaNodesObject.get("WS-Security").equals(true)) {JavaNodes_WSSecurity.add(app_name);}
	        			   if (JavaNodesObject.get("WSRR").equals(true)) {JavaNodes_WSRR.add(app_name);}
	        			   if (JavaNodesObject.get("XSLT").equals(true)) {JavaNodes_XSLT.add(app_name);}
	        			   
	        			   // Record matches for JavaScriptNodes
	        			   Map<String, Object> JavaScriptNodesObject = (Map<String, Object>) obj.get("JavaScriptNodes");
	        			   if (JavaScriptNodesObject.get("LoopBack").equals(true)) {JavaScriptNodes_LoopBack.add(app_name);}
	        			   if (JavaScriptNodesObject.get("Salesforce").equals(true)) {JavaScriptNodes_Salesforce.add(app_name);}
	        			   
	        			   // Record matches for ResourceManagers
	        			   Map<String, Object> ResourceManagersObject = (Map<String, Object>) obj.get("ResourceManagers");
	        			   if (ResourceManagersObject.get("FTE").equals(true)) {ResourceManagers_FTE.add(app_name);}
	        			   if (ResourceManagersObject.get("GlobalCache").equals(true)) {ResourceManagers_GlobalCache.add(app_name);}
	        			   if (ResourceManagersObject.get("OpenTelemetry").equals(true)) {ResourceManagers_OpenTelemetry.add(app_name);}

	        			   // Record matches for FlowSecurityProviders
	        			   Map<String, Object> FlowSecurityProvidersObject = (Map<String, Object>) obj.get("FlowSecurityProviders");
	        			   if (FlowSecurityProvidersObject.get("LDAP").equals(true)) {FlowSecurityProviders_LDAP.add(app_name);}
	        			   if (FlowSecurityProvidersObject.get("TFIM").equals(true)) {FlowSecurityProviders_TFIM.add(app_name);}
	        			   if (FlowSecurityProvidersObject.get("WS-Trust").equals(true)) {FlowSecurityProviders_WS_Trust.add(app_name);}	
	        			   
	        			   if (AnalyzeOptimizationsUtils.ssl_check(curr_app, http, soap, rest, kafka, tcpip)) {SSL.add(app_name);}
	            		} catch (FileNotFoundException e) {
	            			// TODO Auto-generated catch block
	            			e.printStackTrace();
	            		} 
	            	}
	            }
	        }
	        try {
	            FileWriter writer = new FileWriter(currentdir+ System.getProperty("file.separator")+"output.txt");
	            writer.write("Type: " + type);
	            writer.write("\nInput: " + input_file);
	            if(type.compareTo("Zip")==0)
	            	writer.write("\nList of servers: [" + input_file + "]");
	            else if(type.compareTo("Bar")==0)
	            	writer.write("\nList of bar files: [" + input_file + "]");
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
	            //System.out.println("Output written to "+currentdir+ System.getProperty("file.separator")+"output.txt");
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        
	   }
}
