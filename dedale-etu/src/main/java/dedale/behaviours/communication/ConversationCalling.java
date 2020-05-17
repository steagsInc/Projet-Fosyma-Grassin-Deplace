package dedale.behaviours.communication;

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
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ConversationCalling extends OneShotBehaviour{

	private static final long serialVersionUID = -2058134622078521998L;

	private String lastPos;
	protected CustomAgent agent;
	
	private boolean finished = false;
	
	public boolean answered = false;
    public boolean getAnswer = false;
	
	public ConversationCalling (CustomAgent myagent) {
		super(myagent);
		this.lastPos="";
		this.agent = myagent;
		
		
	}

	protected void askConv() {
		String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();

		if (myPosition!=""){
			
			//System.out.println("Agent "+this.myAgent.getLocalName()+ " is trying to reach its friends");

			List<String> agents =this.agent.getYellowpage().getOtherAgents(this.agent);
			
			ACLMessage msg=new ACLMessage(ACLMessage.REQUEST);
			msg.setSender(this.myAgent.getAID());
			msg.setProtocol("Stop");
			
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
	
	protected void answerConv() {
		
		final MessageTemplate msgTemplate =MessageTemplate.and( MessageTemplate.MatchPerformative(ACLMessage.REQUEST), MessageTemplate.MatchProtocol("Stop"));
		
		final ACLMessage msg = this.myAgent.receive(msgTemplate);
		
		if (msg != null) {		
			//System.out.println(this.myAgent.getLocalName()+" <----Stop Request received from "+msg.getSender().getLocalName());
			if( agent.getConversationID(msg.getSender().getLocalName())>=0) {
				
				agent.newConversation(msg.getSender().getLocalName());
				
				ACLMessage answer=new ACLMessage(ACLMessage.CONFIRM);
				answer.setSender(this.myAgent.getAID());
				answer.setProtocol("hasStoped");
				answer.addReceiver(new AID(msg.getSender().getLocalName(),AID.ISLOCALNAME));
				
				this.agent.sendMessage(answer);
				answered = true;
			}
		}
	}
	
	protected void confirmConv() {
		
		final MessageTemplate msgTemplate =MessageTemplate.and( MessageTemplate.MatchPerformative(ACLMessage.CONFIRM), MessageTemplate.MatchProtocol("hasStoped"));

		final ACLMessage msg = this.myAgent.receive(msgTemplate);
		if (msg != null) {
			
			agent.newConversation(msg.getSender().getLocalName());
			getAnswer = true;
		}
	}

	@Override
	public void action() {
		
		getAnswer = false;
    	answered = false;
		askConv();
		answerConv();
		confirmConv();
	}
	
	@Override
    public int onEnd() {
        if (getAnswer && answered){
        	return 2;
        }
        return 0;
    }
}