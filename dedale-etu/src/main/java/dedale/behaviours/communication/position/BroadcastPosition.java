package dedale.behaviours.communication.position;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import dedale.agents.CustomAgent;
import dedale.agents.ExploreSoloAgent;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class BroadcastPosition extends OneShotBehaviour{

	private static final long serialVersionUID = -2058134622078521998L;

	private String lastPos;
	
	private boolean finished = false;
	
	protected CustomAgent agent;
	
	public BroadcastPosition (CustomAgent myagent) {
		super(myagent);
		this.agent = myagent;
		this.lastPos = "";
	}

	protected void sendMessage() {
		String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();

		if (myPosition!=""){
			
			//System.out.println("Agent "+this.myAgent.getLocalName()+ " is trying to reach its friends");

			List<String> agents =this.agent.getYellowpage().getOtherAgents(this.agent);
			
			ACLMessage msg=new ACLMessage(ACLMessage.INFORM);
			msg.setSender(this.myAgent.getAID());
			msg.setProtocol("Pos");
			msg.setContent(myPosition);
			
			for(String a:agents) {
				if(this.agent.getConversationID(a)>=0) {
					if(a!=null) msg.addReceiver(new AID(a,AID.ISLOCALNAME));
				}
			}

			this.agent.sendMessage(msg);
			this.lastPos=myPosition;
			
		}
		
		if(this.agent.getYellowpage().getOtherAgents(this.agent).isEmpty()) {
			//ne spam pas s'il est tout seul
			finished = true;
		}
	}
	
	protected void getAnswer() {
		
		final MessageTemplate msgTemplate =MessageTemplate.and( MessageTemplate.MatchPerformative(ACLMessage.INFORM), MessageTemplate.MatchProtocol("Pos"));

		final ACLMessage msg = this.myAgent.receive(msgTemplate);
		if (msg != null) {

			this.agent.getMapping().updateAgentPos(msg.getSender().getLocalName(), msg.getContent());
			
		}
	}

	@Override
    public int onEnd() {
        return 0;
    }

	@Override
	public void action() {
		sendMessage();
		getAnswer();
		
	}
}