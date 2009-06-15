/**
 * 
 */
package nl.jamiecraane.melodygeneration.ui

import groovy.swing.SwingBuilder
import java.awt.BorderLayout as BL
import java.util.Enumeration
import nl.jamiecraane.melodygeneration.Scale
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
    label(text: "Path to save melodies (i.e c:/melodies)")
    melodyDir = textField("c:/melodies")
}

ScalePanel scalePanel = new ScalePanel()
MelodyGeneratorMain generator = new MelodyGeneratorMain();

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
        MelodyFitnessFunction melodyFitnessFunction = fitnessFunctionBuilder.build();
        println melodyFitnessFunction;
        generator.setProgressBar(myProgressBar)
        generator.generateMelody(fitnessFunctionBuilder.build(), numberOfNotesSpinner.value, Integer.valueOf(numberOfEvolutionsField.text))
}

actionPanel = swing.panel(layout: new MigLayout()) {
    generateButton = button(text: "Generate",
			actionPerformed: {
                myProgressBar.maximum = Integer.parseInt(numberOfEvolutionsField.text) - 1
                generateButton.enabled = false
                saveButton.enabled = false
                playButton.enabled = false
                doOutside {
                    generateMelody()

                    doLater {
                       saveButton.enabled = true
                       playButton.enabled = true
                       generateButton.enabled = true
                    }
                }
         })
    saveButton = button(text: "Save", enabled: false, actionPerformed: {
            doOutside {
                generator.save(melodyDir.text)
            }
        }
    )
    playButton = button(text: "Play", enabled: false, actionPerformed: {
            saveButton.enabled = false
            playButton.enabled = false
            generateButton.enabled = false
            doOutside {
                generator.play()

                doLater {
                   saveButton.enabled = true
                    playButton.enabled = true
                    generateButton.enabled = true
                }
            }
        }
    )
    myProgressBar = progressBar(minimum: 0, value: 0)
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
	panel(border: titledBorder(title: "Parallel intervals", constraints: 'wrap')) {
		widget(parallelIntervalPanel);
	}
    panel(border: titledBorder(title: "General settings"), constraints: 'wrap') {
        widget(generalPanel);
    }
    panel(border: titledBorder(title: "Actions")) {
        widget(actionPanel);
    }
}
frame.show()