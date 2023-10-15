package org.jenkinsci.plugins.pitmutation.targets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * User: Ed Kimber
 * Date: 19/03/13
 * Time: 18:45
 */
class MutatedClassTest extends MutationResultTest {

    private MutatedClass mutatedClass;

    @BeforeEach
    public void setUp() throws IOException, SAXException {
        super.setUp();
        mutatedClass = new MutatedClass("TestClass", null, new ArrayList<>());
    }

    @Test
    void isSourceLevelReturnsTrue() {
        assertThat(mutatedClass.isSourceLevel(), is(true));
    }

    @Test
    void getDisplayNameReturnsName() {
        assertThat(mutatedClass.getDisplayName(), is("Class: TestClass"));
    }

    @Test
    void lineUrlsAreSet() {
    }
}
