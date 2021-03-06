package goals.core;

import goals.env.SimEnvironment;
import goals.objects.SimObject;
import goals.objects.*;
import sim.engine.SimState;

/**
 * @author Miguel Tavares, Pedro Gaspar, Modified by Pedro Macedo and Jo�o Duro
 * 
 */
public class Simulator extends SimState{

	private static final long serialVersionUID = 1L;
	
	private final static int N_EXPLORERS = 3;
	public final static int WIDTH = 400;
	public final static int HEIGHT = 300;
	public final static int limitRadius = (int) (Math.max(WIDTH, HEIGHT) * 0.7);	// Changed from 0.25 to 0.7
	
	public SimEnvironment env;
	
	private SimObject exploresInterest = new Water();
	
	/**
	 * Default constructor that creates a new instance of the simulator
	 * @param seed Seed for the random number generator
	 */
	public Simulator(long seed) {
		super(seed);
	}
	
	/**
	 * Start the simulation. Schedule the environment to iterate.
	 */
	public void start(){
		super.start();
		env = new SimEnvironment(this, WIDTH, HEIGHT, N_EXPLORERS,exploresInterest);
		schedule.scheduleRepeating(env);
		// Now, everything else is up to the environment
	}

	public static void main(String[] args)
    {
		doLoop(Simulator.class, args);
		System.exit(0);
    }    
}


