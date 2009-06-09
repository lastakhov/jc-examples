package nl.jamiecraane.melodygeneration;

import java.util.*;

/**
 * Enum representing the different scales in music.
 */
public enum Scale {
	C_MAJOR(Pitch.C, ScaleType.MAJOR), C_SHARP_MAJOR(Pitch.C_SHARP, ScaleType.MAJOR), D_MAJOR(Pitch.D, ScaleType.MAJOR), D_SHARP_MAJOR(Pitch.D_SHARP, ScaleType.MAJOR), E_MAJOR(Pitch.E, ScaleType.MAJOR), F_MAJOR(Pitch.F, ScaleType.MAJOR), F_SHARP_MAJOR(Pitch.F_SHARP, ScaleType.MAJOR),G_MAJOR(Pitch.G, ScaleType.MAJOR), G_SHARP_MAJOR(Pitch.G_SHARP, ScaleType.MAJOR), A_MAJOR(Pitch.A, ScaleType.MAJOR), A_SHARP_MAJOR(Pitch.A_SHARP, ScaleType.MAJOR), B_MAJOR(Pitch.B, ScaleType.MAJOR),
    C_MINOR(Pitch.C, ScaleType.MINOR), C_SHARP_MINOR(Pitch.C_SHARP, ScaleType.MINOR), D_MINOR(Pitch.D, ScaleType.MINOR), D_SHARP_MINOR(Pitch.D_SHARP, ScaleType.MINOR), E_MINOR(Pitch.E, ScaleType.MINOR), F_MINOR(Pitch.F, ScaleType.MINOR), F_SHARP_MINOR(Pitch.F_SHARP, ScaleType.MINOR),G_MINOR(Pitch.G, ScaleType.MINOR), G_SHARP_MINOR(Pitch.G_SHARP, ScaleType.MINOR), A_MINOR(Pitch.A, ScaleType.MINOR), A_SHARP_MINOR(Pitch.A_SHARP, ScaleType.MINOR), B_MINOR(Pitch.B, ScaleType.MINOR);

    /**
     * Helper enum for specifying the scale type used to measure the notes in a scale.
     */
    private static enum ScaleType {
        MAJOR, MINOR, MELODIC_MINOR, HARMONIC_MINOR;
    }

    /**
	 * Represents the different intervals in a given scale.
	 * TODO Add support for octaves and unisons
	 */
	public static enum Interval {
		SECOND(2), THIRD(4), FOURTH(5), FIFTH(7), SIXTH(9), SEVENTH(11), OCTAVE(12), UNDEFINED(-1);
		private static Map<Integer, Interval> semitonesToIntervalMapping = new HashMap<Integer, Interval>();

		static {
			for (Interval interval : Interval.values()) {
				semitonesToIntervalMapping.put(interval.distanceInSemitones, interval);
			}
		}
		private int distanceInSemitones;

		private Interval(int distanceInSemitones) {
			this.distanceInSemitones = distanceInSemitones;
		}

		public static Interval getInterval(int semitones) {
			return semitonesToIntervalMapping.get(semitones);
		}
	}

    private static EnumSet<Interval> majorIntervals;
	private static EnumSet<Interval> perfectIntervals;

    private static Map<String, Scale> stringToEnumMapping = new HashMap<String, Scale>();

    private final Pitch root;

    private ScaleType scaleType;
	
    private final List<Pitch> notesInScale = new ArrayList<Pitch>();

    static {
		majorIntervals = EnumSet.of(Interval.SECOND, Interval.THIRD, Interval.SIXTH, Interval.SEVENTH);
		perfectIntervals = EnumSet.of(Interval.FOURTH, Interval.FIFTH);
        for (Scale scale : Scale.values()) {
            stringToEnumMapping.put(scale.toString(), scale);
        }
    }

    private Scale(Pitch startPitch, ScaleType scaleType) {
		this.root = startPitch;
        this.scaleType = scaleType;
        this.notesInScale.add(startPitch);
		Pitch currentPitch = this.root;
        int[] scalePattern = getScalePattern(scaleType);
        for (int i = 0; i < scalePattern.length; i++) {
			for (int semitones = 0; semitones < scalePattern[i]; semitones++) {
				currentPitch = currentPitch.getNextSemitone();
			}
			this.notesInScale.add(currentPitch);
		}
    }

    private static int[] getScalePattern(ScaleType scaleType) {
        if (scaleType == ScaleType.MAJOR) {
            return Constants.MAJOR_SCALE_PATTERN;
        } else {
            return Constants.MINOR_SCALE_PATTERN;
        }
    }

    /**
	 * @return The root note of the fiven scale.
	 */
	public Pitch getRoot() {
		return this.root;
	}
	
	public List<Pitch> getNotesInScale() {
		return Collections.unmodifiableList(this.notesInScale);
	}

	public boolean contains(Pitch pitch) {
		return this.notesInScale.contains(pitch);
	}
	
	public boolean isMajorInterval(Pitch first, Pitch second) {
		if (this.root != first) return false;

		int semitones = first.getDifferenceInSemiTones(second);
		return majorIntervals.contains(Interval.getInterval(semitones));
	}

	public boolean isPerfectInterval(Pitch first, Pitch second) {
		if (this.root != first) return false;

		int semitones = first.getDifferenceInSemiTones(second);
		return perfectIntervals.contains(Interval.getInterval(semitones));
	}

	public Interval getInterval(Pitch first, Pitch second) {
		if (Pitch.REST == first || Pitch.REST == second) {
			return Interval.UNDEFINED;
		}

		int semitones = first.getDifferenceInSemiTones(second);
		return Interval.getInterval(semitones) != null ? Interval.getInterval(semitones) : Interval.UNDEFINED;
	}

    public static Scale fromString(String scale) {
        return stringToEnumMapping.get(scale);
    }

    private static final class Constants {
        private Constants() {

        }
        // Major scale pattern {W,W,h,W,W,W,h} The last step is left out because the root note should only be included once.
		public static final int[] MAJOR_SCALE_PATTERN = new int[] {2,2,1,2,2,2};
		// Minor scale pattern {W,h,W,W,h,W,W}
		public static final int[] MINOR_SCALE_PATTERN = new int[] {2,1,2,2,1,2};
		public static final int[] HAMONIC_MINOR_SCALE_PATTERN = new int[] {};
		public static final int[] MELODIC_MINOR_SCALE_PATTERN = new int[] {};
		
		// semitones for major second, third, sixth and seventh
		public static final int[] MAJOR_INTERVAL_SEMITONES = new int[] {2,4,9,11};
		// semitones for perfect fourth and fifth
		public static final int[] PERFECT_INTERVAL_SEMITONES = new int[] {5,7};
	}
}
