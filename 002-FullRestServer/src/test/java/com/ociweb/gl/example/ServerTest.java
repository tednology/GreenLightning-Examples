package com.ociweb.gl.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ociweb.gl.api.GreenRuntime;

public class ServerTest {

	
	private static GreenRuntime runtime;

	@BeforeClass
	public static void start() {
		 String path = FullRestServer.class.getResource("/site/index.html").toString().replace("file:", "");

		runtime = GreenRuntime.run(new FullRestServer(path));//NOTE: it would be nice if this could pick its own port in case of collision while testing.
	}
	
	@AfterClass
	public static void end() {
		
		runtime.shutdownRuntime();
	}
	
	@Test
	public void serverTest() {
		
		try {
			URL url = new URL("http://127.0.0.1:8081/groovyadd/2.3/7.52");
			
			InputStream stream = url.openStream();
			
			Scanner scanner = new Scanner(stream);
			
			assertTrue(scanner.hasNextLine());
			
			String v = scanner.nextLine();
			//System.out.println(v);

			assertEquals("{\"x\":2.3,\"y\":7.52,\"groovySum\":9.82}", v);
						
			
		} catch (MalformedURLException e) {			
			e.printStackTrace();
			fail();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
		
	}
	
}
