package per.wjw.edi.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import per.wjw.edi.EDSystem;

public class EDDNService extends EliteDangerousService{
	
	
	
	
	public static void main(String[] args){
		String RELAY = "tcp://eddn.edcd.io:9500";
		String SCHEMA_KEY = "$schemaRef";
		ZContext ctx = new ZContext();
        ZMQ.Socket client = ctx.createSocket(ZMQ.SUB);
        client.subscribe("".getBytes());
        client.setReceiveTimeOut(30000);

        client.connect(RELAY);
       System.out.println("EDDN Relay connected");
        ZMQ.Poller poller = ctx.createPoller(2);
        poller.register(client, ZMQ.Poller.POLLIN);
        byte[] output = new byte[256 * 1024];
        while (true) {
            int poll = poller.poll(10);
            if (poll == ZMQ.Poller.POLLIN) {
                ZMQ.PollItem item = poller.getItem(poll);

                if (poller.pollin(0)) {
                    byte[] recv = client.recv(ZMQ.NOBLOCK);
                    if (recv.length > 0) {
                        // decompress
                        Inflater inflater = new Inflater();
                        inflater.setInput(recv);
                        try {
                            int outlen = inflater.inflate(output);
                            String outputString = new String(output, 0, outlen, "UTF-8");
                            // outputString contains a json message

                            if (outputString.contains(SCHEMA_KEY)) {
                            	System.out.println(outputString);
                            }

                        } catch (DataFormatException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
	}

}
