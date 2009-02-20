package nl.jamiecraane.patternexample;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
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
 * Example of how one would implement pattern recognition with GA. A target pattern, pattern.gif,
 * is read and the colors of each pixels are converted to black/white and stored in a byte[]. true is white, false is black.
 * Then a population of BooleanGene is evoluted to match the target pattern.
 */
public class PatternRecognition {
	private static final int EVOLUTIONS = 10000;
	private static final int OUTPUT_IMAGE_SCALE_FACTOR = 4;
	
	private int imageWidth;
	private int imageHeight;
	private boolean[] targetPattern;
	
	private ImageDisplay imageDisplay;
	
	public PatternRecognition(ImageDisplay imageDisplay) throws Exception {
		this.imageDisplay = imageDisplay;
		this.initTargetImage();		
	}
	
	public void start() throws Exception {
		Genotype genotype = this.setupGenoType();
		this.evolve(genotype);
	}
	
	private void initTargetImage() throws Exception {
		InputStream is = null;
		
		try {
			is = this.getClass().getResourceAsStream("pattern.gif");
			BufferedImage targetImage = ImageIO.read(is);
			PixelGrabber grabber = new PixelGrabber(targetImage, 0, 0, -1, -1, true);
			if (grabber.grabPixels()) {
				
			}
			this.imageHeight = targetImage.getHeight();
			this.imageWidth = targetImage.getWidth();
			int[] pixels = (int[]) grabber.getPixels();
			this.targetPattern = this.getTargetColors(pixels);
		} finally {
			is.close();
		}
	}
	
	private boolean[] getTargetColors(int[] pixels) {
		boolean[] targetColors = new boolean[pixels.length];
		for (int i = 0; i < pixels.length; i++) {
			int color = (pixels[i] & 0x00ff0000) >> 16;
			color += (pixels[i] & 0x0000ff00) >> 8;
			color += (pixels[i] & 0x000000ff);
			targetColors[i] = ((int) Math.round(color / 3)) < 128 ? false : true;
		}
		return targetColors;
	}

	private void evolve(Genotype genotype) throws Exception {
		double previousFitness = Double.MAX_VALUE;
		for (int i = 0; i < EVOLUTIONS; i++) {
			genotype.evolve();

			double fitness = genotype.getFittestChromosome().getFitnessValue();
			if (fitness < previousFitness) {
				System.err.println(fitness);
				this.drawImage(genotype.getFittestChromosome(), "image" + i + ".png");
				previousFitness = fitness;
			}

			if (i % 100 == 0) {
				System.out.println("Evolutions = " + i);
			}
		}

		this.drawImage(genotype.getFittestChromosome(), "final.png");
	}

	private void drawImage(IChromosome chromosome, String imageName) throws Exception {
		BufferedImage image = new BufferedImage(this.imageWidth, this.imageHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = image.createGraphics();
		Gene[] genes = chromosome.getGenes();

		int count = 0;
		for (int y = 0; y < this.imageHeight; y++) {
			for (int x = 0; x < this.imageWidth; x++) {
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

		int width = this.imageWidth * OUTPUT_IMAGE_SCALE_FACTOR;
		int height = this.imageHeight * OUTPUT_IMAGE_SCALE_FACTOR;
		BufferedImage bdest = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		AffineTransform at = AffineTransform.getScaleInstance((double) width / image.getWidth(), (double) height
				/ image.getHeight());
		Graphics2D g = bdest.createGraphics();
		g.drawRenderedImage(image, at);
//		OutputStream os = new FileOutputStream(new File(imageName));
		this.imageDisplay.imageChanged(bdest);
//		ImageIO.write(bdest, "png", os);
//		os.close();
	}

	private Genotype setupGenoType() throws Exception {
		Configuration gaConf = new DefaultConfiguration();
		gaConf.resetProperty(Configuration.PROPERTY_FITEVAL_INST);
		gaConf.setFitnessEvaluator(new DeltaFitnessEvaluator());

		gaConf.setPreservFittestIndividual(true);
		gaConf.setKeepPopulationSizeConstant(false);

		gaConf.setPopulationSize(50);

		int chromeSize = this.imageHeight * this.imageWidth;

		gaConf.addNaturalSelector(new ThresholdSelector(gaConf, 0.3D), false);

		BooleanGene gene = new BooleanGene(gaConf);

		IChromosome sampleChromosome = new Chromosome(gaConf, gene, chromeSize);
		gaConf.setSampleChromosome(sampleChromosome);
		
		PatternFitnessFunction fitnessFunction = new PatternFitnessFunction(this.targetPattern);
		
		gaConf.setFitnessFunction(fitnessFunction);

		Genotype genotype = Genotype.randomInitialGenotype(gaConf);

		return genotype;
	}
}
