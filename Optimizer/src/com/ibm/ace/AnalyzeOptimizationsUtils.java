package com.ibm.ace;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class AnalyzeOptimizationsUtils {
	public static boolean http_check(File app)
	{
		String http_check="ComIbmWSInput";
		File application_descriptor=new File(app+System.getProperty("file.separator")+ "application.descriptor");
		File library_descriptor=new File(app+System.getProperty("file.separator")+ "library.descriptor");
		//System.out.println(curr_app);
		if(application_descriptor.exists() || library_descriptor.exists())
		{
			File[] app_files=app.listFiles();
			for(File app_file : app_files)
			{
				if(app_file.getName().endsWith(".msgflow") || app_file.getName().endsWith(".subflow"))
				{
					Scanner sc;
					try {
						sc = new Scanner(app_file);
						while (sc.hasNextLine())
    			        {
    			        	String temp=sc.nextLine();
    			        	if(temp.contains(http_check))
    			        	{
    			        		return true;
    			        	}	            			        
    			        }
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    }
			}
		}
		return false;
	}
	
	public static boolean database_check(File app)
	{
		Pattern pattern=Pattern.compile("dataSource=\"\\S+\"");
		//System.out.println(curr_app);
		File[] app_files=app.listFiles();
		for(File app_file : app_files)
		{
			if(app_file.getName().endsWith(".msgflow") || app_file.getName().endsWith(".subflow"))
			{
				//System.out.println(app_file.getName());
				Scanner sc;
				try {
					sc = new Scanner(app_file);
					while (sc.hasNextLine())
			        {
			        	String temp=sc.nextLine();
			        	if(pattern.matcher(temp).find())
			        	{
			        		return true;
			        	}	            			        
			        }
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		}
		return false;
	}
	
	// Assisted by watsonx Code Assistant 
    public static List<File> findFiles(File directory, FilenameFilter filter) {
        List<File> files = new ArrayList<>();
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                files.addAll(findFiles(file, filter));
            } else if (filter.accept(file, file.getName())) {
                files.add(file);
            }
        }
        return files;
    }
	
	public static boolean ssl_check_helper(File app, String s)
	{
		Pattern pattern=Pattern.compile(s);
		//System.out.println(curr_app);
		FilenameFilter filter = (dir, name) -> name.endsWith(".msgflow") || name.endsWith(".subflow");
		List<File> flow_Files = findFiles(app, filter);
		for(File flow_File : flow_Files)
		{
			//System.out.println(app_file.getName());
			Scanner sc;
			try {
				sc = new Scanner(flow_File);
				while (sc.hasNextLine())
		        {
		        	String temp=sc.nextLine();
		        	if(pattern.matcher(temp).find())
		        	{
		        		return true;
		        	}	            			        
		        }
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

	public static boolean ssl_check(File app, boolean http, boolean soap, boolean rest, boolean kafka, boolean tcpip)
	{
		File restapi_descriptor=new File(app+ System.getProperty("file.separator") + "restapi.descriptor");
		if(http || restapi_descriptor.exists())
		{
			if(ssl_check_helper(app,"ComIbmWSInput.*useHTTPS=\"true\""))
					return true;
		}
		if(soap)
		{
			if(ssl_check_helper(app,"ComIbmSOAPInput.*useHTTPS=\"true\""))
					return true;
		}
		if(rest)
		{
			if(ssl_check_helper(app,"ComIbmWSInput.*useHTTPS=\"true\""))
					return true;
		}
		if(kafka)
		{
			if(ssl_check_helper(app,"com_ibm_connector_kafka.*securityProtocol=\"SSL\"") || ssl_check_helper(app,"com_ibm_connector_kafka.*securityProtocol=\"SASL_SSL\""))
					return true;
		}
		if(tcpip)
		{
			Pattern pattern=Pattern.compile("<SSLProtocol>[\\S\\s]+[\\S]+</SSLProtocol>");
			File[] app_files=app.listFiles();
			for(File app_file : app_files)
			{
				if(app_file.getName().endsWith(".policyxml"))
				{
					//System.out.println(app_file.getName());
					Scanner sc;
					try {
						sc = new Scanner(app_file);
						while (sc.hasNextLine())
				        {
				        	String temp=sc.nextLine();
				        	if(pattern.matcher(temp).find())
				        	{
				        		return true;
				        	}	            			        
				        }
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    }
			}
		}
		return false;
	}
}
