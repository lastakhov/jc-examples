package nl.jamiecraane.melodygeneration.fitnessfunction;

import nl.jamiecraane.melodygeneration.Note;
import nl.jamiecraane.melodygeneration.AbstractMelodyFitnessStrategy;
import nl.jamiecraane.melodygeneration.util.GeneNoteFactory;

import org.jgap.Gene;
import org.jgap.IChromosome;
import org.jgap.impl.CompositeGene;

import javax.swing.*;

/**
 * Strategy which counts large pitch differences as errors.
 */
public final class GlobalPitchDistributionStrategy extends AbstractMelodyFitnessStrategy {
	private double pitchAdherenceThreshold = 0.85D;
	private int maximumPitchDifferenceInSemitones = 8;

	/**
	 * How many percent of the notes in a melody must fall within the maximum pitch difference?
	 * @param pitchAdherenceThreshold Must be between 0.0D and 1.0D. Default = 0.85.
	 */
	public void setPitchAdherenceThreshold(double pitchAdherenceThreshold) {
		this.pitchAdherenceThreshold = pitchAdherenceThreshold;
	}

	/**
	 * What is the maximum difference in pitch between the notes in a melody. Default is 8.
	 * @param maximumPitchDifferenceInSemitones
	 */
	public void setMaximumPitchDifferenceInSemitones(int maximumPitchDifferenceInSemitones) {
		this.maximumPitchDifferenceInSemitones = maximumPitchDifferenceInSemitones;
	}

	@Override
	public double calculateErrors(IChromosome melody) {
		long numberOfNotesThatMustBeInMaximumPitch = Math.round(melody.getGenes().length * this.pitchAdherenceThreshold);
		int numberOfNotesNotInPitchDistribution = 0;
        Note startOfMelody = null;
		for (Gene gene : melody.getGenes()) {
            Note note = GeneNoteFactory.fromGene((CompositeGene) gene);
            if (startOfMelody != null) {
                if (!startOfMelody.isRest() && !note.isRest()) {
                    int differenceInSemitones = startOfMelody.getDifferenceInSemiTones(note);
                    if (differenceInSemitones > this.maximumPitchDifferenceInSemitones) {
                        numberOfNotesNotInPitchDistribution++;
                    }
                }
            } else {
                if (!note.isRest()) {
                    startOfMelody = note;
                }
            }
        }
		
		return Math.abs((melody.getGenes().length - numberOfNotesNotInPitchDistribution) - numberOfNotesThatMustBeInMaximumPitch);
	}

    @Override
    public void init(JPanel container) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public String toString() {
        return "[GlobalPitchDistributionStrategy[pitchAdherenceThreshold: " + this.pitchAdherenceThreshold + ", maximumPitchDifferenceInSemitones: " + this.maximumPitchDifferenceInSemitones + "]]";
    }
}
