package goals.agents;

import java.util.ArrayList;
import java.util.Vector;

import goals.core.Simulator;
import goals.objects.SimObject;
import goals.objects.Water;
import sim.util.Int2D;

public class BrokerAgent {
	
	private ArrayList<PointOfInterest> pointsOfInterest;
	private ArrayList<PointOfInterest> removedPoIs;
	private Vector<ExplorerAgent> myExplorerAgents;
	
	public BrokerAgent() {
		this.pointsOfInterest = new ArrayList<PointOfInterest>();
		this.removedPoIs = new ArrayList<PointOfInterest>();
		this.myExplorerAgents = new Vector<ExplorerAgent>();
	}
	
	public Int2D requestTarget(Int2D agentPos) {
		Int2D target = null;
		PointOfInterest target_PoI = null;
				
		
		// If we have no points of interest, return a random point
		if (pointsOfInterest.size() == 0)
			return getLimitedRandomTarget(agentPos);
			//return getRandomTarget();
		
		// Else, find the best point of Interest
		else {
			
			double bestScore = Double.NEGATIVE_INFINITY;
			double score;
			
			for (PointOfInterest PoI : pointsOfInterest) {
				//System.out.println("INTEREST: "+PoI.interestMeasure);
				if(PoI.interestMeasure>99.0)
					score = PoI.interestMeasure;				
				else
					score = PoI.interestMeasure - ( (agentPos.distance(PoI.loc) * 100) / Simulator.limitRadius);
				//System.out.println("[Broker] Score for " + PoI + ": " + score);
				
				//if (score > bestScore && !agentNear(PoI.loc, agentPos)) {
				if(score > bestScore){
					bestScore = score;
					target = PoI.loc;
					target_PoI = PoI;
				}
			}
			
			// If the target is too far, send a random target
			if (bestScore < 0)
				return getLimitedRandomTarget(agentPos);
			
			// Remove the target from the list of Points of Interest and add it to the removed list (this should be done when you arrive at the point if you're constantly calculating new targets)
			if (target_PoI != null) {
				pointsOfInterest.remove(target_PoI);
				removedPoIs.add(target_PoI);
			}
			
			//System.out.println("[Broker] Best score: " + bestScore);
			//System.out.println("[Broker] Target: " + target);
		}
		
		return target;
	}
		
 	public boolean agentNear(Int2D poi, Int2D agentpos)
 	{
 		for(ExplorerAgent agent: myExplorerAgents)
 		{
 			if(agent.getLoc().getX() == agentpos.getX() && agent.getLoc().getY() == agentpos.getY())
 				continue;
 			if((agent.getLoc().getX() < poi.getX()-20 || agent.getLoc().getX() > poi.getX()+20) && 
 					(agent.getLoc().getY() < poi.getY()-20 ||agent.getLoc().getY() > poi.getY()+20))
 					return true;
 		}
 		return false;
 		
 	}
 	public void setMyExplorerAgents(Vector<ExplorerAgent> agents)
 	{
 		this.myExplorerAgents = agents;
 	}
 	
	public void addPointOfInterest(Int2D point, double interestMeasure) {
		
		PointOfInterest PoI = new PointOfInterest(point, interestMeasure);
		
		if (!pointsOfInterest.contains(PoI) && !removedPoIs.contains(PoI)) {
			pointsOfInterest.add(PoI);
			//System.out.println("[Broker] PoI added: " + PoI.loc);
		}
	}
	
	public void addRelativePOI(Int2D point, double interestMeasure)
	{
		
		PointOfInterest PoI = new PointOfInterest(point, interestMeasure);
		PointOfInterest chk = checkPOI(point);
		if (chk!=null)
		{
			if(!checkIfRemoved(PoI.loc))
				pointsOfInterest.add(PoI);
			//System.out.println("POI --> "+ PoI.interestMeasure);
			for(int x=-20; x<19;x+=3)
			{
				for(int y=-20;y<19;y+=3)
				{
					int X = point.getX()+x;
					int Y = point.getY()+y;
					if(0<X || X>400 || 0<Y || Y>300) continue;
					Int2D tmp = new Int2D(X,Y);
					
					
					chk = checkPOI(tmp);				
					if (chk==null && !checkIfRemoved(tmp)) {
						PointOfInterest temp = new PointOfInterest(tmp, interestMeasure-1.0);
						pointsOfInterest.add(temp);
					}
					else if(chk!=null && !checkIfRemoved(tmp))
					{
						pointsOfInterest.get(pointsOfInterest.indexOf(chk)).interestMeasure+=20.0;					
					}
				}			
			}
		}
	}
	
	
	public void decayPoints(Int2D loc)
	{
		for(int x=-10; x<9;x+=2)
		{
			for(int y=-10;y<9;y+=2)
			{
				int X = loc.getX()+x;
				int Y = loc.getY()+y;
				if(0<X || X>400 || 0<Y || Y>300) continue;
				Int2D tmp = new Int2D(X,Y);
				PointOfInterest cur = checkPOI(tmp);
				if(cur!=null)
				{
					cur.interestMeasure-=3.5;
				}
			}
		}
		
	}
	public boolean checkIfRemoved(Int2D loc)
	{
		for(PointOfInterest cur: removedPoIs)
		{
			if(loc.getX() == cur.loc.getX() && loc.getY() == cur.loc.getY())
				return true;
		}
		return false;
	}
	public PointOfInterest checkPOI(Int2D temp)
	{
		for(PointOfInterest cur: pointsOfInterest)
		{
			if(temp.getX() == cur.loc.getX() && temp.getY() == cur.loc.getY())
				return cur;
		}
		return null;
	}
	
	
	public void removePointOfInterest(Int2D loc) {
		PointOfInterest tmp = new PointOfInterest(loc, 1);
		
		if (pointsOfInterest.contains(tmp)) {
			//System.out.println("[Broker] Removing " + loc + " ("+ pointsOfInterest.size() + ")");
			pointsOfInterest.remove(tmp);
			//System.out.println("[Broker] Now with " + pointsOfInterest.size());
			
			removedPoIs.add(tmp);
		}
	}
	

	
	public Int2D getLimitedRandomTarget(Int2D agentPos) {
		Int2D target = null;
		
		while (true) {
			target = getRandomTarget();
			if (agentPos.distance(target) <= Simulator.limitRadius)
				break;
		}
		
		return target; 
	}
	
	public Int2D getRandomTarget() {
		return new Int2D((int)(Math.random()*Simulator.WIDTH), (int)(Math.random()*Simulator.HEIGHT)); 
	}
}


class PointOfInterest {
	public Int2D loc;
	public double interestMeasure;	// I expect this to be in [0, 100]
	
	PointOfInterest(Int2D loc, double interestMeasure) {
		this.loc = loc;
		this.interestMeasure = interestMeasure;
	}
	
	@Override
	public boolean equals(Object o_PoI) {
		PointOfInterest PoI = (PointOfInterest) o_PoI;
		return this.loc.equals(PoI.loc);
	}
	
	public String toString() {
		return "[" + loc.x + ", " + loc.y + " - " + interestMeasure + "]";
	}
}
