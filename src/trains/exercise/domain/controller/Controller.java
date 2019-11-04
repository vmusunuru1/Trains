package trains.exercise.domain.controller;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import trains.exercise.domain.classes.CC;
import trains.exercise.domain.classes.Candidate;
import trains.exercise.domain.classes.DFSNumber;
import trains.exercise.domain.classes.Destination;
import trains.exercise.domain.classes.Graph;
import trains.exercise.domain.classes.InverseNumber;
import trains.exercise.domain.classes.Town;
import trains.exercise.domain.classes.Visited;
import trains.exercise.domain.exception.DestinationAlreadyExistsException;
import trains.exercise.domain.exception.InvalidRouteException;
import trains.exercise.presentation.IO;
import trains.exercise.presentation.Menu;

public class Controller {
	
	private final int INFINITY = 9999999;
	
	private Graph graph;
	private boolean wasDijkstraExecuted;
	private boolean graphLoaded;
	private Town	startDijkstra; 
	
	// Dijkstra algorithm
	private HashMap<String, Integer> minimumWeight;
	private Map<String, Town> predecessor;
	private Set<String> candidates;
	private Set<String> visited;

	// Number of different routes between two towns
	private Map<String,Boolean> visitedDFS;
	private Map<String,Integer> ndfs;
	private Map<String,Integer> ninv;
	
	private List<CC> Cc; // Connected components 
	private int num_dfs;
	private int num_inv;
	private int ncc;
	private int nroutes;
	
	/**
	 * Empty constructor
	 * @param graph
	 */
	public Controller() {
		this.graph = new Graph();
		graphLoaded = false;
		startDijkstra = null;
		wasDijkstraExecuted = false;
		minimumWeight = new HashMap<String, Integer>();
		predecessor = new HashMap<String, Town>();
		candidates = new HashSet<String>();
		visited = new HashSet<String>(); 
	}
	
	/**
	 * Function that capture errors and calls the menu
	 */
	public void start() {
		Menu.printMenu();
		String option = "0";
		
		while( !option.equals("9") ) {
			try {
				option = Menu.start();
				
			} catch (FileNotFoundException e) {
				System.err.println( "Error reading the data: File not found");
				Menu.printMenu();
			} catch( IllegalArgumentException e ) {
				System.err.println( "Error reading the data: " + e.getMessage());
				Menu.printMenu();				
			} catch (DestinationAlreadyExistsException e) {
				System.err.println( "Error reading the data: " + e.getMessage());
				Menu.printMenu();
			} catch (InvalidRouteException e) {
				System.err.println( "Error reading the data: " + e.getMessage());
				Menu.printMenu();
			} catch (CloneNotSupportedException e) {
				System.err.println( "Error computing shortest path ");
				Menu.printMenu();
			}
		}
	}
	
	/**
	 * Validates the input and compute the distance between two towns along a certain path 
	 * @param in path to compute distance
	 * @return Calculated distance or -1 if the path doesn't exist
	 * @throws IllegalArgumentException wrong city name
	 * @throws DestinationAlreadyExistsException Town already exist
	 * @throws InvalidRouteException Invalid route to compute
	 */
	public String computeDistanceAndValidate(String in) throws
			IllegalArgumentException, DestinationAlreadyExistsException, InvalidRouteException  {
		String[] townsList = IO.readRoute(in);
		int computedDistance = computeDistance(townsList);
		String result = "";
		if ( computedDistance < 0 ) {
			result = "NO SUCH ROUTE";	
		}else {
			result = String.valueOf(computedDistance);
		}
		return result;
	}
	
	/**
	 * Compute the distance between to towns along a certain path.
	 * @param townsList
	 * @return distance between a certain path
	 * @throws IllegalArgumentException
	 * @throws DestinationAlreadyExistsException
	 * @throws InvalidRouteException
	 */
	private int computeDistance(String[] townsList) throws
	IllegalArgumentException, DestinationAlreadyExistsException, InvalidRouteException  {
		
		int distance = 0;
		for( int i = 0; i < townsList.length-1; i++ ) {
			if( containsTown(townsList[i]) && 
				containsDestinationTown(townsList[i], townsList[i+1])) {
				distance += graph.getGraph().get(townsList[i]).get(townsList[i+1]);
				
			}else {
				distance = -1;
				break;
			}
		}
		return distance;	
	}
	
	
	/**
	 * Check if town exists in the graph
	 * @param town
	 * @return true if the graph contains the town
	 */
	private boolean containsTown(String town){
		return graph.getGraph().
				containsKey(town);
	}
	
	/**
	 * Gets town routes from the graph
	 * @param town
	 * @return A map with all the destinations of the given town
	 */
	private Map<String,Integer> getTown(String town) {
		return graph.getGraph().
				get(town);
	}
	
