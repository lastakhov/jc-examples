package nl.jamiecraane.gahelloworld;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.DeltaFitnessEvaluator;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.StringGene;

/**
 * Example genetic algorithms program where the goal is to find a solution
 * that matches the target String. The chromosomes are implemented with StringGene's
 * where each value of the StringGene matches one character. The number of genes is
 * the same as the number of characters in the target String.
 */
public class GaHelloWorld {
	private static final String TARGET = "Hello World from a Genetic Algorithms program!";
	private static final int EVOLUTIONS = 3000;

	public GaHelloWorld() throws Exception {
		Genotype genotype = this.setupGenoType();
		this.evolve(genotype);
	}
	
	private void evolve(Genotype genotype) {
		String solution = this.getSolution(genotype.getFittestChromosome());
		System.out.println(solution);
		
		double previousFitness = Double.MAX_VALUE;
		int numEvolutions = 0;
		for (int i = 0; i < EVOLUTIONS; i++) {
			genotype.evolve();
			double fitness = genotype.getFittestChromosome().getFitnessValue();
			if (fitness < previousFitness) {
				previousFitness = fitness;
				solution = this.getSolution(genotype.getFittestChromosome());solution = this.getSolution(genotype.getFittestChromosome());
				System.out.println(solution);
			}
			
			if (solution.equals(TARGET)) {
				numEvolutions = i;
				break;
			}
			
			try {
				Thread.sleep(25);
			} catch (InterruptedException e) {
			}
		}
		
		solution = this.getSolution(genotype.getFittestChromosome());solution = this.getSolution(genotype.getFittestChromosome());		
		System.out.println(solution);
		System.out.println("Needed [" + numEvolutions + "] evolutions for this");
	}
	
	private String getSolution(IChromosome a_subject) {
		StringBuffer solution = new StringBuffer();
		
		Gene[] genes = a_subject.getGenes();
		for (int i = 0; i < genes.length; i++) {
			String allele = (String) genes[i].getAllele();
			solution.append(allele);
		}
		
		return solution.toString();
	}
	
	private Genotype setupGenoType() throws Exception {
		Configuration gaConf = new DefaultConfiguration();
		gaConf.resetProperty(Configuration.PROPERTY_FITEVAL_INST);
		gaConf.setFitnessEvaluator(new DeltaFitnessEvaluator());

		gaConf.setPreservFittestIndividual(true);
		gaConf.setKeepPopulationSizeConstant(false);

		gaConf.setPopulationSize(50);

		int chromeSize = TARGET.length();

		StringGene gene = new StringGene(gaConf);
		gene.setMaxLength(1);
		gene.setMinLength(1);
		gene.setAlphabet(StringGene.ALPHABET_CHARACTERS_LOWER+StringGene.ALPHABET_CHARACTERS_UPPER+" !");

		IChromosome sampleChromosome = new Chromosome(gaConf, gene, chromeSize);
		gaConf.setSampleChromosome(sampleChromosome);

		HelloWorldFitnessFunction fitnessFunction = new HelloWorldFitnessFunction();
		fitnessFunction.setTarget(TARGET);
		gaConf.setFitnessFunction(fitnessFunction);

		return Genotype.randomInitialGenotype(gaConf);
	}
	
	public static void main(String[] args) throws Exception {
		new GaHelloWorld();
	}
}
