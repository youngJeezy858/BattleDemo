package driver;
import java.io.File;
import java.util.zip.ZipEntry;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

import com.erebos.engine.core.*;


/**
 * Contains the main method for running the game. 
 */
public class Driver extends EGame
{

	/**
	 * Instantiates a new driver.
	 */
	public Driver() 
	{
		super("demoForBurry");
		setShowFPS(true);
		addCanvas(BattleCanvas.getC());
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args)
	{
		File homeFolder=new File(System.getProperty("user.home")+File.separator+"Demo");
		if(!homeFolder.exists())
			homeFolder.mkdir();
	
		copyNatives(homeFolder);
		System.setProperty("org.lwjgl.librarypath", homeFolder.getAbsolutePath());
		
		Driver d = new Driver();
		d.setTargetFPS(60);
		d.start(d, 1106, 715, false);
	}
	
	/**
	 * Copy natives.  Determines what OS you're running and implements the proper natives.
	 * Will be removed once I get the newest jar of Erebos
	 *
	 * @param destDir the dest dir
	 */
	public static void copyNatives(File destDir)
	{
	
		Driver.class.getClassLoader();
		InputStream stream=ClassLoader.getSystemResourceAsStream("natives/natives.zip");
		String osArch=System.getProperty("os.arch");
		String osName=System.getProperty("os.name");
				
		byte[] buffer = new byte[1024];
		ZipInputStream zis=null;
		     try
		     {
		    	 zis = new ZipInputStream(stream);
		    	ZipEntry ze = zis.getNextEntry();
		    	while(ze!=null)
		    	{
		    	   String fileName = ze.getName();
		    	   if((fileName.contains(".so") && osName.contains("inux")) || 
		    		  ((fileName.contains(".jnilib")||fileName.contains(".dylib"))&&osName.contains("Mac")) ||
		    		  (!fileName.contains("64") && fileName.contains(".dll") && osName.contains("Win") && osArch.contains("86")) ||
		    		  (fileName.contains(".dll") && fileName.contains("64") &&osName.contains("Win") &&osArch.contains("64")))
		    	  {
		           File newFile = new File(destDir + File.separator + fileName);
		           if(ze.isDirectory())  //Make sure we make a directory if we run into one in case it exist or we'll get exceptions
		           {
		        	   new File(newFile.getParent()).mkdirs();
		           }
		           else
		           {
		        	FileOutputStream fos = new FileOutputStream(newFile);    
		            new File(newFile.getParent()).mkdirs();             
		            int len;
		            
		            while ((len = zis.read(buffer)) > 0) 
		            {
		            	fos.write(buffer, 0, len);
		            }
		            
		            fos.close();   
		            
		           	} 
		           }
		           ze = zis.getNextEntry();
		    	}
		        zis.closeEntry();
		    	zis.close();
		 
		     }
		     catch(IOException ex)
		     {
		    	 System.err.println("Error: Couldn't extract natives :"+ex.getMessage()); 
		     }
		     
	 }    
	
	
	
}