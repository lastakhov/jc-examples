package nl.jamiecraane.melodygeneration.fitnessfunction;

import org.jgap.IChromosome;
import nl.jamiecraane.melodygeneration.AbstractMelodyFitnessStrategy;

import javax.swing.*;

/**
 * Strategy which takes the individual pitch distance in semitones with two consecutive notes into account.
 */
public final class IndividualNotesPitchDistributionStrategy extends AbstractMelodyFitnessStrategy {
	private double pitchAdherenceThreshold = 0.85D;
	private int maximumPitchDifferenceForTwoConsecutiveNotes = 6;

    public IndividualNotesPitchDistributionStrategy(double pitchAdherenceThreshold) {
        this.pitchAdherenceThreshold = pitchAdherenceThreshold;
    }

    /**
	 * @param maximumPitchDifferenceForTwoConsecutiveNotes The maximum difference in pitch between two consecutive notes. Defailt is 6.
	 */
	public void setMaximumPitchDifferenceForTwoConsecutiveNotes(int maximumPitchDifferenceForTwoConsecutiveNotes) {
		this.maximumPitchDifferenceForTwoConsecutiveNotes = maximumPitchDifferenceForTwoConsecutiveNotes;
	}
	
	/**
	 * How many percent of the notes in a melody must fall within the maximum pitch difference between two consecutive notes?
	 * @param pitchAdherenceThreshold Must be between 0.0D and 1.0D. Default = 0.85.
	 */
	public void setPitchAdherenceThreshold(double pitchAdherenceThreshold) {
		this.pitchAdherenceThreshold = pitchAdherenceThreshold;
	}

	@Override
	public double calculateErrors(IChromosome melody) {
		// TODO Auto-generated method stub
		return 0;
	}

    @Override
    public void init(JPanel container) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public String toString() {
        return "[IndividualNotesPitchDistributionStrategy[pitchAdherenceThreshold: " + this.pitchAdherenceThreshold + ", maximumPitchDifferenceForTwoConsecutiveNotes: " + this.maximumPitchDifferenceForTwoConsecutiveNotes + "]]";
    }
}
