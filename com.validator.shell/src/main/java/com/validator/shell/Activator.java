package com.validator.shell;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		Hashtable properties = new Hashtable();
		properties.put("osgi.command.scope", "validate");
		properties.put("osgi.command.function", 
				new String[] { "update", "interconnection" });
		bundleContext.registerService(ShellCommands.class.getName(), new ShellCommands(bundleContext), properties);
		
	}

	@Override
	public void stop(BundleContext arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
