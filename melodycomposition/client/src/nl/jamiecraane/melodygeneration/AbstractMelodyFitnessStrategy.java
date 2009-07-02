package nl.jamiecraane.melodygeneration;

import nl.jamiecraane.melodygeneration.MelodyFitnessStrategy;
import nl.jamiecraane.melodygeneration.Pitch;
import nl.jamiecraane.melodygeneration.Scale;

import org.jgap.Gene;
import org.jgap.IChromosome;

import javax.swing.*;

/**
 * Convenience class which subclasses can extend when implementing the MelodyFitnessStrategy interface.
 * This class provides a default implementation for the setScale method.
 * This class provides an empoty implementation for the configure method which subclasses can override when needed.
 */
public abstract class AbstractMelodyFitnessStrategy implements MelodyFitnessStrategy {
    protected Scale scale;

    final public void setScale(Scale scale) {
        this.scale = scale;
    }

    final boolean isRest(Gene pitch) {
		return Pitch.REST == Pitch.getByIndex((Integer) pitch.getAllele());
	}

    @Override
	abstract public double calculateErrors(IChromosome melody);

    @Override
    abstract public void init(JPanel container);

    /**
     * Empty implementation of the configure method.
     * Subclasses can implement this method if needed
     *
     * @see MelodyFitnessStrategy#configure()
     */
    public void configure() {
        // Subclasses can implement this method when needed
    }
}
