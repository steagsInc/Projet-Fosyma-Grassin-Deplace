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


public class FollowWumpus extends CustomExplorationBehaviour{
	
	private static final long serialVersionUID = 9088209402507795289L;
	
	private MappingBehaviour mapping;
	
	private int lost = 5;
	
	private String last = "";
	
	private String wumPos = "";

	public FollowWumpus (final AbstractDedaleAgent myagent,MappingBehaviour mapping) {
		super(myagent);
		this.mapping = mapping;
		this.lost = 5;
	}

	@Override
	protected void onWake(){
		String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
		
		if (myPosition!=null){
			
			if(myPosition.equals(this.wumPos) || this.mapping.getAgentsPos().contains(this.wumPos)) this.wumPos="";
			else if (wumPos!="") {
				System.out.println(myPosition+"to wumpos : "+wumPos + this.myAgent.getLocalName());
				((AbstractDedaleAgent)this.myAgent).moveTo(wumPos);
			}
			
			List<String> stenchs = ((CustomAgent)this.myAgent).getStenchs();
			
			stenchs.removeAll(this.mapping.getAgentsPos());
			stenchs.remove(this.last);
			
			String nextMove = null;
			
			if(!stenchs.isEmpty()) nextMove = stenchs.get(0);

			if(nextMove!=null) {
				String nextPos = this.mapping.getPath(myPosition, nextMove);
				if(nextPos!="") {
					this.last = myPosition;
					this.wumPos = nextPos;
					System.out.println(myPosition+"->"+wumPos);
					((AbstractDedaleAgent)this.myAgent).moveTo(nextPos);
				}
				lost = 5;
			}else{
				
				lost--;
			}
			
			this.mapping.resetAgentPos();
		}

	}

	@Override
    public int onEnd() {
		if(lost<=0) {
			System.out.println("lost him");
			return 10;
		}else {
			return 0;
		}
    }

}