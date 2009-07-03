package nl.jamiecraane.melodygeneration.plugin.core;

import nl.jamiecraane.melodygeneration.MelodyFitnessStrategy;
import nl.jamiecraane.melodygeneration.plugins.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Jamie Craane
 * Date: 2-jul-2009
 * Time: 14:03:22
 */
public class PluginDiscoverer {
    public PluginDiscoverer() {
    }

    public List<MelodyFitnessStrategy> getAvailablePlugins() {
        List<MelodyFitnessStrategy> plugins = new ArrayList<MelodyFitnessStrategy>();

        plugins.add(new IntervalStrategy());
        plugins.add(new GlobalPitchDistributionStrategy());
        plugins.add(new ParallelIntervalStrategy());
        plugins.add(new ProportionRestAndNotesStrategy());
        plugins.add(new RepeatingNotesStrategy());

        return plugins;
    }
}
