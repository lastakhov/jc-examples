package nl.jamiecraane.melodygeneration.util;

import nl.jamiecraane.melodygeneration.Note;
import nl.jamiecraane.melodygeneration.Pitch;
import org.jgap.impl.CompositeGene;
import org.jgap.impl.IntegerGene;

/**
 * Author: Jamie Craane
 * Date: 2-jun-2009
 * Time: 21:29:28
 */
public final class GeneNoteFactory {
    private GeneNoteFactory() {

    }

    public static Note fromGene(CompositeGene gene) {
        IntegerGene pitch = (IntegerGene) gene.geneAt(0);
        IntegerGene octave = (IntegerGene) gene.geneAt(1);
        return Note.createNote(Pitch.getByIndex(((Integer) pitch.getAllele())), (Integer) octave.getAllele());
    }
}
