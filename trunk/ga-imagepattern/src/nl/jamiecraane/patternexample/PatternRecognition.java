package nl.jamiecraane.patternexample;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.DeltaFitnessEvaluator;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.impl.BooleanGene;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.ThresholdSelector;

/**
 * Example of how a GA creates an image based on a target image from random pixels.
 */
public class PatternRecognition {
	private static final int EVOLUTIONS = 10000;
    // The width of the target image (can also be calculated)
    private static final int IMAGE_WIDTH = 30;
    // The height of the target image (can also be calculated)
    private static final int IMAGE_HEIGHT = 30;

	public PatternRecognition() throws Exception {
		Genotype genotype = this.setupGenoType();
		this.evolve(genotype);
	}

	private void evolve(Genotype genotype) throws Exception {
		double previousFitness = Double.MAX_VALUE;
		for (int i = 0; i < EVOLUTIONS; i++) {
			genotype.evolve();

			double fitness = genotype.getFittestChromosome().getFitnessValue();
			if (fitness < previousFitness) {
				System.err.println(fitness);
				previousFitness = fitness;
			}

			if (i % 100 == 0) {
				System.out.println("Evolutions = " + i);
			}
		}

		this.drawImage(genotype.getFittestChromosome(), "output.png");
	}

	private void drawImage(IChromosome chromosome, String imageName) throws Exception {
		BufferedImage image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		Gene[] genes = chromosome.getGenes();

		int count = 0;
		for (int y = 0; y < IMAGE_HEIGHT; y++) {
			for (int x = 0; x < IMAGE_WIDTH; x++) {
				Gene gene = genes[count];
				boolean b = (Boolean) gene.getAllele();
				// black
				int c = 0;
				if (b) {
					c = 255;
				}
				graphics.setColor(new Color(c, c, c));
				graphics.drawRect(x, y, 1, 1);
				count++;
			}
		}

		OutputStream os = new FileOutputStream(new File(imageName));
		ImageIO.write(image, "png", os);
		os.close();
	}

	private Genotype setupGenoType() throws Exception {
		Configuration gaConf = new DefaultConfiguration();
		gaConf.resetProperty(Configuration.PROPERTY_FITEVAL_INST);
		gaConf.setFitnessEvaluator(new DeltaFitnessEvaluator());

		gaConf.setPreservFittestIndividual(true);
		gaConf.setKeepPopulationSizeConstant(false);

		gaConf.setPopulationSize(50);

		int chromeSize = IMAGE_HEIGHT * IMAGE_WIDTH;

		// Test with different selectors and mutation operators
		// gaConf.addNaturalSelector(new TournamentSelector(gaConf, 5, 0.5),
		// false);
		// ChainOfSelectors chain = gaConf.getNaturalSelectors(false);
		gaConf.addNaturalSelector(new ThresholdSelector(gaConf, 0.2D), false);

		BooleanGene gene = new BooleanGene(gaConf);

		IChromosome sampleChromosome = new Chromosome(gaConf, gene, chromeSize);
		gaConf.setSampleChromosome(sampleChromosome);

		PatternFitnessFunction fitnessFunction = new PatternFitnessFunction(IMAGE_HEIGHT, IMAGE_WIDTH);
		gaConf.setFitnessFunction(fitnessFunction);

		return Genotype.randomInitialGenotype(gaConf);
	}

	public static void main(String[] args) throws Exception {
		new PatternRecognition();
	}
}
