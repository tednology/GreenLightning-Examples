package com.ociweb.gl.example;

import com.ociweb.gl.api.Builder;
import com.ociweb.gl.api.GreenApp;
import com.ociweb.gl.api.GreenRuntime;

public class SimpleRestServer implements GreenApp {

	int REST_ROUTE_1;
	int REST_ROUTE_2;
	
	public static void main(String[] args) { //All green lightning apps start with this main which passes in a GreenApp instance.
		 GreenRuntime.run(new SimpleRestServer());
	}
	
	@Override
	public void declareConfiguration(Builder builder) {

		boolean isTLS = true;
		boolean isLarge = false;
		String bindHost = "127.0.0.1";
		int bindPort = 8081;
		
		builder.enableServer(isTLS, isLarge, bindHost, bindPort);//turn on the web server
		
		builder.parallelism(4); //multiple instances of module will be running.
		
		REST_ROUTE_1 = builder.registerRoute("/groovyadd/%i%./%i%.");//these routes take 2 decimal (eg floating point) numbers
		REST_ROUTE_2 = builder.registerRoute("/add/%i%./%i%.");
		
		//REST_ROUTE_1 = builder.registerRoute("/groovyadd/%i/%i");//these routes only except simple integers
		//REST_ROUTE_2 = builder.registerRoute("/add/%i/%i");
				
	}

	@Override
	public void declareBehavior(GreenRuntime runtime) {		
		//NOT NEEDED FOR THIS EXAMPLE
	}

	@Override
	public void declareParallelBehavior(GreenRuntime runtime) {	
		runtime.addRestListener(new MathUnit(runtime, REST_ROUTE_1, REST_ROUTE_2)); //takes either route
	}
	

}
