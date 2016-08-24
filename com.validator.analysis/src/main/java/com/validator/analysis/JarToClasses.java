package com.validator.analysis;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.osgi.framework.BundleContext;
/*
 * This class exists to take a Jar file by with either a JarFile object or a path.
 * @author Benjamin Farrelly
 * 
 * @Parameters JarFile or path to the Jar file
 * @Returns Classes from the Jar file for other classes to analyze via reflection
 * TODO Add loggers
 * TODO Make this code more generic, constructors only accessing Jar, write method to actually get classes
 * 
 */
public class JarToClasses {
	public ArrayList<Class<?>> classes;
	public Attributes attributes;
	boolean interfaceChecking;
	//This boolean is used if only interface checking is selected.
	boolean wantsInterfaces = false;
	
	JarFile file;
//	public JarToClasses(JarFile jar){
//		classes = new ArrayList<Class>();
//		Enumeration<JarEntry> entries = jar.entries();
//		for(JarEntry entry = entries.nextElement(); entry != null; entry = entries.nextElement()){
//			if(!entry.isDirectory() && entry.getName().endsWith(".class")) {
//				classes.add(entry);
//			}
//		}
//	}
//	
	
	public JarToClasses(String jar) {
		String className;
		classes = new ArrayList<Class<?>>();
		//HashMap<String,String> manifest;
		
		
		try {
			URL[] urls = { new URL("jar:file:" + jar + "!/") };
			URLClassLoader cl = URLClassLoader.newInstance(urls);
			
			file = new JarFile(jar);
			Enumeration<JarEntry> entries = file.entries();
			while(entries.hasMoreElements()){
				JarEntry entry = entries.nextElement();
				if(entry.getName().contains("$1"))
					continue;
				if(!entry.isDirectory() && entry.getName().endsWith(".class")) {
					//-6 because of .class
					className = entry.getName().substring(0, entry.getName().length()-6)
							.replace('/', '.');
					Class<?> loadingAndAddingToMap = null;
					try{
						loadingAndAddingToMap = cl.loadClass(className);
					} catch (NoClassDefFoundError e){
						System.out.println();
						System.out.println(className + " was not able to load, likely because a service is being implemented.");
						System.out.println("Ensure that the required service is in OSGi before starting this bundle.");
						System.out.println("The rest of the analysis will continue and we shall attempt to start the bundle.");
						System.out.println();
					} catch (ClassNotFoundException e){
						System.out.println();
						System.out.println(className + " was not able to load, likely because a service is being implemented.");
						System.out.println("Ensure that the required service is in OSGi before starting this bundle.");
						System.out.println("The rest of the analysis will continue and we shall attempt to start the bundle.");
						System.out.println();
					} 
					
					//if interface checking only, confirm class is an interface. 
					//If not using interface checking add regardless
					if((wantsInterfaces && loadingAndAddingToMap.isInterface()) || !wantsInterfaces){
						classes.add(loadingAndAddingToMap);
					}
					
				} else if(entry.getName().equals("META-INF/MANIFEST.MF") || entry.getName().equals("MANIFEST.MF")) {
					
						// ||entry.getName().endsWith(".MF"))){
					attributes = getManifestMetadata(entry, file);
					
					
				} else if(entry.isDirectory() && entry.getName().equals("META-INF")){
					//entry = entry.
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//TODO  More methods for converting Jars?
		//MapBuilder.putClassesIntoGlobalMap(classes);
		
		
	}
	public static Attributes getManifestMetadata(JarEntry entry, JarFile file){
		//For getting manifest metadata by reading the contents of the manifest.mf file.
		//TODO implement this method.
		
		//Only need to check import header
		
			HashMap<String, String> manifestMap = new HashMap<String, String>();
			String value;
			Attributes attributes = null;
			try {
			InputStream input = file.getInputStream(entry);
			Manifest mf = new Manifest(input);
			Map<String, Attributes> maniMap = mf.getEntries();
			
			//Headers
		//	Set<String> headers = maniMap.keySet();
			//Iterator<String> it = headers.iterator();
			ArrayList<String> manifestHeaders = new ArrayList<String>();
			ArrayList<String> manifestValues = new ArrayList<String>();
			
			attributes = mf.getMainAttributes();
			value = attributes.getValue("Export-package");
			
//			Set<Object> attribNames = attributes.keySet();
//			Iterator<Object> attribIter = attribNames.iterator();
//			while(attribIter.hasNext()){
//				Attributes atts = (Attributes) attribIter.next();
//				String head = atts.toString(); 
//				manifestHeaders.add(head);
//				manifestValues.add(atts.getValue(head));
//				manifestMap.put(head, atts.getValue(head));
//			}
//			
			//Attributes -> strings Probably not neeeeeeeeeeeeeeeeeeeded
//			Collection<Attributes> attribs = maniMap.values();
//			Iterator<Attributes> iter = attribs.iterator();
//			ArrayList<String> attributeStrings = new ArrayList<String>();
//			ArrayList<String> attributeValues = new ArrayList<String>();
//			while(iter.hasNext()){
//				Attributes att = iter.next();
//				Collection<Object>attValues = att.values();
//				Iterator<Object> attIter = attValues.iterator();
//				while(attIter.hasNext()){
//					
//					String attStr = (String) attIter.next();
//					attributeStrings.add(attStr);
//			//		attributeValues.add()
//				}
//			}
			
			//InputStreamReader isr = new InputStreamReader(input);
			//BufferedReader reader = new BufferedReader(isr);
			//Need to read manifest file which follows the format of
			//header: value, value;
			//while()
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return attributes;
		
	}
	
	public static Class<?> getLoadedClass(String clazz){
		Class<?> thisClass = null;
		try {
			URL[] urls={ new URL("file:" + clazz + "!/") };
			URLClassLoader cl = URLClassLoader.newInstance(urls);
			//clazz = clazz.substring(0, clazz.length()-6).replace('/', '.');
			thisClass = cl.loadClass(clazz);
			
		
		} catch (MalformedURLException e){
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return thisClass;
		
	}
	
	ArrayList<Class> getInterfaces(ArrayList<Class> classes){
		ArrayList<Class> interfaces = new ArrayList<Class>();
		for(Class c : classes){
			Class possibleInterface;
			if(c.isInterface())  interfaces.add(c);
			
		}
		return interfaces;
	}
	
	
	
	//This code is probably now obsolete
	ArrayList<Class> getClasses(List<String> classNames){
		//This class returns class instances from the class 
		ArrayList<Class> classes = new ArrayList<Class>();
		for(String name : classNames){
			Class possibleClass;
			
			try{
				possibleClass = Class.forName(name);
				classes.add(possibleClass);
			} catch (ClassNotFoundException e){
				//TODO incomplete catch block
				e.printStackTrace();
			}
		}
		
		return classes;
		
	}
}
