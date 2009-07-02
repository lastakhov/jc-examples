package nl.jamiecraane.melodygeneration.fitnessfunction;

import org.jgap.Gene;
import org.jgap.IChromosome;
import org.jgap.impl.CompositeGene;
import nl.jamiecraane.melodygeneration.util.GeneNoteFactory;
import nl.jamiecraane.melodygeneration.Note;
import nl.jamiecraane.melodygeneration.AbstractMelodyFitnessStrategy;

import javax.swing.*;

/**
 * Strategy that calculates number proportion of rests and notes in a given melody. The higher the propertion of rests
 * differs from the optimal proportion as specified by the setMaximumPercentageOfRests method, the higher the errors.
 * The default proportion for setMaximumPercentageOfRests is 20.
 */
public final class ProportionRestAndNotesStrategy extends AbstractMelodyFitnessStrategy {
	private double maximumPercentageOfRests = 20.0D;

	/**
	 * Sets the ideal proportion of rests and notes in a melody. The higher this percentage, the more
	 * rests the melody gets. Default is 20%. A value of 0% means that the melody should not contain rests at all.
	 * @param maximumPercentageOfRests Value must be between 0 and 100
	 */
	public void setMaximumPercentageOfRests(double maximumPercentageOfRests) {
		if (Double.compare(maximumPercentageOfRests, 0.0D) == -1 ||
			Double.compare(maximumPercentageOfRests, 100.0D) == 1) {
			throw new IllegalArgumentException("maximumPercentageOfRests must be between 0 and 100");
		}
		
		this.maximumPercentageOfRests = maximumPercentageOfRests;
	}

	@Override
	public double calculateErrors(IChromosome melody) {
		int numberOfRests = 0;

		for (Gene gene : melody.getGenes()) {
            Note note = GeneNoteFactory.fromGene((CompositeGene) gene);
			if (note.isRest()) {
				numberOfRests++;
			}
		}

		double percentageOfRests = this.calculateProportionOfRestsComparedToNotes(melody, numberOfRests);

		// Multipy by 2 to make the parameter slightly more important than default
		return Math.abs(this.maximumPercentageOfRests - percentageOfRests) * 2;
	}

    @Override
    public void init(JPanel container) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private double calculateProportionOfRestsComparedToNotes(IChromosome chromosome, int numberOfRests) {
        return ((double) numberOfRests / (double) chromosome.getGenes().length) * 100;
    }

    public String toString() {
        return "[ProportionRestAndNotesStrategy[maximumPercentageOfRests: " + this.maximumPercentageOfRests + "]]";
    }
}
