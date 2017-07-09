package per.wjw.edi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ParseEddb {

	public static void main(String[] args) {
		HttpClient client = HttpClients.createDefault();
		RestClient restClient = RestClient.builder(
		        new HttpHost("localhost", 9200, "http"),
		        new HttpHost("localhost", 9201, "http")).build();
		HttpGet request = new HttpGet("https://eddb.io/archive/v5/stations.json");
		ObjectMapper mapper = new ObjectMapper();
		try {
			HttpEntity entity = client.execute(request).getEntity();
			StringBuffer buff = new StringBuffer();
			String line = null;
			BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
			while( (line=reader.readLine())!= null){
				buff.append(line);
			}
			int rhe=3;
			EntityUtils.consume(entity);
//			HttpPut putRequest = new HttpPut("http://localhost:9200/stations");
//			entity = client.execute(putRequest).getEntity();
			List<Map<String, Object>> stationList = (List<Map<String, Object>> )mapper.readValue(buff.toString(), new TypeReference<List<Map<String,Object>>>() { });
			for(Map<String,Object> station:stationList){
				String stationData =mapper.writeValueAsString(station);
				System.out.println("Trying to store "+(String)station.get("name"));
				Response response = restClient.performRequest("POST", "/stations/record", Collections.<String, String>emptyMap(),new NStringEntity(stationData, ContentType.APPLICATION_JSON));
	            System.out.println("Response status: "+response.getStatusLine());

			}
			restClient.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