	/**
	 * Check if a destination town exists in the graph
	 * @param town
	 * @return true if the destination exist for a given town
	 */
	private boolean containsDestinationTown(String townStart, String townEnd){
		return getTown(townStart).containsKey(townEnd);
	}
	
	/**
	 * Validates that the input is with the format Town Town and compute the number of different paths
	 * @param in
	 * @return number of different paths between two towns
	 * @throws IllegalArgumentException
	 * @throws InvalidRouteException
	 */
	public int numberDifferentRoutesAndValidate(String in) throws
		IllegalArgumentException, InvalidRouteException {
		String[] towns = IO.validateTwoTownRoute(in);
		Town start = new Town(towns[0]);
		Town end =  new Town(towns[1]);
		
		return numberDifferentRoutes(start, end);
	}
	
	/**
	 * Compute the number of different paths between two towns
	 * @param start
	 * @param end
	 * @return number of different paths between two towns
	 */
	public int numberDifferentRoutes(Town start, Town end) {
		intializeDifferentRoutesStructures(start);
		int i = 0;
		Visited visited;
		Iterator<Visited> it = visitedDFS. .iterator();
		while(it.hasNext()) {
			visited = it.next();
			if( !visited.isVisited() ) {
				numberDifferentRoutesRec(visited , 0, end);		
			}
		}
		
		return nroutes;
	}
	
	/**
	 * Recursive method of numberDifferentRoutes 
	 * @param i
	 * @return
	 */
	private void numberDifferentRoutesRec(Visited visited, int father, Town end) {
		// PRE-Visit(v)
		num_dfs += 1;
		System.out.println("V: "+act.getTown().getName());
		// If my neighbor is the end, we count the path
		if( !isNeighborEqualsEnd(visited.getTown(), end) ) {
			visited.setVisited(true);	
			ndfs.get(visited.getTown()).setValue(num_dfs);
		}else {
			nroutes++;
		}			
		
		for( int neighbor=0; neighbor < getNeighborTowns(act.getTown()).size(); neighbor++ ) {
				
				if ( !visitedDFS.get(neighbor).isVisited() ) {
					// PRE-Visit-edge(v,w)
					numberDifferentRoutesRec(neighbor, position, end);
					// POST-Visit-edge(v,w)
				}else {
					if( neighbor != position ) System.out.println("There is a cicle");
				}
		}
		// POST-VIST(v)
		num_inv++;
		ninv.get(position).setValue(num_inv);
	}
	
	/**
	 * Gets the neighbor towns of a given town
	 * @param town
	 * @return list of neighbor towns
	 */
	private List<Town> getNeighborTowns( Town town ){
		return graph.getGraphDFS().get(town.getName());
	}
	
	private boolean isNeighborEqualsEnd( Town n, Town end) {
		return n.getName().equals(end.getName());
	}
	
	/**
	 * Initialize all necessary DFS structures
	 */
	private void intializeDifferentRoutesStructures(Town start) {
		visitedDFS = new HashMap<Visited>();
		ndfs = new HashMap<DFSNumber>();
		ninv = new HashMap<InverseNumber>();
		Cc = new ArrayList<CC>();
		num_dfs = 0;
		num_inv = 0;
		ncc = 0;
		nroutes = 0;
		
		visitedDFS.add(new Visited(start, false));
		ndfs.add(new DFSNumber(start, 0));
		ninv.add(new InverseNumber(start, 0));
		Cc.add(new CC(start, 0));
		
		for( String k: graph.getGraph().keySet() ) {
			if( !k.equals(start.getName()) ) {
				Town town = new Town(k);
				visitedDFS.add(new Visited(town, false));
				ndfs.add(new DFSNumber(town, 0));
				ninv.add(new InverseNumber(town, 0));
				Cc.add(new CC(town, 0));
			}
		}
	}
	
	/**
	 * Validates the input, and verify if dijkstra algorithm needs to be run
	 * @param in - input to validate
	 * @return A path with minimum distance between two towns
	 * @throws IllegalArgumentException
	 * @throws DestinationAlreadyExistsException
	 * @throws InvalidRouteException
	 * @throws CloneNotSupportedException
	 */
	public List<Town> computeShortestRouteAndValidate(String in) throws
		IllegalArgumentException, DestinationAlreadyExistsException, InvalidRouteException, CloneNotSupportedException  {
		
		String[] towns = IO.validateTwoTownRoute(in);
		Town start = new Town(towns[0]);
		Town end =  new Town(towns[1]);
		
		// If the algorithm was not executed, or was executed and the start town is different than the last execution
		if( !wasDijkstraExecuted ||
			(wasDijkstraExecuted && !isSameStartCity(start)) ) {			
			
			initializeStructuresDijkstra();
			computeShortestRoute(start);
			wasDijkstraExecuted = true;
			startDijkstra = start;
		}
		return shortestRoute(start, end);
	}
	
