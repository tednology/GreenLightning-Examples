package com.ociweb.gl.example;

import com.ociweb.gl.api.Builder;
import com.ociweb.gl.api.GreenApp;
import com.ociweb.gl.api.GreenRuntime;

public class StaticFileServer implements GreenApp { //All green lightning apps must implement this interface

	int STATIC_FILES_ROUTE;
	final String pathToIndex;
		
	
	public StaticFileServer(String pathToIndex) {
		this.pathToIndex = pathToIndex;
	}
	
	public static void main(String[] args) { //All green lightning apps start with this main which passes in a GreenApp instance.
		 
		//An example argument
		//  --p /home/nate/git/GreenLightning/src/main/resources/site/index.html
		
		String pathToIndexFile = GreenRuntime.getOptArg("path", "--p", args, null);

		if (null==pathToIndexFile) {
			System.out.println("Set the path argument to the absolute drive path for index.html or any other 'default' landing page");
			return;
		}		
		
		GreenRuntime.run(new StaticFileServer(pathToIndexFile));
		  
		 
	}
	
	@Override
	public void declareConfiguration(Builder builder) {

		boolean isTLS = false;
		boolean isLarge = false;
		String bindHost = "127.0.0.1";
		int bindPort = 8081;
		
		builder.enableServer(isTLS, isLarge, bindHost, bindPort);//turn on the web server
		
		STATIC_FILES_ROUTE = builder.registerRoute("/%b"); //define the route to wich the module will respond.
		
	}

	@Override
	public void declareBehavior(GreenRuntime runtime) {		
		
		runtime.addFileServer(pathToIndex, STATIC_FILES_ROUTE); //add file server module to this route 

	}

	@Override
	public void declareParallelBehavior(GreenRuntime runtime) {	
		//NOT NEEDED FOR THIS SIMPLE EXAMPLE
	}
	

}
