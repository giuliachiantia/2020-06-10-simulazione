package it.polito.tdp.imdb.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.imdb.db.ImdbDAO;

public class Model {
	//ha sim senza event
	ImdbDAO dao;
	Map<Integer, Actor> actorsIdMap;
	Graph<Actor, DefaultWeightedEdge> graph;
	Simulator sim;
	
	public Model() {
		dao = new ImdbDAO();
		actorsIdMap = new HashMap<>();
		dao.listAllActors(actorsIdMap);
	}
	
	public List<String> getGenres() {
		return dao.getGenres();
	}
	
	public void createGraph(String genre) {
		graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		Graphs.addAllVertices(graph, dao.getVerices(genre, actorsIdMap));
		
		
		for(Adjacency adj : dao.getAdjacencies(genre, actorsIdMap)) {
			if(graph.getEdge(adj.getA1(), adj.getA2()) == null) {
				Graphs.addEdgeWithVertices(graph, adj.getA1(), adj.getA2(), adj.getWeight());
			}
		}
	}
	
	public int vertexNumber() {
		return graph.vertexSet().size();
	}
	
	public int edgeNumber() {
		return graph.edgeSet().size();
	}
	
	public List<Actor> getActors() {
		List<Actor> actors = new ArrayList<>(graph.vertexSet());
		Collections.sort(actors, new Comparator<Actor>() {

			@Override
			public int compare(Actor o1, Actor o2) {
				return o1.lastName.compareTo(o2.lastName);
			}
			
		});
		return actors;
	}
	
	public List<Actor> getConnectedActors(Actor a){
		ConnectivityInspector<Actor, DefaultWeightedEdge> ci = new ConnectivityInspector<Actor, DefaultWeightedEdge>(graph);
		List<Actor> actors = new ArrayList<>(ci.connectedSetOf(a));
		actors.remove(a);
		Collections.sort(actors, new Comparator<Actor>() {

			@Override
			public int compare(Actor o1, Actor o2) {
				return o1.lastName.compareTo(o2.lastName);
			}
			
		});
		return actors;
	}
	
	public void simulate(int n) {
		sim = new Simulator(n, graph);
		sim.init();
		sim.run();
	}
	
	public Collection<Actor> getInterviewedActors(){
		if(sim == null){
			return null;
		}
		return sim.getInterviewedActors();
	}
	
	public Integer getPauses(){
		if(sim == null){
			return null;
		}
		return sim.getPauses();
	}

}
