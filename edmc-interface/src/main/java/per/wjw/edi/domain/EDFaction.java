package per.wjw.edi.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EDFaction {

	
	private Long id;
	
	private String name;
	
	@JsonProperty("updated_at")
	private Long updatedAt;
	
	@JsonProperty("government_id")
	private Long governmentId;
	
	private String government;
	
	@JsonProperty("allegiance_id")
	private Long allegianceId;
	
	private String allegiance;
	
	@JsonProperty("state_id")
	private Long stateId;
	
	private String state;
	
	@JsonProperty("home_system_id")
	private Long homeSystemId;
	
	@JsonProperty("is_player_faction")
	private Boolean isPlayerFaction;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Long updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Long getGovernmentId() {
		return governmentId;
	}

	public void setGovernmentId(Long governmentId) {
		this.governmentId = governmentId;
	}

	public String getGovernment() {
		return government;
	}

	public void setGovernment(String government) {
		this.government = government;
	}

	public Long getAllegianceId() {
		return allegianceId;
	}

	public void setAllegianceId(Long allegianceId) {
		this.allegianceId = allegianceId;
	}

	public String getAllegiance() {
		return allegiance;
	}

	public void setAllegiance(String allegiance) {
		this.allegiance = allegiance;
	}

	public Long getStateId() {
		return stateId;
	}

	public void setStateId(Long stateId) {
		this.stateId = stateId;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Long getHomeSystemId() {
		return homeSystemId;
	}

	public void setHomeSystemId(Long homeSystemId) {
		this.homeSystemId = homeSystemId;
	}

	public Boolean getIsPlayerFaction() {
		return isPlayerFaction;
	}

	public void setIsPlayerFaction(Boolean isPlayerFaction) {
		this.isPlayerFaction = isPlayerFaction;
	}
}
