package per.wjw.edi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.RestClient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import per.wjw.edi.domain.EDSystem;

public class ShortestPath {
	List<EDSystem> systems;
	
	Map<String, Map<String, Double>> distanceList;
	String start="Yutumukuni";
	String end = "Synteini";
	Set<String> visitedList;
	private Double longestDistance = Double.MAX_VALUE;
	String visitList = "";
	private Double workingDistance = 0.0;
	private String workingList = "";
	
	public void populateData() {
		systems = new ArrayList<>();
		visitedList = new HashSet<>();
		HttpClient client = HttpClients.createDefault();
		ObjectMapper mapper = new ObjectMapper();
		String queryString = "{ \"size\": 100, \"query\": { \"query_string\":{ \"query\": \"power: Zemina Torval AND power_state: Control\"} } }";
		HttpPost getURI = new HttpPost("http://localhost:9200/populated_systems/_search");
		getURI.setEntity(new ByteArrayEntity(queryString.getBytes()));
		
		try {
			
			HttpResponse response = client.execute(getURI);
			HttpEntity entity = response.getEntity();
			
			StringBuffer buff = new StringBuffer();
			String line = null;
			BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
			while( (line=reader.readLine())!= null){
				buff.append(line);
			}
			EntityUtils.consume(entity);
			Map<String, Object> results = (Map<String, Object>)mapper.readValue(buff.toString(), new TypeReference<Map<String,Object>>() {
			});
			Map<String, Object> hits = (Map<String, Object>)results.get("hits");
			List<Map<String, Object>> hitsList =(List<Map<String,Object>>)hits.get("hits");
			for(Map<String, Object> hit:hitsList) {
				Map<String, Object> source = (Map<String, Object>)hit.get("_source");
				EDSystem newSystem = new EDSystem();
				newSystem.setEddbData(source);
				systems.add(newSystem);
			}
			distanceList = new HashMap<>();
			for(EDSystem startSystem: systems) {
				Map<String, Double> adjList = distanceList.get(startSystem.getName());
				if(adjList == null) {
					adjList = new HashMap<>();
					distanceList.put(startSystem.getName(), adjList);
					for(EDSystem targetSystem: systems) {
						if(!startSystem.getName().equals(targetSystem.getName())) {
							adjList.put(targetSystem.getName(), startSystem.adjacentDistance(targetSystem));
						}
					}
				}
			}
	

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void visit(String nextSystem) {
		if(end.equals(nextSystem)) {
			workingList+=nextSystem;
			if(workingDistance <=longestDistance) {
				longestDistance = workingDistance;
				visitList = new String(workingList+", "+end);
			}
			return;
		}
		System.out.println("Picked "+nextSystem);
		Map<String, Double> nextSystemList = distanceList.get(nextSystem);
		visitedList.add(nextSystem);
		workingList+=nextSystem+", ";
		if(visitedList.size() == distanceList.keySet().size()-1) {
			workingDistance += nextSystemList.get(end);
			visit(end);
			workingDistance -= nextSystemList.get(end);
		}else {
		for(String systemName: nextSystemList.keySet()) {
			if(!(start.equals(systemName)|| end.equals(systemName) || visitedList.contains(systemName))) {
				workingDistance += nextSystemList.get(systemName);
				visit(systemName);
				workingDistance -= nextSystemList.get(systemName);
			}
		}
		}
		visitedList.remove(nextSystem);
		workingList = workingList.substring(0, workingList.lastIndexOf(","));
	}
	
	public void printResults() {
		System.out.println("Best Path: "+visitList);
		System.out.println("Distance: "+longestDistance);
	}
	public static void main(String[] args) {
		ShortestPath path = new ShortestPath();
		path.populateData();
		path.visit(path.start);
		path.printResults();
	}

}
