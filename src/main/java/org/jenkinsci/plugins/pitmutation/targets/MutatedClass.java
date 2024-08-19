package org.jenkinsci.plugins.pitmutation.targets;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;

import org.jenkinsci.plugins.pitmutation.Mutation;

import hudson.util.TextFile;
import lombok.Getter;

/**
 * @author Ed Kimber
 */
public class MutatedClass extends MutationResult<MutatedClass> {
    private static final String END_HEADER_TAG = "</h1>";

    @Getter
    private String name;
    @Getter
    private String package_;
    @Getter
    private String fileName;
    private Collection<Mutation> mutations;
    private Map<String, MutatedLine> mutatedLines;

    public MutatedClass(String name, MutationResult parent, Collection<Mutation> mutations) {
        super(name, parent);
        this.name = name;
        this.mutations = mutations;

        int lastDot = name.lastIndexOf('.');
        package_ = lastDot >= 0 ? name.substring(0, lastDot) : "";
        fileName = lastDot >= 0 ? name.substring(lastDot + 1) + ".java.html" : "";

        mutatedLines = createMutatedLines(mutations);
    }

    private Map<String, MutatedLine> createMutatedLines(Collection<Mutation> mutations) {
        return mutations
            .stream()
            .collect(groupingBy(Mutation::getLineNumber))
            .values()
            .stream()
            .map(m -> new MutatedLine(m.get(0).getLineNumber(), MutatedClass.this, m))
            .collect(toMap(MutatedLine::getName, o -> o));
    }

    @Override
    public boolean isSourceLevel() {
        return true;
    }

    /**
     * Gets the contents of the coverage report for the file, but removes the header and the stylesheet as this needs to be handled separately in jelly.
     *
     * @return The source of the coverage report to show in the UI
     */
    @Override
    public String getSourceFileContent() {
        String fullContents = getFileContents(package_ + File.separator + fileName);
        return fullContents.contains(END_HEADER_TAG) ?
               fullContents.substring(fullContents.indexOf(END_HEADER_TAG) + 5) :
               fullContents;
    }

    /**
     * Gets the contents of the style sheet for the coverage report.
     *
     * @return The source of the coverage report to show in the UI.
     */
    @Override
    public String getStyleSheetContent() {
        return getFileContents("style.css");
    }

    private String getFileContents(String path) {
        String filePath =
            getOwner().getRootDir() + File.separator + getMutationReportDirectory() + File.separator + path;
        try {
            return new TextFile(new File(filePath)).read();
        } catch (IOException exception) {
            return "Could not read file: " + filePath + "\n";
        }
    }


    public String getDisplayName() {
        return "Class: " + name;
    }

    @Override
    public MutationStats getMutationStats() {
        return new MutationStatsImpl(getName(), mutations);
    }

    @Override
    public Map<String, ? extends MutationResult<?>> getChildMap() {
        return mutatedLines;
    }

    @Override
    public int compareTo(@Nonnull MutatedClass other) {
        return this.getMutationStats().getUndetected() - other.getMutationStats().getUndetected();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        MutatedClass that = (MutatedClass) o;
        return Objects.equals(name, that.name)
               && Objects.equals(package_, that.package_)
               && Objects.equals(fileName,
                                 that.fileName)
               && Objects.equals(mutations, that.mutations)
               && Objects.equals(mutatedLines, that.mutatedLines);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, package_, fileName, mutations, mutatedLines);
    }
}
