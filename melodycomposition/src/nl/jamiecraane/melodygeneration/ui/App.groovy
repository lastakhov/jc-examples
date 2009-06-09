/**
 * 
 */
package nl.jamiecraane.melodygeneration.ui

import groovy.swing.SwingBuilder
import java.awt.BorderLayout as BL
import java.util.Enumeration
import nl.jamiecraane.melodygeneration.Scale
import java.awt.FlowLayout
import javax.swing.BoxLayout
import java.awt.GridLayout
import net.miginfocom.swing.MigLayout
import nl.jamiecraane.melodygeneration.fitnessfunction.ProportionRestAndNotesStrategy
import nl.jamiecraane.melodygeneration.fitnessfunction.RepeatingNotesStrategy
import nl.jamiecraane.melodygeneration.fitnessfunction.GlobalPitchDistributionStrategy
import nl.jamiecraane.melodygeneration.fitnessfunction.IntervalStrategy
import nl.jamiecraane.melodygeneration.fitnessfunction.ParallelIntervalStrategy
import nl.jamiecraane.melodygeneration.fitnessfunction.MelodyFitnessFunctionBuilder
import nl.jamiecraane.melodygeneration.fitnessfunction.ScaleStrategy
import nl.jamiecraane.melodygeneration.MelodyGeneratorMain
import nl.jamiecraane.melodygeneration.fitnessfunction.MelodyFitnessFunction

def swing = new SwingBuilder()

pitchDistributionPanel = swing.panel(layout: new MigLayout()) {
	label(text: "% of notes in maximum ST", constraints: 'width 263')
	distributionSlider = slider(minimum: 0, maximum: 100, paintTicks: true, paintLabels: true, majorTickSpacing: 10, minorTickSpacing: 1, value: 95, stateChanged: {distributionLabel.text = distributionSlider.value})
	distributionLabel = label(text: 95, constraints:'wrap')
	label(text: "Maximum semitones")
	distributionSpinner = spinner(model: spinnerNumberModel(minimum: 0), value: 8, constraints: 'span 2')
}

intervalPanel = swing.panel(layout: new MigLayout()) {
	label(text: "Number of major intervals")
	majorIntervalSpinner = spinner(model: spinnerNumberModel(minimum: 0), value: 1, constraints: 'wrap')
	label(text: "Number of perfect intervals")
	perfectIntervalSpinner = spinner(model: spinnerNumberModel(minimum: 0), value: 1)
}

notesRestPanel = swing.panel(layout: new MigLayout()) {
	label(text: "% notes/rests (higher means less notes, thus more rests)", constraints: 'width 270')
	noteRestSlider = slider(minimum: 0, maximum: 100, paintTicks: true, paintLabels: true, majorTickSpacing: 10, minorTickSpacing: 1, value: 12, stateChanged: {noteRestLabel.text = noteRestSlider.value})
	noteRestLabel = label(text: "12")
}

duplicatesPanel = swing.panel(layout: new MigLayout()) {
	label(text: "Maximum consecutive duplicate notes")
	duplicateNoteSpinner = spinner(model: spinnerNumberModel(minimum: 0), value: 2, constraints: 'wrap')
	label(text: "Maximum consecutive duplicate rests")
	duplicateRestSpinner = spinner(model: spinnerNumberModel(minimum: 0), value: 2)
}

parallelIntervalPanel = swing.panel(layout: new MigLayout()) {
	label(text: "Number of good sounding parallel intervals")
	parallelIntervalSpinner = spinner(model: spinnerNumberModel(minimum: 0), value: 2)
}

generalPanel = swing.panel(layout: new MigLayout()) {
    label(text: "Number of notes")
	numberOfNotesSpinner = spinner(model: spinnerNumberModel(minimum: 0), value: 24)
    label(text: "Number of evolutions")
	numberOfEvolutionsField = textField(text: 250)
}

ScalePanel scalePanel = new ScalePanel()

def generateMelody = {
    ProportionRestAndNotesStrategy proportionRestAndNotesStrategy = new ProportionRestAndNotesStrategy();
        proportionRestAndNotesStrategy.setMaximumPercentageOfRests(noteRestSlider.value);
        RepeatingNotesStrategy repeatingNotesStrategy = new RepeatingNotesStrategy();
        repeatingNotesStrategy.setDuplicateThreshold(duplicateNoteSpinner.value);
        repeatingNotesStrategy.setDuplicateRestThreshold(duplicateRestSpinner.value);
        GlobalPitchDistributionStrategy globalPitchDistributionStrategy = new GlobalPitchDistributionStrategy();
        globalPitchDistributionStrategy.setMaximumPitchDifferenceInSemitones(distributionSpinner.value);
        globalPitchDistributionStrategy.setPitchAdherenceThreshold(distributionSlider.value / 100);
        IntervalStrategy intervalStrategy = new IntervalStrategy();
        intervalStrategy.setNumberOfMajorIntervals(majorIntervalSpinner.value);
        intervalStrategy.setNumberOfPerfectÍntervals(perfectIntervalSpinner.value);
        ParallelIntervalStrategy parallelIntervalStrategy = new ParallelIntervalStrategy();
        parallelIntervalStrategy.setNumberOfParallelIntervalsThatSoundGood(parallelIntervalSpinner.value);

        MelodyFitnessFunctionBuilder fitnessFunctionBuilder = new MelodyFitnessFunctionBuilder();
        fitnessFunctionBuilder.withScale(Scale.fromString(scalePanel.selectedScale)).addStrategy(new ScaleStrategy()).
                addStrategy(proportionRestAndNotesStrategy).addStrategy(repeatingNotesStrategy).
                addStrategy(globalPitchDistributionStrategy).addStrategy(intervalStrategy).addStrategy(parallelIntervalStrategy);
        MelodyGeneratorMain generator = new MelodyGeneratorMain();
        MelodyFitnessFunction melodyFitnessFunction = fitnessFunctionBuilder.build();
        println melodyFitnessFunction;
        generator.generateMelody(fitnessFunctionBuilder.build(), numberOfNotesSpinner.value, Integer.valueOf(numberOfEvolutionsField.text))
}

frame = swing.frame(title:"app", size:[1100,800], windowClosing: {System.exit(0)}, layout: new MigLayout()) {	
	widget(scalePanel, constraints: 'span 2, wrap')
	panel(border: titledBorder(title: "Global Pitch Distribution"), constraints: 'span 2, wrap') {
		widget(pitchDistributionPanel);
	}
	panel(border: titledBorder(title: "Percentage notes/rests"), constraints: 'span 2, wrap') {
		widget(notesRestPanel);
	}
	panel(border: titledBorder(title: "Intervals")) {
		widget(intervalPanel);
	}	
	panel(border: titledBorder(title: "Duplicate notes/rests"), constraints: 'wrap') {
		widget(duplicatesPanel);
	}
	panel(border: titledBorder(title: "Parallel intervals")) {
		widget(parallelIntervalPanel);
	}
    panel(border: titledBorder(title: "General settings"), constraints: 'wrap') {
        widget(generalPanel);
    }
    button(text:"generate",
			actionPerformed: {generateMelody()})
}
frame.show()