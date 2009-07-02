package nl.jamiecraane.melodygeneration.plugins;

import nl.jamiecraane.melodygeneration.Note;
import nl.jamiecraane.melodygeneration.AbstractMelodyFitnessStrategy;
import nl.jamiecraane.melodygeneration.util.GeneNoteFactory;

import org.jgap.Gene;
import org.jgap.IChromosome;
import org.jgap.impl.CompositeGene;

import javax.swing.*;

import net.miginfocom.swing.MigLayout;

/**
 * Strategy which counts large pitch differences as errors.
 */
public final class GlobalPitchDistributionStrategy extends AbstractMelodyFitnessStrategy {
	private double pitchAdherenceThreshold = 0.85D;
	private int maximumPitchDifferenceInSemitones = 8;

    private JSlider distributionSlider;
    private JSpinner distributionSpinner;

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
        // TODO Create UI
        container.setLayout(new MigLayout());
        container.add(new JLabel("Number of major intervals"));
        distributionSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 95);
        container.add(distributionSlider, "wrap");
        container.add(new JLabel("Number of perfect intervals"));
        distributionSpinner = new JSpinner(new SpinnerNumberModel(1,0,100,1));
        container.add(distributionSpinner, "wrap");
//       label(text: "% of notes in maximum ST", constraints: 'width 263')
//	distributionSlider = slider(minimum: 0, maximum: 100, paintTicks: true, paintLabels: true, majorTickSpacing: 10, minorTickSpacing: 1, value: 95, stateChanged: {distributionLabel.text = distributionSlider.value})
//	distributionLabel = label(text: 95, constraints:'wrap')
//	label(text: "Maximum semitones")
//	distributionSpinner = spinner(model: spinnerNumberModel(minimum: 0), value: 8, constraints: 'span 2')
    }

    public String toString() {
        return "[GlobalPitchDistributionStrategy[pitchAdherenceThreshold: " + this.pitchAdherenceThreshold + ", maximumPitchDifferenceInSemitones: " + this.maximumPitchDifferenceInSemitones + "]]";
    }
}
