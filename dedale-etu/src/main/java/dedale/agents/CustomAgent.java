package dedale.agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dataStructures.tuple.Couple;
import dedale.behaviours.communication.ConversationCalling;
import dedale.behaviours.communication.ExchangeMap;
import dedale.behaviours.communication.position.BroadcastPosition;
import dedale.behaviours.exploration.CustomExplorationBehaviour;
import dedale.behaviours.exploration.ExploSoloBehaviour;
import dedale.behaviours.knowledge.MappingBehaviour;
import dedale.knowledge.MapRepresentation;
import dedale.knowledge.YellowPage;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;
import jade.core.behaviours.Behaviour;

public abstract class CustomAgent extends AbstractDedaleAgent {

	private static final long serialVersionUID = -6431752665590433727L;
	protected MappingBehaviour mapping;
	public MappingBehaviour getMapping() {
		return mapping;
	}

	protected CustomExplorationBehaviour explo;
	protected YellowPage yellowpage;
	protected List<Behaviour> lb=new ArrayList<Behaviour>();
	
	protected HashMap<String, Integer> conversations = new HashMap<String, Integer>();

	protected final int COOLDOWN =-50;
	
	protected void setup(){

		super.setup();
		
		this.mapping = new MappingBehaviour(this);
		this.yellowpage = YellowPage.getinstance();
		this.yellowpage.register(this);
		
		System.out.println("custom");

	}


	public CustomExplorationBehaviour getExplo() {
		return explo;
	}

	public YellowPage getYellowpage() {
		return yellowpage;
	}


	public int getConversationID(String agent) {
		if(!this.conversations.containsKey(agent)) this.conversations.put(agent, 0);
		if (this.conversations.get(agent)<0) this.conversations.put(agent, this.conversations.get(agent)+1);
		return this.conversations.get(agent);
	}
	
	public int newConversation(String agent) {
		this.conversations.put(agent,(int) (Math.random() * (99999)));
		
		return this.conversations.get(agent);
	}
	
	public int endConversation(String agent) {
		this.conversations.put(agent,COOLDOWN);
		
		return this.conversations.get(agent);
	}
	
	public List<String> getActiveConversations() {
		
		List<String> activeConversations = new ArrayList<String>();
		
		for(String a:this.conversations.keySet()) {
			if (this.conversations.get(a)>0) activeConversations.add(a);
		}
		
		return activeConversations;
	}
	
	public List<String> getStenchs() {
		
		List<Couple<String,List<Couple<Observation,Integer>>>> lobs=this.observe();//myPosition
		lobs.remove(0);

		List<String> stenchs = new ArrayList<String>();
		for(Couple<String,List<Couple<Observation,Integer>>> po:lobs){
			for(Couple<Observation,Integer> o:po.getRight()) {
				if(o.getLeft().equals(Observation.STENCH)) {
					stenchs.add(po.getLeft());
					break;
				}
			}
		}
		
		return stenchs;
		
	}

}
