package dedale.agents;

import java.util.ArrayList;
import java.util.List;

import dedale.behaviours.communication.ConversationCalling;
import dedale.behaviours.communication.ExchangeMap;
import dedale.behaviours.exploration.ExploSoloBehaviour;
import dedale.knowledge.MapRepresentation;
import dedale.knowledge.YellowPage;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;
import jade.core.behaviours.Behaviour;

public class ExploreSoloAgent extends CustomAgent {

	private static final long serialVersionUID = -6431752665590433727L;
	
	protected void setup(){

		super.setup();
		
		this.explo = new ExploSoloBehaviour(this,this.mapping);
		lb.add(this.explo);
		
		addBehaviour(new startMyBehaviours(this,lb));
		
		System.out.println("the  agent "+this.getLocalName()+ " is started");

	}
}
