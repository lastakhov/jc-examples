package nl.jamiecraane.melodygeneration;

import nl.jamiecraane.melodygeneration.fitnessfunction.MelodyFitnessFunction;
import nl.jamiecraane.melodygeneration.util.MidiDataOutputStream;
import nl.jamiecraane.melodygeneration.util.MidiFileWriter;
import nl.jamiecraane.melodygeneration.util.MidiGeneHelper;
import org.apache.log4j.Logger;
import org.jgap.*;
import org.jgap.impl.CompositeGene;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.IntegerGene;

import javax.sound.midi.*;
import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MelodyGenerator {
    private static final Logger LOG = Logger.getLogger(MelodyGenerator.class);
    private static final int MINIMUM_OCTAVE = 4;
    private static final int MAXIMUM_OCTAVE = 7;
    private JProgressBar progressBar;
    private IChromosome fittestChromosome;

    public void setProgressBar(JProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public MelodyGenerator() {
    }

    /**
     * Called by the UI.
     */
    public void generateMelody(MelodyFitnessFunction melodyFitnessFunction, int numberOfNotes, int evolutions) throws Exception {
        Genotype genotype = this.setupGenoType(melodyFitnessFunction, numberOfNotes);
        this.evolve(genotype, evolutions);
    }

//    public void generateMelody() throws Exception {
//        Genotype genotype = this.setupGenoType();
//        this.evolve(genotype, NUMBER_OF_EVOLUTIONS);
//    }

    private Genotype setupGenoType(MelodyFitnessFunction melodyFitnessFunction, int numberOfNotes) throws Exception {
        Configuration.reset();
        Configuration gaConf = new DefaultConfiguration();
        gaConf.resetProperty(Configuration.PROPERTY_FITEVAL_INST);
        gaConf.setFitnessEvaluator(new DeltaFitnessEvaluator());

        gaConf.setPreservFittestIndividual(true);
        gaConf.setKeepPopulationSizeConstant(false);

        gaConf.setPopulationSize(40);

        CompositeGene gene = new CompositeGene(gaConf);
        // Add the pitch gene
        gene.addGene(new IntegerGene(gaConf, 0, 12), false);
        // Add the octave gene
        gene.addGene(new IntegerGene(gaConf, MINIMUM_OCTAVE, MAXIMUM_OCTAVE), false);
        // Add the length (from 3 - 5 is from quarter to sixteenth)
        gene.addGene(new IntegerGene(gaConf, 3, 4), false);

        // A size of 16 represent 16 notes
        IChromosome sampleChromosome = new Chromosome(gaConf, gene, numberOfNotes);
        gaConf.setSampleChromosome(sampleChromosome);

        gaConf.setFitnessFunction(melodyFitnessFunction);

        return Genotype.randomInitialGenotype(gaConf);
    }

//    private Genotype setupGenoType() throws Exception {
//        ProportionRestAndNotesStrategy proportionRestAndNotesStrategy = new ProportionRestAndNotesStrategy();
//        proportionRestAndNotesStrategy.setMaximumPercentageOfRests(6.75D);
//        RepeatingNotesStrategy repeatingNotesStrategy = new RepeatingNotesStrategy();
//        repeatingNotesStrategy.setDuplicateThreshold(2);
//        GlobalPitchDistributionStrategy globalPitchDistributionStrategy = new GlobalPitchDistributionStrategy();
//        globalPitchDistributionStrategy.setMaximumPitchDifferenceInSemitones(12);
//        globalPitchDistributionStrategy.setPitchAdherenceThreshold(0.95D);
//        IntervalStrategy intervalStrategy = new IntervalStrategy();
//        intervalStrategy.setNumberOfMajorIntervals(1);
//        intervalStrategy.setNumberOfPerfectÍntervals(1);
//        ParallelIntervalStrategy parallelIntervalStrategy = new ParallelIntervalStrategy();
//        parallelIntervalStrategy.setNumberOfParallelIntervalsThatSoundGood(2);
//
//        MelodyFitnessFunctionBuilder fitnessFunctionBuilder = new MelodyFitnessFunctionBuilder();
//        fitnessFunctionBuilder.withScale(Scale.C_MAJOR).addStrategy(new ScaleStrategy()).
//                addStrategy(proportionRestAndNotesStrategy).addStrategy(repeatingNotesStrategy).
//                addStrategy(globalPitchDistributionStrategy).addStrategy(intervalStrategy).addStrategy(parallelIntervalStrategy);
//        return this.setupGenoType(fitnessFunctionBuilder.build(), NUMBER_OF_NOTES);
//    }

    public void play() throws InvalidMidiDataException, MidiUnavailableException {
        Sequence sequence = generateMidiSequence();
        this.playSequence(sequence);
    }

    public void save(String path) {
        Sequence sequence = null;
        try {
            sequence = generateMidiSequence();
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        this.writeSequenceToMidiFile(sequence, path);
    }

    private Sequence generateMidiSequence() throws InvalidMidiDataException {
        Sequence sequence = new Sequence(Sequence.PPQ, 4);
        Track track = sequence.createTrack();

        long ticks = 0;
        for (Gene gene : this.fittestChromosome.getGenes()) {
            CompositeGene note = (CompositeGene) gene;
            Duration duration = Duration.getByIndex((Integer) note.geneAt(2).getAllele());
            ticks += duration.getTicks();
            track.add(new MidiEvent(MidiGeneHelper.toMidiMessage(note), ticks));
            track.add(new MidiEvent(MidiGeneHelper.noteOffMidiMessage(), 0));
        }
        // Because the last note is not played for some reason, we add it again as a workaround
        this.addLastNote(this.fittestChromosome, track, ticks);
        return sequence;
    }


    private void evolve(Genotype genotype, int evolutions) {
        for (int i = 0; i < evolutions; i++) {
            this.progressBar.setValue(i);
            genotype.evolve();
        }
        this.fittestChromosome = genotype.getFittestChromosome();
        this.printSolution(genotype.getFittestChromosome());
        try {
            this.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printSolution(IChromosome chromosome) {
        System.out.println("Fitness of melody = " + chromosome.getFitnessValue());
        for (Gene gene : chromosome.getGenes()) {
            CompositeGene note = (CompositeGene) gene;
            IntegerGene pitch = (IntegerGene) note.geneAt(0);
            System.out.print("[" + Pitch.getByIndex((Integer) (pitch.getAllele())) + ":");
            IntegerGene octave = (IntegerGene) note.geneAt(1);
            System.out.print(octave.getAllele() + ":");
            IntegerGene length = (IntegerGene) note.geneAt(2);
            System.out.println(Duration.getByIndex((Integer) length.getAllele()) + "]");
        }
    }

    private void playSequence(Sequence sequence) throws InvalidMidiDataException, MidiUnavailableException {
        Sequencer sequencer = null;

        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequencer.getTransmitter().setReceiver(MidiSystem.getReceiver());
            sequencer.setSequence(sequence);
            sequencer.start();
            while (true) {
                if (sequencer.isRunning()) {
                    try {
                        Thread.sleep(1000); // Check every second
                    } catch (InterruptedException ignore) {
                        break;
                    }
                } else {
                    break;
                }
            }
        } finally {
            if (sequencer != null) {
                // Close the MidiDevice & free resources
                sequencer.stop();
                sequencer.close();
            }
        }
    }

    private void addLastNote(IChromosome chromosome, Track track, long ticks) throws InvalidMidiDataException {
        CompositeGene note = (CompositeGene) chromosome.getGene(chromosome.getGenes().length - 1);
        Duration duration = Duration.getByIndex((Integer) note.geneAt(2).getAllele());
        ticks += duration.getTicks();
        track.add(new MidiEvent(MidiGeneHelper.toMidiMessage(note), ticks));
        track.add(new MidiEvent(MidiGeneHelper.noteOffMidiMessage(), 0));
    }

    private void writeSequenceToMidiFile(Sequence sequence, String path) {
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(new File(path + "/generated-melody-" + System.currentTimeMillis() + ".mid"));
            MidiDataOutputStream dos = new MidiDataOutputStream(os);
            MidiFileWriter midiFileWriter = new MidiFileWriter();
            midiFileWriter.write(sequence, 0, os);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                LOG.error("Unable to close OutputStream", e);
            }
        }
    }

//    public static void main(String[] args) throws Exception {
//        new MelodyGenerator().generateMelody();
//    }
}
