package dedale.agents;

import java.util.ArrayList;
import java.util.List;

import dedale.behaviours.communication.ConversationCalling;
import dedale.behaviours.communication.ExchangeMap;
import dedale.behaviours.exploration.ExploDuoBehaviour;
import dedale.behaviours.exploration.ExploSoloBehaviour;
import dedale.knowledge.MapRepresentation;
import dedale.knowledge.YellowPage;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;
import generalBehaviour.GeneralFSM;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.WakerBehaviour;

public class ExploreDuoAgent extends CustomAgent {

	private static final long serialVersionUID = -6431752455590433727L;
	
	protected void setup(){

		super.setup();
		
		this.explo = new ExploDuoBehaviour(this,this.mapping);
		lb.add(new GeneralFSM(this));
		
		try {
			this.doWait(2000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		addBehaviour(new startMyBehaviours(this,lb));
		
		System.out.println("the agent "+this.getLocalName()+ " is created");

	}
}
