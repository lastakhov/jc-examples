package nl.jamiecraane.mover.bruteforce;

import nl.jamiecraane.mover.Box;
import nl.jamiecraane.mover.Van;

import java.util.Arrays;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

import org.jgap.Gene;

public class BruteForceTester {
	private int[] input;
	private static final int NUMBER = 20;
	private static final double MINIMUM_VOLUME = 0.25;
	private static final double MAXIMUM_VOLUME = 2.75;
	private double totalVolumeOfBoxes = 0.0D;
	private static final double VOLUME_OF_VANS = 4.33;
	
	private Box[] boxes;
	
	public static void main(String[] args) {
		new BruteForceTester();
	}
	
	private void createBoxes(int seed) {
		Random r = new Random(seed);
		this.boxes = new Box[NUMBER];
		for (int i = 0; i < NUMBER; i++) {
			Box box = new Box(MINIMUM_VOLUME + (r.nextDouble() * MAXIMUM_VOLUME));
            box.setId(i);
            this.boxes[i] = box;
        }

        double[] volumes = new double[this.boxes.length];
        for (int i = 0; i < this.boxes.length; i++) {
			System.out.println("Box [" + i + "]: " + this.boxes[i]);
			this.totalVolumeOfBoxes += this.boxes[i].getVolume();
            volumes[i] = this.boxes[i].getVolume();
        }
		System.out.println("The total volume of the [" + NUMBER + "] boxes is [" + this.totalVolumeOfBoxes + "] cubic metres.");
    }
	
	private List<Van> numberOfVansNeeded(int[] solution) {
        List<Van> vans = new ArrayList();
        Van van = new Van(VOLUME_OF_VANS);
        for (int index : solution) {
            if (!van.addBox(this.boxes[index])) {
                // A new van is needed
                vans.add(van);
                van = new Van(VOLUME_OF_VANS);
                van.addBox(this.boxes[index]);
            }
        }
		return vans;
	}
	
	public BruteForceTester() {
		this.createBoxes(37);
		
		int optimalNumberOfVans = (int) Math.ceil(this.totalVolumeOfBoxes / VOLUME_OF_VANS);
		System.out.println("Optimal number of vans = " + optimalNumberOfVans);
		
		long startTime = System.currentTimeMillis();
		this.input = new int[NUMBER];
		for (int i = 0; i < this.input.length; i++) {
			this.input[i] = i;
		}
		
		Permutations permutations = new Permutations();
		long n = permutations.fact(this.input.length);		
		int[] nextSolution = this.input;
		int numberOfVansFound = Integer.MAX_VALUE;
		int[] bestSolution = nextSolution;
		for (int i = 0; i < n; i++) {							
			if (i == (n - 1)) {
				int x = 1;
			}
			int numberOfVansNeeded = this.numberOfVansNeeded(nextSolution).size();
            if (numberOfVansNeeded < numberOfVansFound) {
				numberOfVansFound = numberOfVansNeeded;
				bestSolution = Arrays.copyOf(nextSolution, nextSolution.length);
			}
			if (numberOfVansNeeded == optimalNumberOfVans) {				
				break;
			}				
			nextSolution = permutations.nextPerm(nextSolution);
		}
		
		long endTime = System.currentTimeMillis();
		System.out.println("Optimal number of vans found = " + numberOfVansFound);
		printSolution(bestSolution);
		System.err.println("Time to generate permutations = " + (endTime - startTime) + " ms.");
	}

	private void printSolution(int[] nextSolution) {
		for (int j = 0; j < nextSolution.length; j++)
			System.out.print(nextSolution[j] + ", ");
		System.out.println();
	}
}
