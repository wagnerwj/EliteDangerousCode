package per.wjw.edi.domain;

public class Edge  implements Comparable<Edge>{
    private final String id;
    private final Vertex source;
    private final Vertex destination;
    private final Double weight;

    public Edge(String id, Vertex source, Vertex destination, Double weight) {
        this.id = id;
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }

    public String getId() {
        return id;
    }
    public Vertex getDestination() {
        return destination;
    }

    public Vertex getSource() {
        return source;
    }
    public Double getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return source + " " + destination;
    }

	@Override
	public int compareTo(Edge o) {
		
		return this.weight.compareTo(o.weight);
	}


}