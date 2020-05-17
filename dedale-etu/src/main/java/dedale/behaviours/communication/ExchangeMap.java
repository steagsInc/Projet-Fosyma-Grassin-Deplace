package dedale.behaviours.communication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import dedale.agents.CustomAgent;
import dedale.agents.ExploreSoloAgent;
import dedale.behaviours.exploration.ExploDuoBehaviour;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class ExchangeMap extends OneShotBehaviour{

	private static final long serialVersionUID = -2058134622078521998L;

	private CustomAgent agent;
	
	private int timeout = 10;
	
	public ExchangeMap (CustomAgent myagent) {
		super(myagent);
		this.agent = myagent;
		
	}
	
	public boolean answered = false;
    public boolean getAnswer = false;

	protected void sendMap() {
		
		List<String> activeConversations = this.agent.getActiveConversations();
		
		if (!activeConversations.isEmpty()) {
			
			ACLMessage msg=new ACLMessage(ACLMessage.INFORM);
			msg.setSender(this.myAgent.getAID());
			msg.setProtocol("map");
			
			HashMap<String,Object> truc = new HashMap();
			
			truc.put("open", agent.getMapping().getOpenNodes());
			truc.put("closed", agent.getMapping().getClosedNodes());
			truc.put("edges", agent.getMapping().getEdges());
			if(agent.getExplo() instanceof ExploDuoBehaviour) truc.put("objectives", agent.getMapping().setupObjectives());
				
			//System.out.println("Agent "+this.myAgent.getLocalName()+ " is trying to send a map");
			
			try {
				msg.setContentObject(truc);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			for(String a:activeConversations) {
				msg.addReceiver(new AID(a,AID.ISLOCALNAME));
			}

			this.agent.sendMessage(msg);
			
		}
	}
	
	protected void getMap() {
		//1) receive the message
		final MessageTemplate msgTemplate =MessageTemplate.and( MessageTemplate.MatchPerformative(ACLMessage.INFORM), MessageTemplate.MatchProtocol("map"));

		final ACLMessage msg = this.myAgent.receive(msgTemplate);
		
		if (msg != null) {		
			
			System.out.println(this.myAgent.getLocalName()+"<----Result received from "+msg.getSender().getLocalName());
			try {
				HashMap<String,Object> truc = (HashMap<String, Object>) msg.getContentObject();	
				agent.getMapping().mergeMap((List<String>)truc.get("open"),(Set<String>)truc.get("closed"),(List<String>)truc.get("edges"));
				
				if(agent.getExplo() instanceof ExploDuoBehaviour) {
					agent.getMapping().setObjectives((List<String>)truc.get("objectives"));
				}
				
				ACLMessage answer=new ACLMessage(ACLMessage.CONFIRM);
				answer.setSender(this.myAgent.getAID());
				answer.setProtocol("MapReceived");
				answer.addReceiver(new AID(msg.getSender().getLocalName(),AID.ISLOCALNAME));
				
				this.agent.sendMessage(answer);
				
				agent.endConversation(msg.getSender().getLocalName());
				
			} catch (UnreadableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			answered = true;
		}
	}
	
	protected void confirmMapReception() {
		//1) receive the message
		final MessageTemplate msgTemplate =MessageTemplate.and( MessageTemplate.MatchPerformative(ACLMessage.CONFIRM), MessageTemplate.MatchProtocol("MapReceived"));

		final ACLMessage msg = this.myAgent.receive(msgTemplate);
		
		if (msg != null) {
			
			agent.endConversation(msg.getSender().getLocalName());
			
			System.out.println("Map confirmed");
		}
		
	}
	
	@Override
	public void action() {
		//timeout--;
		
		System.out.println("conversation " + Integer.toString(timeout));
		
		sendMap();
		getMap();
		confirmMapReception();
		
	}
	
	@Override
    public int onEnd() {
        if(getAnswer || answered || timeout == 0){
        	timeout=10;
        	return 1;
        }
        return 0;
    }

}