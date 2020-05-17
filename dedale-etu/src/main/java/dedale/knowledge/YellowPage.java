package dedale.knowledge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;

public class YellowPage {
	
	private static YellowPage instance = null;
	
	private List<String> agents;
	
	public YellowPage() {
		this.agents = new ArrayList<String>();
	}
	
	public void register(AbstractDedaleAgent agent) {
		this.agents.add(agent.getLocalName());
	}
	
	public List<String> getOtherAgents(AbstractDedaleAgent agent){
		List<String> copie = new ArrayList<String>();
		for(String a :this.agents) {
			if(a!=agent.getLocalName()) {
				copie.add(a);
			}
		}
		return copie;
	}

	public static YellowPage getinstance() {
		if (instance==null) {
			instance=new YellowPage();
		}
		
		return instance;
	}
	
}
