package nl.jamiecraane.melodygeneration.plugins;

import org.jgap.IChromosome;
import nl.jamiecraane.melodygeneration.AbstractMelodyFitnessStrategy;

import javax.swing.*;

/**
 * Author: Jamie Craane
 * Date: 2-jun-2009
 * Time: 16:47:20
 */
public class QuarterBeatAdherenceStrategy extends AbstractMelodyFitnessStrategy {
    @Override
    public double calculateErrors(IChromosome melody) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void init(JPanel container) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
