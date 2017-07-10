package per.wjw.edi;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import per.wjw.edi.domain.EDSystem;

public class PPCreation {
	
	
	public static Double synteiniDist(Double x, Double y, Double z){
		return Math.sqrt(Math.pow((x-51.78125),2.0)+ Math.pow((y+76.40625), 2.0)+Math.pow((z-28.71875), 2.0));
	}
	
	public static Long estUpkeep(EDSystem system){
		Double dist = Math.log10(PPCreation.synteiniDist(system.getX(), system.getY(), system.getZ()));
		return Math.round(Math.floor(-135.779*Math.pow(dist, 3.0)+664.128*Math.pow(dist, 2.0)-1074.97*dist+549.68));
	}

	public static void main(String[] args) {
		List<String> testList =   new ArrayList<String>();
		testList.add("Ac Yax Baru");
		testList.add("Wolfberg");

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.ACCEPT_FLOAT_AS_INT, true);
		Map<String, EDSystem> systemList = new HashMap<>();
		
		try {
			HttpClient client = HttpClients.createDefault();
			HttpGet request = new HttpGet("https://eddb.io/archive/v5/systems_populated.json");

				HttpEntity entity = client.execute(request).getEntity();
				StringBuffer buff = new StringBuffer();
				String line = null;
				BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
				while( (line=reader.readLine())!= null){
					buff.append(line);
				}

				EntityUtils.consume(entity);

			List<Map<String, Object>> dataList = (List<Map<String, Object>> )mapper.readValue(buff.toString(), new TypeReference<List<Map<String,Object>>>() { });
			for(Map<String, Object> edSystem:dataList){
				EDSystem newSystem = new EDSystem();
				newSystem.setEddbData(edSystem);
				if(PPCreation.synteiniDist(newSystem.getX(), newSystem.getY(), newSystem.getZ())<=200 && newSystem.getEddbData().get("power_state") == null)
					systemList.put(newSystem.getName(), newSystem);
					newSystem.setUpkeep(PPCreation.estUpkeep(newSystem));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(String key:systemList.keySet()){
			EDSystem workingSystem = systemList.get(key);
			if(PPCreation.synteiniDist(workingSystem.getX(), workingSystem.getY(), workingSystem.getZ())<=150){
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
		List<EDSystem> priorityList = new ArrayList<>();
		for(String key:systemList.keySet()){
			EDSystem workingSystem = systemList.get(key);
			long controlIncome =workingSystem.getSystemCCIncome();
			List<String> sphere= workingSystem.getControlSphere();

			if(sphere!=null && sphere.size()>0)
				for(String exploitedSystem:sphere){
					controlIncome += systemList.get(exploitedSystem).getSystemCCIncome();
				}
			if(controlIncome>81){
			workingSystem.setControlSphereIncome(controlIncome);
			priorityList.add(workingSystem);
			}
		}
		Collections.sort(priorityList);
		for(EDSystem system:priorityList){
			System.out.println(system.getName()+" "+estUpkeep(system)+" "+system.getControlSphereIncome());
			
		}

	}

}
