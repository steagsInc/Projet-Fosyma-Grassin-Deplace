package dedale.behaviours.hunting;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import dataStructures.tuple.Couple;
import dedale.agents.CustomAgent;
import dedale.behaviours.exploration.CustomExplorationBehaviour;
import dedale.behaviours.knowledge.MappingBehaviour;
import dedale.knowledge.MapRepresentation;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import jade.core.behaviours.TickerBehaviour;


public class GetAheadWumpus extends CustomExplorationBehaviour{
	
	private static final long serialVersionUID = 9088209402507795289L;
	
	private MappingBehaviour mapping;

	public GetAheadWumpus (final AbstractDedaleAgent myagent,MappingBehaviour mapping) {
		super(myagent);
		this.mapping = mapping;
	}

	@Override
	protected void onWake() {
		
		String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
		if (myPosition!=null){
			
			System.out.println("scout "+this.myAgent.getLocalName());
			//System.out.println(this.mapping.getWumpusPos());
			
			String nextPos = this.mapping.getPath(myPosition, this.mapping.getWumpusPos());
			String nextPosHunter = this.mapping.getPath(myPosition, this.mapping.getHunterPos());
			String nextNode=this.mapping.getNextNode(myPosition);
			
			if(nextPos!="") {
				System.out.println("wumpus");
				((AbstractDedaleAgent)this.myAgent).moveTo(nextPos);
			}
			else if(nextPosHunter!="") {
				System.out.println("hunter");
				System.out.println(myPosition+"to ->"+nextPosHunter);
				((AbstractDedaleAgent)this.myAgent).moveTo(nextPosHunter);
			}
			else if(nextNode!="") {
				System.out.println("node");
				 ((AbstractDedaleAgent)this.myAgent).moveTo(nextNode);
			}
			
			this.mapping.resetAgentPos();
		}

	}

	@Override
    public int onEnd() {
        return 0;
    }

}