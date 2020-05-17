package dedale.behaviours.exploration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import dataStructures.tuple.Couple;
import dedale.agents.ExploreSoloAgent;
import dedale.knowledge.MapRepresentation;
import dedale.knowledge.MapRepresentation.MapAttribute;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.WakerBehaviour;

public abstract class CustomExplorationBehaviour extends WakerBehaviour {

	public CustomExplorationBehaviour(final AbstractDedaleAgent myagent) {
		super(myagent,1000);
	}
	
}
