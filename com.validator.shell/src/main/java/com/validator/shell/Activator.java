package com.validator.shell;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Benjamin Farrelly
 *
 *	This bundle is designed to take the shell commands for the shell allowing for bundles to be validated 
 *	against the current working set of bundles and services.
 */
public class Activator implements BundleActivator {

	ServiceRegistration<?> shellRegistration = null;
	@SuppressWarnings("unchecked")
	@Override
	public void start(BundleContext bundleContext) {
		
		Hashtable properties = new Hashtable();
		properties.put("osgi.command.scope", "validate");
		properties.put("osgi.command.function", 
				new String[] { "update", "interconnection", "totalinterconnection" });
	shellRegistration =	bundleContext.registerService(
			ShellCommands.class.getName(), 
			new ShellCommands(bundleContext), properties);
		System.out.println("Activator bundle now running, validate commands can now be used");
		
		
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		
		if(shellRegistration != null){
			
			bundleContext.ungetService(shellRegistration.getReference());
			
		}
		// TODO Auto-generated method stub
		System.out.println("Validator stopped");
		
	}

}
