package generalBehaviour;

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
import dedale.behaviours.communication.BroadcastMap;
import dedale.behaviours.communication.BroadcastWumpus;
import dedale.behaviours.communication.ConversationCalling;
import dedale.behaviours.communication.ExchangeMap;
import dedale.behaviours.communication.position.BroadcastPosition;
import dedale.behaviours.hunting.FollowWumpus;
import dedale.behaviours.hunting.GetAheadWumpus;
import dedale.behaviours.knowledge.WumpusScouting;
import dedale.knowledge.MapRepresentation;
import dedale.knowledge.MapRepresentation.MapAttribute;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.SimpleBehaviour;

public class GeneralFSM extends FSMBehaviour {
	
	private CustomAgent myAgent;
	
	

	public GeneralFSM(CustomAgent myagent) {
		super(myagent);
		this.myAgent = myagent;
		
		ParallelBehaviour exploProcess = new ParallelProcess(this.myAgent,"EXPLO",this.myAgent.getMapping(),new WumpusScouting(this.myAgent),new BroadcastWumpus(this.myAgent,BroadcastWumpus.role.Receiver),new ConversationCalling(this.myAgent),new BroadcastPosition(this.myAgent),this.myAgent.getExplo());
		
		ParallelBehaviour convoProcess = new ParallelProcess(this.myAgent,"CONVERSATION",new ExchangeMap(this.myAgent));
		
		ParallelBehaviour huntingProcess = new ParallelProcess(this.myAgent,"HUNTING",this.myAgent.getMapping(),new BroadcastPosition(this.myAgent),new BroadcastMap(this.myAgent),new FollowWumpus(this.myAgent,this.myAgent.getMapping()),new BroadcastWumpus(this.myAgent,BroadcastWumpus.role.Broadcaster));
		
		ParallelBehaviour scoutingProcess = new ParallelProcess(this.myAgent,"SCOUT",this.myAgent.getMapping(),new BroadcastPosition(this.myAgent),new BroadcastMap(this.myAgent),new WumpusScouting(this.myAgent),new BroadcastWumpus(this.myAgent,BroadcastWumpus.role.Receiver),new GetAheadWumpus(this.myAgent,this.myAgent.getMapping()));
		
		String [] explo = {"EXPLO"} ;
        String [] conversation = {"CONVERSATION"} ;
        String [] hunting = {"HUNTING"} ;
        String [] scout = {"SCOUT"} ;
        
        
        registerFirstState(exploProcess,"EXPLO");
        registerState(convoProcess,"CONVERSATION");
        registerState(huntingProcess,"HUNTING");
        registerState(scoutingProcess,"SCOUT");
        
        registerDefaultTransition("EXPLO","EXPLO",explo);
        registerTransition("EXPLO","EXPLO",0,explo);
        registerTransition("EXPLO","CONVERSATION",2,conversation);
        registerTransition("EXPLO","HUNTING",5,hunting);
        registerTransition("EXPLO","SCOUT",20,scout);
        registerTransition("EXPLO","SCOUT",21,scout);
        registerTransition("EXPLO","SCOUT",25,scout);
        
        registerDefaultTransition("CONVERSATION","EXPLO",explo);
        registerTransition("CONVERSATION","EXPLO",1,explo);
        
        registerDefaultTransition("HUNTING","HUNTING",hunting);
        registerTransition("HUNTING","HUNTING",0,hunting);
        registerTransition("HUNTING","EXPLO",10,explo);
        
        registerDefaultTransition("SCOUT","SCOUT",scout);
        registerTransition("SCOUT","SCOUT",0,scout);
        registerTransition("SCOUT","HUNTING",5,hunting);
		
	}
	
}
