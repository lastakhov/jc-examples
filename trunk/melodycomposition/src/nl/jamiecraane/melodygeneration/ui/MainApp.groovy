/**
 * Main application.
 */
package nl.jamiecraane.melodygeneration.ui

import groovy.swing.SwingBuilder
import javax.swing.JPanel
import javax.swing.JTabbedPane
import net.miginfocom.swing.MigLayout
import nl.jamiecraane.melodygeneration.MelodyFitnessStrategy
import nl.jamiecraane.melodygeneration.MelodyGenerator
import nl.jamiecraane.melodygeneration.fitnessfunction.MelodyFitnessFunction
import nl.jamiecraane.melodygeneration.fitnessfunction.MelodyFitnessFunctionBuilder
import nl.jamiecraane.melodygeneration.plugin.core.PluginDiscoverer

MelodyGenerator generator = new MelodyGenerator();

def plugins = new PluginDiscoverer().getAvailablePlugins()
def swing = new SwingBuilder()
tabbedPluginPane = swing.tabbedPane(tabPlacement: JTabbedPane.LEFT) {
    plugins.each { MelodyFitnessStrategy plugin ->
        JPanel pluginPane = panel(name: plugin.name)
        plugin.init(pluginPane)
    }
}

def generateMelody = {
    MelodyFitnessFunctionBuilder fitnessFunctionBuilder = new MelodyFitnessFunctionBuilder();

    plugins.each { MelodyFitnessStrategy plugin ->
        plugin.configure()
        fitnessFunctionBuilder.addStrategy(plugin)
    }

    MelodyFitnessFunction melodyFitnessFunction = fitnessFunctionBuilder.build();
    println melodyFitnessFunction;
    generator.setProgressBar(myProgressBar)
    generator.generateMelody(fitnessFunctionBuilder.build(), numberOfNotesSpinner.value, Integer.valueOf(numberOfEvolutionsField.text))
}

generalPanel = swing.panel(layout: new MigLayout()) {
    label(text: "Number of notes")
	numberOfNotesSpinner = spinner(model: spinnerNumberModel(minimum: 0), value: 24)
    label(text: "Number of evolutions")
	numberOfEvolutionsField = textField(text: 250)
    label(text: "Path to save melodies (i.e c:/melodies)")
    melodyDir = textField("c:/melodies")
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

frame = swing.frame(title: "app", size: [1000, 800], windowClosing: {System.exit(0)}, layout: new MigLayout()) {
    widget(tabbedPluginPane, constraints: 'wrap')
    panel(border: titledBorder(title: "General settings"), constraints: 'wrap') {
        widget(generalPanel);
    }
    panel(border: titledBorder(title: "Actions")) {
        widget(actionPanel);
    }
}
frame.show()