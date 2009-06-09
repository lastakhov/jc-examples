package nl.jamiecraane.melodygeneration.fitnessfunction;

import nl.jamiecraane.melodygeneration.MelodyFitnessStrategy;
import nl.jamiecraane.melodygeneration.Pitch;
import nl.jamiecraane.melodygeneration.Scale;

import org.jgap.Gene;
import org.jgap.IChromosome;

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
}
