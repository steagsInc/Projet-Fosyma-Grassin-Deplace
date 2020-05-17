package dedale.behaviours.knowledge;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import dataStructures.tuple.Couple;
import dedale.agents.CustomAgent;
import dedale.behaviours.knowledge.MappingBehaviour;
import dedale.knowledge.MapRepresentation;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.TickerBehaviour;

public class WumpusScouting extends OneShotBehaviour{
	
	private static final long serialVersionUID = 9088209402507795289L;
	
	private Boolean stench = false;

	public WumpusScouting (final AbstractDedaleAgent myagent) {
		super(myagent);
	}

	@Override
	public void action() {

		String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
		if (myPosition!=null){
			stench = false;
			
			if(!((CustomAgent)this.myAgent).getStenchs().isEmpty()) stench = true;
			
		}

	}

	@Override
    public int onEnd() {
		if(stench) return 5;
        return 0;
    }

}