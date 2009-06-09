package nl.jamiecraane.melodygeneration.util;

import org.jgap.impl.CompositeGene;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.InvalidMidiDataException;

import nl.jamiecraane.melodygeneration.Pitch;
import nl.jamiecraane.melodygeneration.Duration;

public final class MidiGeneHelper {
    private MidiGeneHelper() {
    }

    public static MidiMessage toMidiMessage(CompositeGene gene) throws InvalidMidiDataException {
        Pitch pitch = Pitch.getByIndex((Integer) gene.geneAt(0).getAllele());
        int octave = (Integer) gene.geneAt(1).getAllele();
        Duration duration = Duration.getByIndex((Integer) gene.geneAt(2).getAllele());
        ShortMessage midiMessage = new ShortMessage();
        int velocity = Pitch.REST != pitch ? 127 : 0;
        midiMessage.setMessage(ShortMessage.NOTE_ON, 1, pitch.toMidiNoteNumber(octave), velocity);
        return midiMessage;
    }

    public static MidiMessage noteOffMidiMessage() throws InvalidMidiDataException {
        ShortMessage midiMessage = new ShortMessage();
        midiMessage.setMessage(ShortMessage.NOTE_OFF, 1, 0, 0);
        return midiMessage;
    }
}
