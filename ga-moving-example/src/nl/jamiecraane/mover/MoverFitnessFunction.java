package nl.jamiecraane.mover;

import org.jgap.FitnessFunction;
import org.jgap.IChromosome;
import org.jgap.Gene;

/**
 * Fitness function for the Mover example. See this
 * {@link #evaluate(IChromosome)} for the actual fitness function.
 */
public class MoverFitnessFunction extends FitnessFunction {
	private Box[] boxes;
	private double vanCapacity;

	public void setVanCapacity(double vanCapacity) {
		this.vanCapacity = vanCapacity;
	}

	public void setBoxes(Box[] boxes) {
		this.boxes = boxes;
	}

	/**
	 * Fitness function. A lower value value means the difference between the
	 * total volume of boxes in a van is small, which is better. This means a
	 * more optimal distribution of boxes in the vans. The number of vans needed
	 * is multiplied by the size difference as more vans are more expensive.
	 */
	@Override
	protected double evaluate(IChromosome a_subject) {
		double wastedVolume = 0.0D;
		double volumeInCurrentVan = 0.0D;
		int numberOfVansNeeded = 1;
        
        Gene[] genes = a_subject.getGenes();
        for (Gene gene : genes) {
            int index = (Integer) gene.getAllele();
            if (enoughSpaceAvailable(volumeInCurrentVan, this.boxes[index])) {
				volumeInCurrentVan += this.boxes[index].getVolume();
			} else {
				numberOfVansNeeded++;
				wastedVolume += Math.abs(vanCapacity - volumeInCurrentVan);
				// Make sure we put the box which did not fit in this van in the next van
				volumeInCurrentVan = this.boxes[index].getVolume();
			}
        }

		return wastedVolume * numberOfVansNeeded;
    }

    private boolean enoughSpaceAvailable(double volumeInVan, Box box) {
        return (volumeInVan + box.getVolume()) <= this.vanCapacity;
    }
}
