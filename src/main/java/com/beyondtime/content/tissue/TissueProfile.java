package com.beyondtime.content.tissue;

import java.util.List;

/**
 * What the analyzers know about one kind of creature.
 *
 * <p>This is deliberately only the <em>readable</em> part of a tissue sample: a broad group, a few
 * gene-fragment codes, and a short list of traits. The story text itself lives in the language files,
 * so a translation or a reword does not touch the code.
 *
 * @param groupKey translation key of the broad group, e.g. {@code tissue.beyondtime.group.ender}
 * @param genome gene-fragment codes, e.g. {@code E-01}; a partial reading hides the tail
 * @param traitKeys translation keys of the traits a reading can show
 */
public record TissueProfile(String groupKey, List<String> genome, List<String> traitKeys) {
    public TissueProfile {
        genome = List.copyOf(genome);
        traitKeys = List.copyOf(traitKeys);
    }
}
