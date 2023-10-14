package org.jenkinsci.plugins.pitmutation;

import hudson.FilePath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.mockito.Mockito.*;

class FileProcessorTest {

    @Mock
    private FilePath source;

    @Mock
    private FilePath buildTarget;

    private FileProcessor fileProcessor;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        fileProcessor = new FileProcessor();
    }

    @Test
    void copySingleModuleReport() throws Exception {
        fileProcessor.copySingleModuleReport(source, buildTarget);
        verify(source, times(1)).getParent();
    }

    @Test
    void copyMultiModuleReport() throws Exception {
        String module = "module1";
        fileProcessor.copyMultiModuleReport(source, buildTarget, module);
        verify(source, times(1)).getParent();
    }

    // TODO: Add more tests for other methods in FileProcessor
}
