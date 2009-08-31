import org.jgap.*;
import org.jgap.impl.*;

import java.util.List;
import java.util.Random;
import java.util.Arrays;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Jamie.Craane
 * Date: 31-aug-2009
 * Time: 8:51:57
 * To change this template use File | Settings | File Templates.
 */
public class PitchClassificationTest {
    private double[] pitches = new double[100];
    private Random random = new Random();
    private static final double MINIMUM_VOLUME = 0.1;
	private static final double MAXIMUM_VOLUME = 5.0;
    private static final int EVOLUTIONS = 500;

    private double min = Double.MAX_VALUE;
    private double max = Double.MIN_VALUE;

    public PitchClassificationTest() throws InvalidConfigurationException {
        pitches = this.createPitches();
        Genotype genotype = this.configureJGAP();
        this.evolve(genotype);
    }

    private double[] createPitches() {
        for (int i = 0; i < pitches.length; i++) {
            pitches[i] = MINIMUM_VOLUME + (random.nextDouble() * MAXIMUM_VOLUME);
            if (pitches[i] < min) {
                min = pitches[i];
            }
            if (pitches[i] > max) {
                max = pitches[i];
            }
//            System.out.println("pitches = " + pitches[i]);
        }

        return pitches;
    }

    /**
     * Setup JGAP.
     */
    private Genotype configureJGAP() throws InvalidConfigurationException {
		Configuration gaConf = new DefaultConfiguration();
		// Here we specify a fitness evaluator where lower values means a better fitness
		Configuration.resetProperty(Configuration.PROPERTY_FITEVAL_INST);
		gaConf.setFitnessEvaluator(new DeltaFitnessEvaluator());

        // We are only interested in the most fittest individual
        gaConf.setPreservFittestIndividual(true);
		gaConf.setKeepPopulationSizeConstant(false);

		gaConf.setPopulationSize(50);
        // The number of chromosomes is the number of boxes we have. Every chromosome represents one box.
        int chromeSize = 1;
		Genotype genotype;

        CompositeGene boundaries = new CompositeGene(gaConf);
        boundaries.addGene(new DoubleGene(gaConf, min, max));
        boundaries.addGene(new DoubleGene(gaConf, min, max));
        
        // Setup the structure with which to evolve the solution of the problem.
        // An IntegerGene is used. This gene represents the index of a box in the boxes array.
		IChromosome sampleChromosome = new Chromosome(gaConf, boundaries, chromeSize);
		gaConf.setSampleChromosome(sampleChromosome);
        // Setup the fitness function
        PitchBoundaryFitnessFunction fitnessFunction = new PitchBoundaryFitnessFunction();
        fitnessFunction.setPitches(this.pitches);
        fitnessFunction.setMax(max);
        fitnessFunction.setMin(min);
        gaConf.setFitnessFunction(fitnessFunction);

		return Genotype.randomInitialGenotype(gaConf);
	}

    public void evolve(Genotype genotype) {
        for (int i = 0; i < EVOLUTIONS; i++) {
            genotype.evolve();
        }

        IChromosome fittest = genotype.getFittestChromosome();
        System.out.println("fittest.getFitnessValue() = " + fittest.getFitnessValue());
        Double bound1 = (Double) ((CompositeGene) fittest.getGene(0)).geneAt(0).getAllele();
        Double bound2 = (Double) ((CompositeGene) fittest.getGene(0)).geneAt(1).getAllele();
        System.out.println("lowerbounds = " + min + " -> " + bound1);
        System.out.println("middlebounds = " + bound1 + " -> " + bound2);
        System.out.println("upperbounds = " + bound2 + " -> " + max);
        int lowerBound = 0;
        int middleBound = 0;
        int upperBound = 0;
        List lower = new ArrayList();
        List middle = new ArrayList();
        List upper = new ArrayList();
        for (int i = 0; i < pitches.length; i++) {
            double pitch = pitches[i];
            if (pitch < bound1) {
                lowerBound++;
                lower.add(pitch);
            }

            if (pitch > bound1 && pitch < bound2) {
                middleBound++;
                middle.add(pitch);
            }

            if (pitch > bound2) {
                upperBound++;
                upper.add(pitch);
            }
        }
        for (Object o : lower) {
            System.out.print(o + ":");
        }
        System.out.println("");
        for (Object o : middle) {
            System.out.print(o + ":");
        }
        System.out.println("");
        for (Object o : upper) {
            System.out.print(o + ":");
        }
        System.out.println("");
        System.out.println("lowerBound = " + lowerBound);
        System.out.println("middleBound = " + middleBound);
        System.out.println("upperBound = " + upperBound);
    }

    public static void main(String[] args) throws InvalidConfigurationException {
        new PitchClassificationTest();
    }


}
