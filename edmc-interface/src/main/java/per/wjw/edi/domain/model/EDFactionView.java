package per.wjw.edi.domain.model;

import per.wjw.edi.domain.EDFaction;

//Used in the EDSystemView to display influences and such
public class EDFactionView implements Comparable<EDFactionView>{
	
	
	private EDFaction faction;
	
	private Double influence;

	public EDFaction getFaction() {
		return faction;
	}

	public void setFaction(EDFaction faction) {
		this.faction = faction;
	}

	public Double getInfluence() {
		return influence;
	}

	public void setInfluence(Double influence) {
		this.influence = influence;
	}

	@Override
	public int compareTo(EDFactionView o) {
		// TODO Auto-generated method stub
		return this.influence.compareTo(o.getInfluence());
	}
	
	
	

}
