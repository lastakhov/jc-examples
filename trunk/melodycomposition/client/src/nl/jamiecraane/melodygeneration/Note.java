package nl.jamiecraane.melodygeneration;

import java.util.HashMap;
import java.util.Map;

/**
 * Immutable class representing a musical note containing a pitch and an octave in which the note is played.
 */
public final class Note {
    public static final int MIN_OCTAVE = 0;
    public static final int MAX_OCTAVE = 10;
    private static final Map<Key, Note> noteCache = new HashMap<Key, Note>();
    private final Pitch pitch;
    private final int octave;

    static {
        for (int octave = MIN_OCTAVE; octave < MAX_OCTAVE; octave++) {
            for (Pitch pitch : Pitch.values()) {
                noteCache.put(new Key(pitch, octave), new Note(pitch, octave));
            }
        }
    }

    private Note(Pitch pitch, int octave) {
        this.pitch = pitch;
        this.octave = octave;
    }

    /**
     * Returns note instances based on the FlyWeight pattern, see GOF.
     * @param pitch pitch used to create the note
     * @param octave octave used for the giuven note
     * @return Note instance for the given pitch and octave
     */
    public static Note createNote(Pitch pitch, int octave) {
        if (octave < MIN_OCTAVE || octave > MAX_OCTAVE) {
            throw new IllegalArgumentException("Octave must be between " + MIN_OCTAVE + " and " + MAX_OCTAVE +
            ".");
        }
        // Since all notes are created in a static initialiser, no need to lazy-load and lock access to the cache
        return noteCache.get(new Key(pitch, octave));
    }

    /**
     * @return The MIDI number representing this note, from 0..127.
     */
    public int toMidiNoteNumber() {
        return this.pitch.toMidiNoteNumber(this.octave);
    }

    public boolean isRest() {
        return Pitch.REST == this.pitch;
    }

    public Note getNextSemitone() {
        if (upOneOctave()) {
            if (this.octave == MAX_OCTAVE) {
                throw new IllegalStateException("Maximum octave reached");
            }
            return noteCache.get(new Key(this.pitch.getNextSemitone(), this.octave+1));
        } else {
            return noteCache.get(new Key(this.pitch.getNextSemitone(), this.octave));
        }
	}

    private boolean upOneOctave() {
        return Pitch.B == this.pitch;
    }

    public Note getPreviousSemitone() {
        if (downOneOctave()) {
            if (this.octave == MAX_OCTAVE) {
                throw new IllegalStateException("Minimum octave reached");
            }
            return noteCache.get(new Key(this.pitch.getPreviousSemitone(), this.octave-1));
        } else {
            return noteCache.get(new Key(this.pitch.getPreviousSemitone(), this.octave));
        }
	}

    private boolean downOneOctave() {
        return Pitch.C == this.pitch;
    }

    public boolean higherThan(Note other) {
        return this.pitch.higherThan(other.pitch) &&
                this.octave > other.octave;
    }

    public int getDifferenceInSemiTones(Note other) {
        int differenceInSemiTones = this.pitch.getDifferenceInSemiTones(other.pitch);
        differenceInSemiTones += Math.abs(this.octave - other.octave) * 12;
        return differenceInSemiTones;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[Note: [pitch: ").append(this.pitch).append("]");
        builder.append(",[octave: ").append(this.octave).append("]]");
        return builder.toString();
    }

    public Pitch getPitch() {
        return pitch;
    }

    public int getOctave() {
        return octave;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Note note = (Note) o;

        return octave == note.octave && pitch == note.pitch;

        }

    public int hashCode() {
        int result;
        result = (pitch != null ? pitch.hashCode() : 0);
        result = 31 * result + octave;
        return result;
    }

    /**
     * Immutable key class.
     */
    private static class Key {
        private Pitch pitch;
        private int octave;

        public Key(Pitch pitch, int octave) {
            this.pitch = pitch;
            this.octave = octave;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Key key = (Key) o;

            return octave == key.octave && pitch == key.pitch;

            }

        public int hashCode() {
            int result;
            result = (pitch != null ? pitch.hashCode() : 0);
            result = 31 * result + octave;
            return result;
        }
    }
}

