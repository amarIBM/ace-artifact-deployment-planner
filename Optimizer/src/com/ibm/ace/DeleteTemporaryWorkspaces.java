package com.ibm.ace;

import java.io.File;

public class DeleteTemporaryWorkspaces {
	public static void deleteDirectory(File file)
    {
        for (File subfile : file.listFiles()) {
            if (subfile.isDirectory()) {
                deleteDirectory(subfile);
            }
            else {
            	subfile.delete();
            }
        }
        file.delete();
    }
	
	public static void main(String[] args)
	{
		//to delete all temporary workspaces created
        
		File source_dir = new File(System.getProperty("user.dir"));
        source_dir = new File("/Users/charithakreddy/IBM/ACET13/workspace/TEST_SERVER2");
        File[] children = source_dir.listFiles();
        for (File subdir : children)
        {
        	if (subdir.isDirectory() && subdir.getName().startsWith("temp_"))
        	{
        		deleteDirectory(subdir);
        	}
        }
	}
}
