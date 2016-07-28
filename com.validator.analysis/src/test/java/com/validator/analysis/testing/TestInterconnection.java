package com.validator.analysis.testing;

import static org.junit.Assert.*;

import java.util.jar.Attributes;

import org.junit.Before;
import org.junit.Test;

import com.validator.analysis.JarToClasses;

public class TestInterconnection {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testIfBundleJarIsAccessible() {
		
		JarToClasses bundle = new JarToClasses("/Users/Ben/eclipse/validator/felix-cache/bundle1/data/version1.1.0/bundle.jar");
		assertNotNull("No classes came through the bundle jar", bundle.classes);
		Attributes atts = bundle.attributes;
		assertNotNull("No export packages", atts.getValue("Export-package"));
		
	}
	@Test
	public void testSameInterfaceCanBeFound(){
		JarToClasses bundle = new JarToClasses("/Users/Ben/eclipse/validator/felix-cache/bundle1/data/version1.1.0/bundle.jar");
		JarToClasses jar = new JarToClasses("/Users/Ben/eclipse/felixtutorial/tutorial/src/tutorial/example6/example6.jar");
		//check the 
	}
	@Test
	public void testInterconnectionAndVersionNumbers(){
		//isServiceUsedCorrectly(); need to get the service class loaded, then check method.
	}

}
