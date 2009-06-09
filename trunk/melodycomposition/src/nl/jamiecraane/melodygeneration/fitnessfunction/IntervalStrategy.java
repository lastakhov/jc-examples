package nl.jamiecraane.melodygeneration.fitnessfunction;

import nl.jamiecraane.melodygeneration.Pitch;
import nl.jamiecraane.melodygeneration.Note;
import nl.jamiecraane.melodygeneration.util.GeneNoteFactory;

import org.jgap.Gene;
import org.jgap.IChromosome;
import org.jgap.impl.CompositeGene;

/**
 * Intervals are measured from the root note of a specific scale to the actual note.
 * When this strategy is used it is implied that the given melody contains as much root notes as there must
 * be major or perfect intervals.
 * For example: when the numberOfMajorIntervals is set to 2, the root note of the scale must
 * appear at least two times in the melody. The second note after the root note must form the 
 * major interval.
 * 
 * There is no support for diminished or augmented intervals yet.
 * TODO: Rewrite to use new Note class
 */
public final class IntervalStrategy extends AbstractMelodyFitnessStrategy {
	// We have a major second, third, sixth and seventh
	private int numberOfMajorIntervals = 0;
	// We have a perfect fourth, fifth
	private int numberOfPerfectÍntervals = 0;

	/**
	 * @throws IllegalArgumentException If the given scale is null.
	 */
	public IntervalStrategy() {
	}

	/**
	 * Sets the number of perfect intervals the melody must contain. Default 0.
	 * @param numberOfPerfectÍntervals
	 */
	public void setNumberOfPerfectÍntervals(int numberOfPerfectÍntervals) {
		this.numberOfPerfectÍntervals = numberOfPerfectÍntervals;
	}

    /**
     * Sets the numer of major intervals the melody must contain. Default 0.
     * @param numberOfMajorIntervals
     */
    public void setNumberOfMajorIntervals(int numberOfMajorIntervals) {
        this.numberOfMajorIntervals = numberOfMajorIntervals;
    }

    @Override
	public double calculateErrors(IChromosome melody) {
		double errors = 1.0D;
		int actualMajorIntervals = 0, actualPerfectIntervals = 0;
		
        Note previousNote = null;
        for (Gene gene : melody.getGenes()) {
            Note note = GeneNoteFactory.fromGene((CompositeGene) gene);
            if (previousNote != null) {
                if (currentNoteAndPreviousNoteAreNotes(previousNote, note)) {
                    if (super.scale.isMajorInterval(previousNote.getPitch(), note.getPitch())) {
                        actualMajorIntervals++;
                    }
                    if (super.scale.isPerfectInterval(previousNote.getPitch(), note.getPitch())) {
                        actualPerfectIntervals++;
                    }
                }
            }

            if (!note.isRest()) {
                previousNote = note;
            }
        }
		
		errors += Math.abs(this.numberOfMajorIntervals - actualMajorIntervals);
		errors += Math.abs(this.numberOfPerfectÍntervals - actualPerfectIntervals);
		
		if (Double.compare(errors, 1.0D) == 0) {
			errors = 0.0D;
		}
		
		// The interval strategy is quite importan so square the result
		return errors * errors;
	}

    private boolean currentNoteAndPreviousNoteAreNotes(Note previousNote, Note currentNote) {
        return !previousNote.isRest() && !currentNote.isRest();
    }

    public String toString() {
        return "[IntervalStrategy[numberOfMajorIntervals: " + this.numberOfMajorIntervals + ", numberOfPerfectÍntervals: " + this.numberOfPerfectÍntervals + "]]";
    }
}
