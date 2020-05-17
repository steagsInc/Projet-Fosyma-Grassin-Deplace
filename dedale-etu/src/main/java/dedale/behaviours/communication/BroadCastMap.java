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

public class BroadcastMap extends OneShotBehaviour{

	private static final long serialVersionUID = -2058134622078521998L;

	private CustomAgent agent;
	
	public BroadcastMap (CustomAgent myagent) {
		super(myagent);
		this.agent = myagent;
		
	}
	
	public boolean answered = false;
    public boolean getAnswer = false;

	protected void sendMap() {
		
		String myPosition=this.agent.getCurrentPosition();
		if (myPosition!=null){
			
			ACLMessage msg=new ACLMessage(ACLMessage.INFORM);
			msg.setSender(this.myAgent.getAID());
			msg.setProtocol("Broadmap");
			
			HashMap<String,Object> truc = new HashMap<String, Object>();
			
			truc.put("open", agent.getMapping().getOpenNodes());
			truc.put("closed", agent.getMapping().getClosedNodes());
			truc.put("edges", agent.getMapping().getEdges());
				
			//System.out.println("Agent "+this.myAgent.getLocalName()+ " is trying to send a map");
			List<String> agents =this.agent.getYellowpage().getOtherAgents(this.agent);
			
			try {
				msg.setContentObject(truc);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			for(String a:agents) {
				if(a!=null) msg.addReceiver(new AID(a,AID.ISLOCALNAME));
			}

			this.agent.sendMessage(msg);
			
		}
	}
	
	protected void getMap() {
		//1) receive the message
		final MessageTemplate msgTemplate =MessageTemplate.and( MessageTemplate.MatchPerformative(ACLMessage.INFORM), MessageTemplate.MatchProtocol("Broadmap"));

		final ACLMessage msg = this.myAgent.receive(msgTemplate);
		
		if (msg != null) {		
			
			try {
				HashMap<String,Object> truc = (HashMap<String, Object>) msg.getContentObject();	
				agent.getMapping().mergeMap((List<String>)truc.get("open"),(Set<String>)truc.get("closed"),(List<String>)truc.get("edges"));
				
			} catch (UnreadableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void action() {
		
		sendMap();
		getMap();
		
	}
	
	@Override
    public int onEnd() {
        return 0;
    }

}