package nl.jamiecraane.melodygeneration;

import org.jgap.IChromosome;

public interface MelodyFitnessStrategy {
	double calculateErrors(IChromosome chromosome);
    void setScale(Scale scale);
}
