package dedale.behaviours.communication;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import dataStructures.tuple.Couple;
import dedale.agents.CustomAgent;
import dedale.behaviours.knowledge.MappingBehaviour;
import dedale.knowledge.MapRepresentation;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class BroadcastWumpus extends OneShotBehaviour{
	
	private static final long serialVersionUID = 9088209402507795289L;
	
	private Boolean huntCall = false;
	
	protected CustomAgent agent;
	
	private String lastPos;
	
	private role AgentRole;
	
	public enum role {
		Broadcaster,
		Receiver,
		Both
	}

	public BroadcastWumpus (CustomAgent myagent) {
		super(myagent);
		this.agent = myagent;
		this.lastPos = "";
		this.AgentRole = role.Both;
	}
	
	public BroadcastWumpus (CustomAgent myagent,role aRole) {
		super(myagent);
		this.agent = myagent;
		this.lastPos = "";
		this.AgentRole = aRole;
	}

	public void sendMessage() {
		//Example to retrieve the current position
		String myPosition=this.agent.getCurrentPosition();
		if (myPosition!=null){
			
			List<String> wumpusPos = ((CustomAgent)this.myAgent).getStenchs();
			
			if(!wumpusPos.isEmpty()) {
				List<String> agents =this.agent.getYellowpage().getOtherAgents(this.agent);
				
				ACLMessage msg=new ACLMessage(ACLMessage.INFORM);
				msg.setSender(this.myAgent.getAID());
				msg.setProtocol("WumpusPos");
				msg.setContent(wumpusPos.get(0)+","+myPosition);
				
				for(String a:agents) {
					if(this.agent.getConversationID(a)>=0) {
						if(a!=null) msg.addReceiver(new AID(a,AID.ISLOCALNAME));
					}
				}

				this.agent.sendMessage(msg);
				this.lastPos=myPosition;
				
			}
			
		}

	}
	
	protected void getAnswer() {
		
		final MessageTemplate msgTemplate =MessageTemplate.and( MessageTemplate.MatchPerformative(ACLMessage.INFORM), MessageTemplate.MatchProtocol("WumpusPos"));

		final ACLMessage msg = this.myAgent.receive(msgTemplate);
		if (msg != null) {
			
			String[] splits = ((String)msg.getContent()).split(",",0);
			
			this.agent.getMapping().setWumpusPos(splits[0]);
			this.agent.getMapping().setHunterPos(splits[1]);
			
			this.huntCall = true;
			
		}
	}
	
	@Override
	public void action() {
		this.huntCall = false;
		
		if(this.AgentRole == role.Both || this.AgentRole == role.Broadcaster) sendMessage();
		if(this.AgentRole == role.Both || this.AgentRole == role.Receiver) getAnswer();
		
	}

	@Override
    public int onEnd() {
		if(huntCall) return 20;
        return 0;
    }

}