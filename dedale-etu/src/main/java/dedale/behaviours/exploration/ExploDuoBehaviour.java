package dedale.behaviours.exploration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import dataStructures.tuple.Couple;
import dedale.agents.CustomAgent;
import dedale.agents.ExploreSoloAgent;
import dedale.behaviours.knowledge.MappingBehaviour;
import dedale.knowledge.MapRepresentation;
import dedale.knowledge.MapRepresentation.MapAttribute;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SimpleBehaviour;

public class ExploDuoBehaviour extends CustomExplorationBehaviour {

	private static final long serialVersionUID = 8567689712496787661L;

	private boolean finished = false;

	private MappingBehaviour mapping;
	
	public ExploDuoBehaviour(final AbstractDedaleAgent myagent, MappingBehaviour mapping) {
		super(myagent);
		this.mapping=mapping;
	}

	@Override
	protected void onWake() {

		String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
	
		if (myPosition!=null){

			String nextNode=this.mapping.getNextNode(myPosition);
			
			if (nextNode=="") {
					finished = true;
			}else {
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
