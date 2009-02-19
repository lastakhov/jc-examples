package nl.jamiecraane.patternexample;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.IChromosome;

public class PatternFitnessFunction extends FitnessFunction {
	private int imageHeight;
	private int imageWidth;

	private static boolean[] targetColors;

	public PatternFitnessFunction(int imageHeight, int imageWidth) throws Exception {
		super();
		this.imageHeight = imageHeight;
		this.imageWidth = imageWidth;
		if (targetColors == null) {
			this.initTargetImage();
		}
	}

	private void initTargetImage() throws Exception {
		InputStream is = this.getClass().getResourceAsStream("pattern.gif");
		BufferedImage targetImage = ImageIO.read(is);
		PixelGrabber grabber = new PixelGrabber(targetImage, 0, 0, -1, -1, true);
		if (grabber.grabPixels()) {
			
		}
		Object p = grabber.getPixels();
		int[] pixels = (int[]) grabber.getPixels();
		this.getTargetColors(pixels);
		
		BufferedImage image = new BufferedImage(this.imageWidth, this.imageHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		int count = 0;
		for (int y = 0; y < this.imageHeight; y++) {
			for (int x = 0; x < this.imageWidth; x++) {
				boolean b = this.targetColors[count];
				// black
				int c = 0;
				if (b) {
					c = 255;
				} 
				graphics.setColor(new Color(c,c,c));
				graphics.drawRect(x, y, 1, 1);
				count++;
			}
		}

		OutputStream os = new FileOutputStream(new File("target.png"));
		ImageIO.write(image, "png", os);
		os.close();
		
		is.close();
	}

	private boolean[] getTargetColors(int[] pixels) {
		this.targetColors = new boolean[pixels.length];
		for (int i = 0; i < pixels.length; i++) {
			int color = (pixels[i] & 0x00ff0000) >> 16;
			color += (pixels[i] & 0x0000ff00) >> 8;
			color += (pixels[i] & 0x000000ff);
			this.targetColors[i] = ((int) Math.round(color / 3)) < 128 ? false : true;
		}
		return this.targetColors;
	}

	@Override
	/*
	 * Generate gradient.
	 */
	protected double evaluate(IChromosome a_subject) {
		double errors = 0;
		
		Gene[] genes = a_subject.getGenes();
		for (int i = 0; i < genes.length; i++) {
			Gene gene = genes[i];
			boolean allele = (Boolean) gene.getAllele();
			if (!allele == this.targetColors[i]) {
				errors += 1;
			}
		}
		
		return errors * errors;
	}

}
