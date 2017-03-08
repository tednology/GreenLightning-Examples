package com.ociweb.gl.example;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ociweb.gl.api.CommandChannel;
import com.ociweb.gl.api.GreenRuntime;
import com.ociweb.gl.api.ListenerConfig;
import com.ociweb.gl.api.NetResponseWriter;
import com.ociweb.gl.api.PayloadReader;
import com.ociweb.gl.api.RestListener;
import com.ociweb.pronghorn.network.config.HTTPContentTypeDefaults;
import com.ociweb.pronghorn.network.config.HTTPVerb;
import com.ociweb.pronghorn.network.schema.HTTPRequestSchema;
import com.ociweb.pronghorn.pipe.DataInputBlobReader;
import com.ociweb.pronghorn.util.Appendables;
import com.ociweb.pronghorn.util.math.Decimal;

public class MathUnit implements RestListener {

	private final Logger logger = LoggerFactory.getLogger(MathUnit.class);
	
	private final CommandChannel cc; 
	private final ListenerConfig lc;
	
	//example response UTF-8 encoded
	//{"x":9,"y":17,"groovySum":26}
	private final byte[] part1 = "{\"x\":".getBytes();
	private final byte[] part2 =          ",\"y\":".getBytes();
	private final byte[] part3 =                    ",\"groovySum\":".getBytes();
	private final byte[] part4 =                                      "}".getBytes();
	
	//these member vars can be used because the stage will only use 1 thread for calling process method.
	private final StringBuilder a = new StringBuilder();
	private final StringBuilder b = new StringBuilder();
	private final StringBuilder c = new StringBuilder();
	
	public MathUnit(final GreenRuntime runtime, int ... routes) {
		assert(routes.length>0) : "This rest module must be associated with at least 1 route";
		
		//create object used for sending the responses
		this.cc = runtime.newCommandChannel(CommandChannel.NET_RESPONDER);

		//create object to declare which routes this class will respond to
		this.lc = runtime.newRestListenerConfig(routes);
				
	}
	
	
	
	@Override
	public boolean restRequest(int routeId, long connectionId, long sequenceCode, HTTPVerb verb,  PayloadReader request) {//use special HTTP request object with channelID and Sequence plus stream...

		populateResponseStringBuilders(request);
		
		//optional but without it we must chunk
		int length = part1.length+a.length()+
				 	 part2.length+b.length()+
					 part3.length+c.length()+
					 part4.length;
		
		int context = END_OF_RESPONSE; //if we choose we can or this with CLOSE_CONNECTION or not and leave the connection open for more content.
				
		int statusCode = 200;
        
		Optional<NetResponseWriter> writer = cc.openHTTPResponse(connectionId, sequenceCode, statusCode, context, HTTPContentTypeDefaults.JSON, length); 
				
		writer.ifPresent( (outputStream) -> {
			
			outputStream.write(part1);
			
			outputStream.writeUTF8Text(a);
			
			outputStream.write(part2);
			
			outputStream.writeUTF8Text(b);
			
			outputStream.write(part3);
			
			outputStream.writeUTF8Text(c);
			
			outputStream.write(part4);
			
			outputStream.close();
						
		});		

		return writer.isPresent(); //if false is returned then this method will be called again later with the same inputs.
	
	}
	
	
	private void populateResponseStringBuilders(DataInputBlobReader<HTTPRequestSchema> inputStream) {

		long m1 = inputStream.readPackedLong(); //TODO: confirm that the capture logic returns the values in this format.
		byte e1 = inputStream.readByte();
		//logger.info("Value a {} {}",m1,e1);	
		
		//NOTE: instead of the double as 2 values read above we could have used this method
		// double value1 = inputStream.readDecimalAsDouble();
		
		
		long m2 = inputStream.readPackedLong();
		byte e2 = inputStream.readByte();		
		//logger.info("Value b {} {}",m2,e2);

		a.setLength(0);
		Appendables.appendDecimalValue(a, m1, e1);
		
		b.setLength(0);
		Appendables.appendDecimalValue(b, m2, e2);
		
		c.setLength(0);
		Decimal.sum(m1, e1, m2, e2, (m,e)->{
			Appendables.appendDecimalValue(c, m, e);			
		});
				
		
	}	

}
