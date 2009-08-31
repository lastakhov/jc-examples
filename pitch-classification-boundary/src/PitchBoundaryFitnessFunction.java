import org.jgap.FitnessFunction;
import org.jgap.IChromosome;
import org.jgap.Gene;
import org.jgap.impl.CompositeGene;

/**
 * Created by IntelliJ IDEA.
 * User: Jamie.Craane
 * Date: 31-aug-2009
 * Time: 8:54:12
 * To change this template use File | Settings | File Templates.
 */
public class PitchBoundaryFitnessFunction extends FitnessFunction {
    private double[] pitches = new double[50];
    private double min;
    private double max;

    public void setPitches(double[] pitches) {
        this.pitches = pitches;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public void setMax(double max) {
        this.max = max;
    }

    protected double evaluate(IChromosome iChromosome) {
        double errors = 0.0D;

        Double bound1 = (Double) ((CompositeGene) iChromosome.getGene(0)).geneAt(0).getAllele();
        Double bound2 = (Double) ((CompositeGene) iChromosome.getGene(0)).geneAt(1).getAllele();

        if (Double.compare(bound1, bound2) > 0) {
            errors = 25000;
        }

        int lowerBound = 0;
        int middleBound = 0;
        int upperBound = 0;
        for (int i = 0; i < pitches.length; i++) {
            double pitch = pitches[i];
            if (pitch < bound1) {
                lowerBound++;
            }

            if (pitch > bound1 && pitch < bound2) {
                middleBound++;
            }

            if (pitch > bound2) {
                upperBound++;
            }
        }

        int avg = Math.round(pitches.length / 3);
        int diff = Math.abs(lowerBound - avg);
        errors += diff * diff;
        diff = Math.abs(middleBound - avg);
        errors += diff * diff;
        diff = Math.abs(upperBound - avg);
        errors += diff * diff;
        
        return errors;
    }
}
