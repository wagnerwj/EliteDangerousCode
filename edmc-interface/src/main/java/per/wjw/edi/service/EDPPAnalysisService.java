package per.wjw.edi.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.lucene.queries.TermsQuery;
import org.apache.lucene.search.Query;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.lucene.search.Queries;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.ToXContent.Params;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryShardContext;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.SearchHit;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;

import per.wjw.edi.PPCreation;
import per.wjw.edi.domain.EDSystem;

public class EDPPAnalysisService extends EliteDangerousService {
	
	public static Double synteiniDist(EDSystem system){
		return Math.sqrt(Math.pow((system.getX()-51.78125),2.0)+ Math.pow((system.getY()+76.40625), 2.0)+Math.pow((system.getZ()-28.71875), 2.0));
	}
	
	public static Long estUpkeep(EDSystem system){
		Double dist = Math.log10(PPCreation.synteiniDist(system.getX(), system.getY(), system.getZ()));
		return Math.round(Math.floor(-135.779*Math.pow(dist, 3.0)+664.128*Math.pow(dist, 2.0)-1074.97*dist+549.68));
	}
	
	
	public void createPPAnalysis(){
		Map<String,EDSystem> systemList = new HashMap<>();
		Map<String,EDSystem> controlSystems = new HashMap<>();
		Map<String, EDSystem> explotedSystems = new HashMap<>();
		SearchResponse response;
		Set<String> querySet = new HashSet<>();
		querySet.add("Control");
		querySet.add("Exploited");
		try {
			QueryBuilder query = QueryBuilders.termsQuery("power_state", "control","exploited");
			response = (SearchResponse) transportClient.prepareSearch("populated_systems").
					setQuery(query).
					setScroll(new TimeValue(60000)).
					setSize(100).
					execute().
					get();
		
		
		
		do{
			System.out.println(response.getHits().totalHits+" Total Hits");
		for(SearchHit hit: response.getHits().getHits()){
			try {

			
				Map<String, Object> edSystem;
				
				edSystem = (Map<String, Object> )mapper.readValue(hit.getSourceAsString(), new TypeReference<Map<String,Object>>() { });
			
			EDSystem newSystem = new EDSystem();
			newSystem.setEddbData(edSystem);
			
			
			systemList.put(newSystem.getName(), newSystem);
			
			if("Control".equals((String)newSystem.getEddbDataEntry("power_state"))){
				controlSystems.put(newSystem.getName(), newSystem);
			}else{
				explotedSystems.put(newSystem.getName(), newSystem);
			}
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		response = transportClient.prepareSearchScroll(response.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
		}while(response.getHits().getHits().length>0);
		} catch (InterruptedException | ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		for(String key:controlSystems.keySet()){
			EDSystem workingSystem = systemList.get(key);
		
			System.out.println("Working "+key);
			workingSystem.addIncomeToSphere(workingSystem.getSystemCCIncome());
			for(String innnerKey:explotedSystems.keySet()){
				if(workingSystem.getAdjacentSystem(innnerKey)==null){
				EDSystem nextNeighbor = systemList.get(innnerKey);
				Double dist = workingSystem.adjacentDistance(nextNeighbor);

				if(dist < 15.0){
				
					workingSystem.addExploit(innnerKey);
					nextNeighbor.setControllingSystem(key);
					workingSystem.addIncomeToSphere(nextNeighbor.getSystemCCIncome());
					
				}
				}
			}
			
	
		}
		String indexName = "pp_analysis";
		try {
			DeleteIndexResponse deleteResponse = transportClient.admin().indices().delete(new DeleteIndexRequest(indexName)).actionGet();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		transportClient.admin().indices().prepareCreate(indexName).get();
		//List<Map<String, Object>> dataList = (List<Map<String, Object>> )mapper.readValue(buff.toString(), new TypeReference<List<Map<String,Object>>>() { });
	    BulkRequestBuilder bulkRequestBuilder = transportClient.prepareBulk();
	    for (EDSystem system:systemList.values()) {

	        try {
				bulkRequestBuilder.add(transportClient.prepareIndex(indexName, "record").setSource(mapper.writeValueAsString(system)));
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    bulkRequestBuilder.execute().actionGet();
	}
	
	
	
	public void calculateTorvalExpansionCandidates(){
		Map<String,EDSystem> systemList = new HashMap<>();
		SearchResponse response;
		try {
			response = (SearchResponse) transportClient.prepareSearch("populated_systems").
					setQuery(QueryBuilders.boolQuery().
					mustNot(QueryBuilders.existsQuery("power_state"))).
					setScroll(new TimeValue(60000)).
					setSize(100).
					execute().
					get();
		
		
		
		do{
		for(SearchHit hit: response.getHits().getHits()){
			try {
			Map<String, Object> edSystem;
			
				edSystem = (Map<String, Object> )mapper.readValue(hit.getSourceAsString(), new TypeReference<Map<String,Object>>() { });
			
			EDSystem newSystem = new EDSystem();
			newSystem.setEddbData(edSystem);
			if(EDPPAnalysisService.synteiniDist(newSystem)<=200 ){
				systemList.put(newSystem.getName(), newSystem);
				newSystem.setUpkeep(PPCreation.estUpkeep(newSystem));
			}
			
			
			
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		response = transportClient.prepareSearchScroll(response.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
		}while(response.getHits().getHits().length>0);
		} catch (InterruptedException | ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	for(String key:systemList.keySet()){
		EDSystem workingSystem = systemList.get(key);
		if(EDPPAnalysisService.synteiniDist(workingSystem)<=150){
		System.out.println("Working "+key);
		workingSystem.addIncomeToSphere(workingSystem.getSystemCCIncome());
		for(String innnerKey:systemList.keySet()){
			if(!key.equalsIgnoreCase(innnerKey) &&workingSystem.getAdjacentSystem(innnerKey)==null){
			EDSystem nextNeighbor = systemList.get(innnerKey);
			Double dist = workingSystem.adjacentDistance(nextNeighbor);

			if(dist < 15.0){
				workingSystem.addExploit(innnerKey);
				
				nextNeighbor.addExploit(key);
				
			}
			}
		}
	}
	}

	for(String key:systemList.keySet()){
		EDSystem workingSystem = systemList.get(key);
		long controlIncome =workingSystem.getSystemCCIncome();
		List<String> sphere= workingSystem.getControlSphere();

		if(sphere!=null && sphere.size()>0)
			for(String exploitedSystem:sphere){
				controlIncome += systemList.get(exploitedSystem).getSystemCCIncome();
			}
		
		workingSystem.setControlSphereIncome(controlIncome);

	}
	String indexName = "torval_analysis";
	try {
		DeleteIndexResponse deleteResponse = transportClient.admin().indices().delete(new DeleteIndexRequest(indexName)).actionGet();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 
	transportClient.admin().indices().prepareCreate(indexName).get();
	//List<Map<String, Object>> dataList = (List<Map<String, Object>> )mapper.readValue(buff.toString(), new TypeReference<List<Map<String,Object>>>() { });
    BulkRequestBuilder bulkRequestBuilder = transportClient.prepareBulk();
    for (EDSystem system:systemList.values()) {

        try {
			bulkRequestBuilder.add(transportClient.prepareIndex(indexName, "record").setSource(mapper.writeValueAsString(system)));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    bulkRequestBuilder.execute().actionGet();
	}

	public static void main(String[] args){
		EDPPAnalysisService service = new EDPPAnalysisService();
		service.createPPAnalysis();
	}
}
