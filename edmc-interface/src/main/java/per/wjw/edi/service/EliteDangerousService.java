package per.wjw.edi.service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class EliteDangerousService {

	protected ObjectMapper mapper;
	protected TransportClient transportClient;
	protected HttpClient restClient;
	public EliteDangerousService() {
		mapper= new ObjectMapper();
		restClient = HttpClientBuilder.create().addInterceptorFirst(new HttpResponseInterceptor() {
	           
         public void process(
                 final HttpResponse response, 
                 final HttpContext context) throws HttpException, IOException {
             HttpEntity entity = response.getEntity();
             Header ceheader = entity.getContentEncoding();
             if (ceheader != null) {
                 HeaderElement[] codecs = ceheader.getElements();
                 for (int i = 0; i < codecs.length; i++) {
                     if (codecs[i].getName().equalsIgnoreCase("gzip")) {
                         response.setEntity(
                                 new GzipDecompressingEntity(response.getEntity())); 
                         return;
                     }
                 }
             }
         }
         
     }).build();

		try {
			transportClient = new PreBuiltTransportClient(Settings.EMPTY)
			        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

}
