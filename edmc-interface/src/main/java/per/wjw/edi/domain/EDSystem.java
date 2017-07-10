package per.wjw.edi.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EDSystem implements Comparable<EDSystem>{
  private String name;
  private Double[] coords;
  
  private List<String> controlSphere;
  private Map<String, Double> adjacencyList;
  
  private Map<String, Object> eddbData;
  
  private String controllingSystem;
  public String getControllingSystem() {
	return controllingSystem;
}

public void setControllingSystem(String controllingSystem) {
	this.controllingSystem = controllingSystem;
}

private long controlSphereIncome=0l;
  
  private long systemCCIncome;
  
  private long upkeep;

public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public Map<String, Double> getAdjacencyList() {
	return adjacencyList;
}

public void setAdjacencyList(Map<String, Double> adjacencyList) {
	this.adjacencyList = adjacencyList;
}

public void addAdjacentSystem(String name, Double distance){
	if(adjacencyList == null)
		adjacencyList = new HashMap<>();
	
	adjacencyList.put(name, distance);
}

public Double getAdjacentSystem(String name){
	if(adjacencyList == null)
		adjacencyList = new HashMap<>();
	
	return adjacencyList.get(name);
}

public void setCoords(Double x, Double y, Double z){
	coords= new Double[]{x,y,z};
}

public Double getX(){
	if(coords != null && coords.length>0)
		return coords[0];
	return null;
}
public Double getY(){
	if(coords != null && coords.length>1)
		return coords[1];
	return null;
}
public Double getZ(){
	if(coords != null && coords.length>2)
		return coords[2];
	return null;
}

public Double adjacentDistance(EDSystem neighbor){
	return Math.sqrt(Math.pow((this.getX()-neighbor.getX()),2.0)+ Math.pow((this.getY()-neighbor.getY()), 2.0)+Math.pow((this.getZ()-neighbor.getZ()), 2.0));
}

public Map<String, Object> getEddbData() {
	return eddbData;
}

public void setEddbData(Map<String, Object> eddbData) {
	this.eddbData = eddbData;
	this.setName((String)eddbData.get("name"));
	this.setCoords(((Number)eddbData.get("x")).doubleValue(), ((Number)eddbData.get("y")).doubleValue(), ((Number)eddbData.get("z")).doubleValue());
	try {
		this.systemCCIncome = (long) Math.max(Math.round(Math.log10( ((Number)eddbData.get("population")).longValue()*10l)),0l);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		this.systemCCIncome=0l;
	}
}

public Object getEddbDataEntry(String key){
	if(eddbData !=null)
	return eddbData.get(key);
	return null;
}

public void addExploit(String system){
	if(controlSphere==null){
		controlSphere=new ArrayList<>();
	}
	if(!controlSphere.contains(system))
		controlSphere.add(system);
}

public List<String> getControlSphere(){
	return controlSphere;
}

public Long getSystemCCIncome(){
	return this.systemCCIncome;
}

public long getControlSphereIncome() {
	return controlSphereIncome;
}

public void setControlSphereIncome(long calcSystemIncome) {
	this.controlSphereIncome = calcSystemIncome;
}

public void addIncomeToSphere(long income){
	this.controlSphereIncome+= income;
}

public long getUpkeep() {
	return upkeep;
}

public void setUpkeep(long upkeep) {
	this.upkeep = upkeep;
}

@Override
public int compareTo(EDSystem o) {
	if(controlSphereIncome> o.controlSphereIncome)
		return 1;
	if(controlSphereIncome< o.controlSphereIncome)
		return -1;
	return 0;
}
}
