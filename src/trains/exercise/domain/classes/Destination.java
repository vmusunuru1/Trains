package trains.exercise.domain.classes;

	/**
	 * Candidate weight gives the track weight
	 * @author Sergio
	 *
	 */
public class Destination{
	private Town town;
	private int weight;
	
	/**
	 * @param name
	 * @param weight
	 */
	public Destination(Town town, int weight) {
		this.town = town;
		this.weight = weight;
	}
	
	public Town getTown() {
		return town;
	}

	public int getWeight() {
		return weight;
	}
	
}
