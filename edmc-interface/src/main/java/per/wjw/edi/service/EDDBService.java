package per.wjw.edi.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import per.wjw.edi.domain.EDSystem;

public class EDDBService extends EliteDangerousService {

	
	
	private void getAndStoreObjectsInIndex(String uri, String indexName) throws JsonParseException, JsonMappingException, IOException{
		Map<String, EDSystem> systemList = new HashMap<>();
		
	

			HttpGet request = new HttpGet(uri);
			request.addHeader("Accept-Encoding", "gzip, deflate, sdch");
			
				HttpEntity entity = restClient.execute(request).getEntity();
				StringBuffer buff = new StringBuffer();
				String line = null;
				BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
				while( (line=reader.readLine())!= null){
					buff.append(line);
				}

				EntityUtils.consume(entity);
			
		try {
			DeleteIndexResponse deleteResponse = transportClient.admin().indices().delete(new DeleteIndexRequest(indexName)).actionGet();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		transportClient.admin().indices().prepareCreate(indexName).get();
		
		List<Map<String, Object>> dataList = (List<Map<String, Object>> )mapper.readValue(buff.toString(), new TypeReference<List<Map<String,Object>>>() { });
	    BulkRequestBuilder bulkRequestBuilder = transportClient.prepareBulk();
	    for (Map<String,Object> data : dataList) {

	        bulkRequestBuilder.add(transportClient.prepareIndex(indexName, "record").setSource(data));
	    }
	    bulkRequestBuilder.execute().actionGet();
	}
	
	
	
public void updateSystemData(){
		
	try {
		getAndStoreObjectsInIndex("https://eddb.io/archive/v5/systems_populated.json","populated_systems");
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	
	
	}

public void updateFactionData(){
	try {
		getAndStoreObjectsInIndex("https://eddb.io/archive/v5/factions.json","factions");
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

public void updateStationData(){
	try {
		getAndStoreObjectsInIndex("https://eddb.io/archive/v5/stations.json","stations");
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

public static void main(String[] args){
	EDDBService eddbService = new EDDBService();
	eddbService.updateSystemData();
	eddbService.updateFactionData();
	eddbService.updateStationData();
}

}
