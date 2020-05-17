package dedale.behaviours.knowledge;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.graphstream.graph.Node;

import dataStructures.tuple.Couple;
import dedale.agents.ExploreSoloAgent;
import dedale.knowledge.MapRepresentation;
import dedale.knowledge.MapRepresentation.MapAttribute;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;

public class MappingBehaviour extends OneShotBehaviour {

	private static final long serialVersionUID = 8567689731496787661L;

	private boolean finished = false;

	/**
	 * Current knowledge of the agent regarding the environment
	 */
	private MapRepresentation myMap;

	/**
	 * Nodes known but not yet visited
	 */
	private List<String> openNodes;
	
	private List<String> objectiveNodes;
	/**
	 * Visited nodes
	 */
	private Set<String> closedNodes;
	
	private HashMap<String, String> agentNodes;
	
	private String wumpusPos = null;
	private String hunterPos = null;
	
	private String nextNode = null;


	public MappingBehaviour(final AbstractDedaleAgent myagent) {
		super(myagent);
		this.openNodes=new ArrayList<String>();
		this.objectiveNodes=new ArrayList<String>();
		this.closedNodes=new HashSet<String>();
		this.agentNodes= new HashMap<String,String>();
	}
	
	public void mergeMap(List<String> openNodes,Set<String> closedNodes,List<String> edges) {

		for (String n : closedNodes) {
			if(!this.closedNodes.contains(n)) {
				this.myMap.addNode(n,MapAttribute.closed);
				this.closedNodes.add(n);
			}
        }
		for (String n : openNodes) {
			if(!this.openNodes.contains(n)) {
				this.myMap.addNode(n,MapAttribute.open);
				this.openNodes.add(n);
			}
        }
		
		List<String> myEdges = myMap.getEdges();
		
		for (String e : edges) {
			if(!myEdges.contains(e)) {
				String [] s = e.split(",");
				String e0 = s[0];
				String e1 = s[1];
				boolean b0 = closedNodes.contains(e0) || openNodes.contains(e0);
				boolean b1 = closedNodes.contains(e1) || openNodes.contains(e1);
				if(b0 && b1) {
					this.myMap.addEdge(e0, e1);
				}
			}
		}
	
		this.openNodes.removeAll(closedNodes);
		
	}

	@Override
	public void action() {

		if(this.myMap==null)
			this.myMap= new MapRepresentation();
		
		//0) Retrieve the current position
		String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
		
		if(this.objectiveNodes.contains(myPosition)) this.objectiveNodes.remove(myPosition);
	
		if (myPosition!=null){
			//List of observable from the agent's current position
			List<Couple<String,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();//myPosition

			//1) remove the current node from openlist and add it to closedNodes.
			this.closedNodes.add(myPosition);
			this.openNodes.remove(myPosition);

			this.myMap.addNode(myPosition,MapAttribute.closed);

			//2) get the surrounding nodes and, if not in closedNodes, add them to open nodes.
			this.nextNode=null;
			Iterator<Couple<String, List<Couple<Observation, Integer>>>> iter=lobs.iterator();
			while(iter.hasNext()){
				String nodeId=iter.next().getLeft();
				if (!this.closedNodes.contains(nodeId)){
					if (!this.openNodes.contains(nodeId)){
						this.openNodes.add(nodeId);
						this.myMap.addNode(nodeId, MapAttribute.open);
						this.myMap.addEdge(myPosition, nodeId);	
					}else{
						//the node exist, but not necessarily the edge
						this.myMap.addEdge(myPosition, nodeId);
					}
					//if (nextNode==null) nextNode=nodeId;
				}
			}
				
			}

		}
	
	public void updateAgentPos(String agent,String pos) {
		this.agentNodes.put(agent, pos);
	}
	
	public void resetAgentPos() {
		this.agentNodes.clear();
	}
	
	private String getClosest(String position) {
		
		
		String nextOpen = "";
		Integer distanceOpen = 999;
		
		String nextObjectiv = "";
		Integer distanceObjectiv = 999;
		
		for(String n: this.openNodes) {
			List<String> path = this.myMap.getShortestPath(position, n,this.agentNodes.values());
			if(path != null && path.size()<distanceOpen && path.size()!=0) {
				nextOpen = path.get(0);
				distanceOpen = path.size();
			}
		}
		
		for(String n: this.objectiveNodes) {
			List<String> path = this.myMap.getShortestPath(position, n,this.agentNodes.values());
			if(path != null && path.size()<distanceObjectiv && path.size()!=0) {
				nextObjectiv = path.get(0);
				distanceObjectiv = path.size();
			}
		}
		
		this.resetAgentPos();
		
		return distanceOpen < distanceObjectiv ? nextOpen : nextObjectiv;
		
	}
	
	public String getNextNode(String position) {
		
		
		if(this.openNodes.isEmpty()) return "";
		
		return this.getClosest(position);
	}
	
	public String getPath(String position,String node) {
		
		//System.out.println(position +" to --> "+node);
		
		List<String> path = this.myMap.getShortestPath(position, node,this.agentNodes.values());
		
		if (path == null || path.size()==0) return "";
		
		return path.get(0);
	}

	public List<String> getOpenNodes() {
		return openNodes;
	}

	public Set<String> getClosedNodes() {
		return closedNodes;
	}
	
	public List<String> getEdges() {
		return myMap.getEdges();
	}
	
	public List<String> setupObjectives() {
		
		this.objectiveNodes = new ArrayList<String>(this.openNodes.subList(0, this.openNodes.size()/2));
		
		return new ArrayList<String>(this.openNodes.subList(this.openNodes.size()/2,this.openNodes.size()));
		
	}
	
	public void setObjectives(List<String> objectives) {
		this.objectiveNodes = objectives;
	}
	
	public void setWumpusPos(String pos) {
		this.wumpusPos = pos;
	}
	
	public void setHunterPos(String name) {
		this.hunterPos = name;
	}
	
	
	
	public String getWumpusPos() {
		return wumpusPos;
	}

	public String getHunterPos() {
		return 	this.hunterPos;
	}
	
	public String getAgentPos(String name) {
		return this.agentNodes.get(name);
	}
	
	public Collection<String> getAgentsPos(){
		
		//System.out.println(this.agentNodes);
		
		return this.agentNodes.values();
	}

	public MapRepresentation getMyMap() {
		if(this.myMap==null)
			this.myMap= new MapRepresentation();
		return myMap;
	}
	
	@Override
    public int onEnd() {
        return 0;
    }
}