	/**
	 * Compute a path with minimum distance between two towns, the result is the predecessors list
	 * @param start
	 * @throws IllegalArgumentException
	 * @throws DestinationAlreadyExistsException
	 * @throws InvalidRouteException
	 * @throws CloneNotSupportedException
	 */
	public void computeShortestRoute(Town start) throws
		IllegalArgumentException, DestinationAlreadyExistsException, InvalidRouteException, CloneNotSupportedException  {
		
		// Start point of the algorithm
		minimumWeight.put(start.getName(), 0);
		
		// For each candidate we find the shortest path to the start point
		while( candidates.size() != 0 ) {
			
			Candidate cand = getMinimumCand( );
			// we remove the candidate from the unvisited list
			candidates.remove(cand.getTown().getName());
			
			int size = graph.getGraphP().get(cand.getTown().getName()).size();
			
			for( int i=0; i < size; i++) {
				// checking all candidate neighbors weights
				Destination neighbor = graph.getGraphP().get(cand.getTown().getName()).get(i);
				updateMinimumWeight(neighbor, cand);
			}
			// We add the candidate to the visited list
			visited.add(cand.getTown().getName());
		}
	}
	
	
	/**
	 * Updates the minimum weight table with the new weight computation
	 * @param neighbor
	 * @param cand
	 */
	private void updateMinimumWeight( Destination neighbor, Candidate cand ) {
		// Candidate is always the minimum path to this point 
		int w = minimumWeight.get(cand.getTown().getName()) + neighbor.getWeight();
			
		if( w < minimumWeight.get(neighbor.getTown().getName())) {
			minimumWeight.put(neighbor.getTown().getName(), w);
			// Add the town to the predecessors list
			predecessor.put(neighbor.getTown().getName(), cand.getTown());
		}
	}
	
	/**
	 * Check if the start city is the same that the last dijkstra run, to save calculate it again
	 * @param start
	 * @return true if is the same start city
	 */
	private boolean isSameStartCity(Town start) {
		return startDijkstra.getName().equals(start.getName());
	}
	
	/**
	 * Initialize Dijkstra structure 
	 */
	private void initializeStructuresDijkstra(  ) {
		minimumWeight = new HashMap<String, Integer>();
		predecessor = new HashMap<String, Town>();
		candidates = new HashSet<String>();
		visited = new HashSet<String>(); 
		
		candidates.addAll(graph.getGraphP().keySet());
		
		// Initialize all distances to infinity
		for( String k: candidates ) {
			minimumWeight.put(k, INFINITY);
		}
	}
	
	/**
	 * Gets the candidate with minimum weight
	 * @return candidate with minimum weight
	 */
	private Candidate getMinimumCand( ) {
		int minimum = INFINITY+1;
		int weight = INFINITY;
		String result = "";
		
		for( String cand: candidates ) {
			weight = minimumWeight.get(cand);
			if ( weight < minimum ) {
				result = cand;
				minimum = weight;
			}
		}
		return new Candidate(new Town(result), minimum);
	}
	
	/**
	 * Creates the route from one town to other, with the predecessors list
	 * @param start point
	 * @param end point
	 * @return Ordered list with the shortest route between two towns
	 */
	private List<Town> shortestRoute(Town start, Town end ){
		List<Town> result = new ArrayList<Town>();
		Town predecessorTown = predecessor.get(end.getName());
		result.add(end);
		
		if(predecessorTown != null) {			
			result.add(predecessorTown);
			while( !(predecessorTown.getName()).equals(start.getName()) ) {
				
				predecessorTown = predecessor.get(predecessorTown.getName());
				result.add(predecessorTown);
			}
		}
		return result;
	}
	
	/**
	 * Call graph method to generate a graph
	 * @param in - list of towns
	 * @throws IllegalArgumentException
	 * @throws DestinationAlreadyExistsException
	 */
	public void generateGraph(String[] in) throws
		IllegalArgumentException, DestinationAlreadyExistsException {
		graph.generateGraph(in);
	}
	
	/**
	 * Print a graph
	 */
	public void printGraph() {
		IO.printGraph(graph);
	}

	/* Getters and Setters */
	
	public boolean wasDijkstraExecuted() {
		return wasDijkstraExecuted;
	}

	public void setWasDijkstraExecuted(boolean wasDijkstraExecuted) {
		this.wasDijkstraExecuted = wasDijkstraExecuted;
	}

	public boolean isGraphLoaded() {
		return graphLoaded;
	}

	public void setGraphLoaded(boolean graphLoaded) {
		this.graphLoaded = graphLoaded;
	}

	public Town getStartDijkstra() {
		return startDijkstra;
	}

	public void setStartDijkstra(Town startDijkstra) {
		this.startDijkstra = startDijkstra;
	}
}
