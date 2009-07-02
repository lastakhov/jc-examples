package nl.jamiecraane.melodygeneration.fitnessfunction;

import nl.jamiecraane.melodygeneration.fitnessfunction.MelodyFitnessFunction;
import nl.jamiecraane.melodygeneration.Scale;
import nl.jamiecraane.melodygeneration.MelodyFitnessStrategy;

import java.util.Map;
import java.util.HashMap;

import org.apache.log4j.Logger;

/**
 * Builder for a MelodyFitnessFunction.
 */
public final class MelodyFitnessFunctionBuilder {
    public static final Logger LOG = Logger.getLogger(MelodyFitnessFunctionBuilder.class);
    private Scale scale;
    private Map<Class, MelodyFitnessStrategy> strategies = new HashMap<Class, MelodyFitnessStrategy>();

    public MelodyFitnessFunctionBuilder addStrategy(MelodyFitnessStrategy melodyFitnessStrategy) {
        if (melodyFitnessStrategy == null) {
            throw new IllegalArgumentException("melodyFitnessStrategy may nog be null");
        }

        if (this.strategies.put(melodyFitnessStrategy.getClass(), melodyFitnessStrategy) != null) {
            LOG.info(melodyFitnessStrategy.getClass().getName() + " already exists as a strategy. The new strategy is used in place of the old one.");            
        }

        return this;
    }

    public MelodyFitnessFunctionBuilder withScale(Scale scale) {
        this.scale = scale;
        return this;
    }

    public MelodyFitnessFunction build() {
        if (this.scale == null) {
            throw new IllegalStateException("Scale may not be null");
        }

        if (this.strategies.isEmpty()) {
            throw new IllegalStateException("At least one strategy must be added to compute the fitness value of a melody");
        }

        MelodyFitnessFunction melodyFitnessFunction = new MelodyFitnessFunction();
        for (MelodyFitnessStrategy strategy : this.strategies.values()) {
            strategy.setScale(this.scale);
            melodyFitnessFunction.addFitnessStrategy(strategy);
        }

        return melodyFitnessFunction;
    }
}
