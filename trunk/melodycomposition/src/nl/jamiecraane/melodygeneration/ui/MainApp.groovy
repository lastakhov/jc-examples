/**
 *
 */
package nl.jamiecraane.melodygeneration.ui

import groovy.swing.SwingBuilder
import java.awt.BorderLayout as BL
import java.util.Enumeration
import nl.jamiecraane.melodygeneration.Scale
import net.miginfocom.swing.MigLayout
import nl.jamiecraane.melodygeneration.plugins.ProportionRestAndNotesStrategy
import nl.jamiecraane.melodygeneration.plugins.RepeatingNotesStrategy
import nl.jamiecraane.melodygeneration.plugins.GlobalPitchDistributionStrategy
import nl.jamiecraane.melodygeneration.plugins.IntervalStrategy
import nl.jamiecraane.melodygeneration.plugins.ParallelIntervalStrategy
import nl.jamiecraane.melodygeneration.fitnessfunction.MelodyFitnessFunctionBuilder
import nl.jamiecraane.melodygeneration.plugins.ScaleStrategy
import nl.jamiecraane.melodygeneration.MelodyGenerator
import nl.jamiecraane.melodygeneration.fitnessfunction.MelodyFitnessFunction
import nl.jamiecraane.melodygeneration.plugin.core.PluginDiscoverer
import nl.jamiecraane.melodygeneration.MelodyFitnessStrategy
import javax.swing.JPanel
import javax.swing.JTabbedPane

MelodyGenerator generator = new MelodyGenerator();

def generateMelody = {
    nl.jamiecraane.melodygeneration.plugins.ProportionRestAndNotesStrategy proportionRestAndNotesStrategy = new nl.jamiecraane.melodygeneration.plugins.ProportionRestAndNotesStrategy();
    proportionRestAndNotesStrategy.setMaximumPercentageOfRests(noteRestSlider.value);
    nl.jamiecraane.melodygeneration.plugins.RepeatingNotesStrategy repeatingNotesStrategy = new nl.jamiecraane.melodygeneration.plugins.RepeatingNotesStrategy();
    repeatingNotesStrategy.setDuplicateThreshold(duplicateNoteSpinner.value);
    repeatingNotesStrategy.setDuplicateRestThreshold(duplicateRestSpinner.value);
    nl.jamiecraane.melodygeneration.plugins.GlobalPitchDistributionStrategy globalPitchDistributionStrategy = new nl.jamiecraane.melodygeneration.plugins.GlobalPitchDistributionStrategy();
    globalPitchDistributionStrategy.setMaximumPitchDifferenceInSemitones(distributionSpinner.value);
    globalPitchDistributionStrategy.setPitchAdherenceThreshold(distributionSlider.value / 100);
//        IntervalStrategy intervalStrategy = new IntervalStrategy();
//        intervalStrategy.setNumberOfMajorIntervals(majorIntervalSpinner.value);
//        intervalStrategy.setNumberOfPerfectÍntervals(perfectIntervalSpinner.value);
    intervalStrategy.configure()
    nl.jamiecraane.melodygeneration.plugins.ParallelIntervalStrategy parallelIntervalStrategy = new nl.jamiecraane.melodygeneration.plugins.ParallelIntervalStrategy();
    parallelIntervalStrategy.setNumberOfParallelIntervalsThatSoundGood(parallelIntervalSpinner.value);

    MelodyFitnessFunctionBuilder fitnessFunctionBuilder = new MelodyFitnessFunctionBuilder();
    fitnessFunctionBuilder.withScale(Scale.fromString(scalePanel.selectedScale)).addStrategy(new nl.jamiecraane.melodygeneration.plugins.ScaleStrategy()).
            addStrategy(proportionRestAndNotesStrategy).addStrategy(repeatingNotesStrategy).
            addStrategy(globalPitchDistributionStrategy).addStrategy(intervalStrategy).addStrategy(parallelIntervalStrategy);
    MelodyFitnessFunction melodyFitnessFunction = fitnessFunctionBuilder.build();
    println melodyFitnessFunction;
    generator.setProgressBar(myProgressBar)
    generator.generateMelody(fitnessFunctionBuilder.build(), numberOfNotesSpinner.value, Integer.valueOf(numberOfEvolutionsField.text))
}

def swing = new SwingBuilder()

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

def plugins = new PluginDiscoverer().getAvailablePlugins()
tabbedPluginPane = swing.tabbedPane(tabPlacement: JTabbedPane.LEFT) {
    plugins.each { MelodyFitnessStrategy plugin ->
        JPanel pluginPane = panel()
        plugin.init(pluginPane)
    }
}

frame = swing.frame(title: "app", size: [600, 800], windowClosing: {System.exit(0)}, layout: new MigLayout()) {
    widget(tabbedPluginPane, constraints: 'wrap')
    panel(border: titledBorder(title: "Actions")) {
        widget(actionPanel);
    }
}
frame.show()