package com.ociweb.gl.example;

import com.ociweb.gl.api.Builder;
import com.ociweb.gl.api.GreenApp;
import com.ociweb.gl.api.GreenRuntime;

public class FullRestServer implements GreenApp {

	int REST_ROUTE_1;
	int REST_ROUTE_2;
	int STATIC_FILES_ROUTE;
	final String pathToIndex;
	
	FullRestServer(String pathToIndex) {
		this.pathToIndex = pathToIndex;
	}
	
	public static void main(String[] args) { //All green lightning apps start with this main which passes in a GreenApp instance.
		
		 String path = FullRestServer.class.getResource("/site/index.html").toString().replace("file:", "");

		 GreenRuntime.run(new FullRestServer(path));
	}
	
	@Override
	public void declareConfiguration(Builder builder) {

		boolean isTLS = false;
		boolean isLarge = false;
		String bindHost = "127.0.0.1";
		int bindPort = 8081;
		
		builder.enableServer(isTLS, isLarge, bindHost, bindPort);//turn on the web server
		
		builder.parallelism(4); //multiple instances of module will be running.
		
		REST_ROUTE_1 = builder.registerRoute("/groovyadd/%i%./%i%.");//these routes take 2 decimal (eg floating point) numbers
		REST_ROUTE_2 = builder.registerRoute("/add/%i%./%i%.");
		
		
		STATIC_FILES_ROUTE = builder.registerRoute("/%b"); //define the route to wich the module will respond.
			
	}

	@Override
	public void declareBehavior(GreenRuntime runtime) {		
		runtime.addFileServer(pathToIndex, STATIC_FILES_ROUTE); //This is needed to capure unknonwn routes and return 404
	}

	@Override
	public void declareParallelBehavior(GreenRuntime runtime) {	
		runtime.addRestListener(new MathUnit(runtime, REST_ROUTE_1, REST_ROUTE_2)); //takes either route
	}
	

}
