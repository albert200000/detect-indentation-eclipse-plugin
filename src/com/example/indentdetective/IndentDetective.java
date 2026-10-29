package com.example.indentdetective;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Core detection logic.
 *
 * Runs heuristic scoring on the line prefixes of a document to infer the
 * indentation settings (tabs vs. spaces and the indentation width), modeled
 * faithfully after the functionality of atom-indent-detective (Pulsar editor).
 */
public class IndentDetective {

    private static final int MAX_LINES_TO_CHECK = 150;
    private static final Set<Integer> POSSIBLE_INDENTATIONS = new HashSet<>(Arrays.asList(2, 3, 4, 6, 8));

    public static IndentResult analyze(IDocument document) {
        int lineCount = Math.min(document.getNumberOfLines(), MAX_LINES_TO_CHECK);
        int tabLines = 0;
        int spaceLines = 0;
        Map<Integer, Integer> spaceIndentFrequencies = new HashMap<>();

        int previousIndent = 0;
        int previousDiff = 0;
        int numberOfCounts = 0;

        for (int i = 0; i < lineCount; i++) {
            if (numberOfCounts > MAX_LINES_TO_CHECK) {
                break;
            }
            try {
                IRegion region = document.getLineInformation(i);
                String line = document.get(region.getOffset(), region.getLength());

                // Skip empty or whitespace-only lines
                if (line.matches("^\\s*$")) {
                    continue;
                }

                if (line.startsWith("\t")) {
                    tabLines++;
                    previousIndent = 0;
                    previousDiff = 0;
                    numberOfCounts++;
                } else if (line.startsWith(" ") || line.matches("^[ ]+.*")) {
                    spaceLines++;
                    int indent = countLeadingSpaces(line);
                    int diff = Math.abs(indent - previousIndent);

                    if (diff == 0) {
                        // Reinforce previous successful diff if we are at a consistent indent level
                        if (previousDiff != 0 && indent != 0) {
                            spaceIndentFrequencies.put(previousDiff,
                                    spaceIndentFrequencies.getOrDefault(previousDiff, 0) + 1);
                        }
                    } else {
                        spaceIndentFrequencies.put(diff,
                                spaceIndentFrequencies.getOrDefault(diff, 0) + 1);
                        previousDiff = diff;
                    }

                    previousIndent = indent;
                    numberOfCounts++;
                }
            } catch (BadLocationException e) {
                // Ignore invalid line indexes during live edits
            }
        }

        boolean useSpaces = spaceLines >= tabLines;
        int detectedWidth = determineBestWidth(spaceIndentFrequencies);

        return new IndentResult(useSpaces, detectedWidth);
    }

    private static int countLeadingSpaces(String line) {
        int count = 0;
        while (count < line.length() && line.charAt(count) == ' ') {
            count++;
        }
        return count;
    }

    private static int determineBestWidth(Map<Integer, Integer> frequencies) {
        int maxScore = 0;
        int bestWidth = 4; // Default standard fallback

        for (Map.Entry<Integer, Integer> entry : frequencies.entrySet()) {
            int vote = entry.getKey();
            int score = entry.getValue();

            // Only consider standard/possible indentation sizes (2, 3, 4, 6, 8)
            if (POSSIBLE_INDENTATIONS.contains(vote) && score > maxScore) {
                maxScore = score;
                bestWidth = vote;
            }
        }
        return bestWidth;
    }
}