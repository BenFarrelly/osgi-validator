package com.validator.shell;

import org.apache.felix.service.command.Descriptor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import com.validator.analysis.*;
public class ShellCommands {
	//This class is a bundle which gives a customised version of the Gogo shell giving the extra commands in validation.
	private final BundleContext bundleContext;
	private ConfigurationAdmin configAdmin;
	
	ShellCommands(BundleContext bundleContext){
		
		this.bundleContext = bundleContext;
		
		//getConfigAdmin();
	}
	@Descriptor("Validate a bundle for updating.")
	public void update(int bundleNumber, String path){
		//This method is used to validate that a bundle can indeed update safely - in that nomethod and typemismatch exceptions will not occur. 
		//These are typically unchecked by osgi.
		
		//Firstly the classes are taken from the Bundle
		JarToClasses jar = new JarToClasses(path);
		//TODO take the code from the tests
	}
	public void update(int bundleNumber){
		
	}
	@Descriptor("Used for checking that a bundle implements a service correctly, give either paths or bundle numbers for the validating bundle and the bundle that contains the service.")
	public void interconnection(String path, String path2){
		
	}
	public void interconnection(int bundeNumber, int bundleNumber){
		
	}
	
}
