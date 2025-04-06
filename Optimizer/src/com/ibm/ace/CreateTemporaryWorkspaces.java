/********************************************************* {COPYRIGHT-TOP} ***
* Copyright 2022 IBM Corporation
*
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the MIT License
* which accompanies this distribution
 ********************************************************** {COPYRIGHT-END} **/

 package com.ibm.ace;

 import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
 import java.nio.file.Path;
 import java.nio.file.Paths;
 import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.IOException;
 import javax.xml.parsers.DocumentBuilderFactory;
 import org.w3c.dom.Document;
 import org.w3c.dom.Node;
 import org.w3c.dom.NodeList;
 import javax.xml.parsers.DocumentBuilder;
 
 public class CreateTemporaryWorkspaces{

		 public static void cpydir(Path sourceDirectoryLocation, Path destinationDirectoryLocation) 
				{
				    try {
						Files.walk(sourceDirectoryLocation)
						  .forEach(source -> {
						      Path destination = Paths.get(destinationDirectoryLocation.toString(), source.toString()
						        .substring(sourceDirectoryLocation.toString().length()));
						      try {
						          Files.copy(source, destination);
						      } catch (IOException e) {
						          e.printStackTrace();
						      }
						  });
					} catch (IOException e) {
						// TODO Auto-generated catch block
						System.out.println("              Error copying "+sourceDirectoryLocation+" to "+destinationDirectoryLocation);
						System.out.println("              "+e.toString());
					}
				}
		 
		 public static void check_config(File config_dir)
		 {
			 if(!config_dir.exists())
				 config_dir.mkdir();
			 File common_dir = new File(config_dir+ System.getProperty("file.separator") + "common");
			 if(!common_dir.exists())
				 common_dir.mkdir();
			 File log_dir = new File(common_dir+ System.getProperty("file.separator") + "log");
			 if(!log_dir.exists())
				 log_dir.mkdir();
			 File registry_dir = new File(config_dir+ System.getProperty("file.separator") + "registry");
			 if(!registry_dir.exists())
				 registry_dir.mkdir();
			 File integration_server_dir = new File(registry_dir+ System.getProperty("file.separator") + "integration_server");
			 if(!integration_server_dir.exists())
				 integration_server_dir.mkdir();
		 }
		 
		 public static void copy_policy_apps(File source_dir, File new_run_dir, File new_run_app_dir)
		 {
			 Pattern pattern=Pattern.compile("ConfigurableProperty override=\"\\{\\S+\\}:\\S+\"");
			 File broker_xml = new File(new_run_app_dir+ System.getProperty("file.separator") + "META-INF" + System.getProperty("file.separator") + "broker.xml");
			 if(broker_xml.exists())
			 {
				 Scanner sc;
					try {
						sc = new Scanner(broker_xml);
						while (sc.hasNextLine())
				        {
				        	String curr_line=sc.nextLine();
				        	Matcher matcher = pattern.matcher(curr_line);
				        	if(matcher.find())
				        	{
				        		String match=matcher.group();
				        		String policy=match.substring(match.indexOf("{")+1, match.indexOf("}"));
				        		//Copy the library
					    	 	File from_dir = new File(source_dir+ System.getProperty("file.separator")+ "run"+ System.getProperty("file.separator") + policy);
							    File to_dir = new File(new_run_dir+ System.getProperty("file.separator") + policy);
								cpydir(from_dir.toPath(), to_dir.toPath());
				        	}	            			        
				        }
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			 }
		 }
		 
		 public static boolean isValid(File app)
		 {
			 if (app.isDirectory() && !app.isHidden())
			 {
				 File application_descriptor=new File(app+ System.getProperty("file.separator") + "application.descriptor");
				 File service_descriptor=new File(app+ System.getProperty("file.separator") + "service.descriptor");
				 File restapi_descriptor=new File(app+ System.getProperty("file.separator") + "restapi.descriptor");
				 if(application_descriptor.exists() || service_descriptor.exists() || restapi_descriptor.exists())
					 return true;
				 else
					 return false;
			 }
			 else
				 return false;
		 }
		 
		 public static void copy_shared_lib(File source_dir, File new_run_dir, File new_run_app_dir)
		 {
			 File application_descriptor=new File(new_run_app_dir+ System.getProperty("file.separator") + "application.descriptor");
			 if(application_descriptor.exists())
			    {
			    	try {      
				         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
					     DocumentBuilder builder = factory.newDocumentBuilder();
					     Document xmldoc = builder.parse(application_descriptor);
					   
					     NodeList sharedLibraryReferences = xmldoc.getElementsByTagName("sharedLibraryReference");
					     //System.out.print("\tShared Libraries:"+sharedLibraryReferences.getLength());
					     for (int j = 0; j < sharedLibraryReferences.getLength(); j++) {
					    	 	Node lib = sharedLibraryReferences.item(j);
					    	 	//System.out.print("\t"+lib.getTextContent());
					    	 	
					    	 	//Copy the library
					    	 	File from_dir = new File(source_dir+ System.getProperty("file.separator")+ "run"+ System.getProperty("file.separator") + lib.getTextContent());
							    File to_dir = new File(new_run_dir+ System.getProperty("file.separator") + lib.getTextContent());
								cpydir(from_dir.toPath(), to_dir.toPath());
			                }
					    	  
				      } catch (Exception e) {
				         e.printStackTrace();
				      }
			    }
		 }
		 
		 public static void create_work_dir(File source_dir, File app, File config_dir, File overrides_dir, File serverconfyaml)
		 {
			File new_dir = new File(source_dir + File.separator + "temp_" + app.getName());
            new_dir.mkdir();
            
            File new_run_dir = new File(new_dir+ System.getProperty("file.separator") + "run");
            new_run_dir.mkdir();
            
            File new_config_dir = new File(new_dir+ System.getProperty("file.separator") + "config");
		    File new_overrides_dir = new File(new_dir+ System.getProperty("file.separator") + "overrides");
		    File new_run_app_dir = new File(new_run_dir+ System.getProperty("file.separator") + app.getName());
		    File new_serverconfyaml = new File(new_dir+ System.getProperty("file.separator") + "server.conf.yaml");
		    //System.out.println(config_dir.getAbsolutePath()+"\n"+ new_config_dir.getAbsolutePath());
			
		    try {
				Files.copy(serverconfyaml.toPath(), new_serverconfyaml.toPath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Error copying "+serverconfyaml+" to "+new_serverconfyaml);
			}
		    cpydir(config_dir.toPath(), new_config_dir.toPath());
			cpydir(overrides_dir.toPath(), new_overrides_dir.toPath());
			cpydir(app.toPath(), new_run_app_dir.toPath());
			
			//copy policy apps
			copy_policy_apps(source_dir, new_run_dir, new_run_app_dir);
		    //copy shared libraries
		    copy_shared_lib(source_dir, new_run_dir, new_run_app_dir);
		 }
		 
		 public static void main(String[] args) {

	        File source_dir = new File(System.getProperty("user.dir"));
	        //System.out.println("Source_dir: "+source_dir);
	        //source_dir = new File("/Users/charithakreddy/IBM/ACET13/workspace/TCP_Deploy");
		    File config_dir = new File(source_dir+ System.getProperty("file.separator") + "config");
		    File overrides_dir = new File(source_dir+ System.getProperty("file.separator") + "overrides");
		    File run_dir = new File(source_dir+ System.getProperty("file.separator") + "run");
		    File serverconfyaml = new File(source_dir+ System.getProperty("file.separator") + "server.conf.yaml");
		    //System.out.println(run_dir);
		    check_config(config_dir);
		    File[] apps = run_dir.listFiles();
		    int no_of_apps=apps.length;
	        for (int i=0;i<no_of_apps;i++) {
	        	if(isValid(apps[i])) {
	        		System.out.println(apps[i].getName());
	        		create_work_dir(source_dir, apps[i], config_dir,overrides_dir,serverconfyaml);
					
	        	}	
	        }
	        System.out.println();
	    }
	 }
