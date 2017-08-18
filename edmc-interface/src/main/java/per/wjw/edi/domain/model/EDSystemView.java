package per.wjw.edi.domain.model;

import java.util.List;

import per.wjw.edi.domain.EDSystem;

public class EDSystemView {

	private EDSystem system;
	
	public EDSystem getSystem() {
		return system;
	}

	public void setSystem(EDSystem system) {
		this.system = system;
	}

	public List<EDFactionView> getFactions() {
		return factions;
	}

	public void setFactions(List<EDFactionView> factions) {
		this.factions = factions;
	}

	public List<EDStationView> getStations() {
		return stations;
	}

	public void setStations(List<EDStationView> stations) {
		this.stations = stations;
	}

	private List<EDFactionView> factions;
	
	private List<EDStationView> stations;
}
