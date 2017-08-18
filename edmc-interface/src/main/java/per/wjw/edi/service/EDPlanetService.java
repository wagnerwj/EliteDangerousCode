package per.wjw.edi.service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import per.wjw.edi.domain.EDFaction;
import per.wjw.edi.domain.model.EDSystemView;

public class EDPlanetService extends EliteDangerousService {
	
	//{"id":1,"name":"39 b Draconis One","updated_at":1499421548,"government_id":80,"government":"Cooperative","allegiance_id":4,"allegiance":"Independent","state_id":80,"state":"None","home_system_id":185,"is_player_faction":false}
	
	
	
	
	//So- get back the Minor Faction asked for
	public EDFaction getMinorFaction(Long factionId){
		EDFaction faction = null;
		SearchResponse response;
		QueryBuilder query = QueryBuilders.termQuery("id", factionId);
		try {
			response = (SearchResponse) transportClient.prepareSearch("factions").
					setQuery(query).
					execute().
					get();
			
			if(response.getHits().totalHits>0)
				faction = mapper.readValue(response.getHits().getHits()[0].getSourceAsString(), EDFaction.class);
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		
		return faction;
	}
	
	public EDSystemView getEDSystem(){
		return null;
	}

	
}
