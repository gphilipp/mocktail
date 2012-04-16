package org.mocktail.aj.creator;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mocktail.MocktailContainer;
import org.mocktail.MocktailContext;
import org.mocktail.MocktailObjectMother;
import org.mocktail.xml.domain.Mocktail;
import org.mocktail.xml.domain.MocktailMode;
import org.springframework.beans.DirectFieldAccessor;

public class MocktailClassAspectCreatorTest {

    @Mock
    File aspectsRootDir;

    @Before
    public void setup() {
        MocktailContainer.initializeContainer("");
        MocktailContext mocktailContext = MocktailContext.getMocktailContext();
        DirectFieldAccessor dfa = new DirectFieldAccessor(mocktailContext);
        // Need to set as Mocktail Context is a singleton class and is getting
        // set-upped from multiple places
        dfa.setPropertyValue("recordingDirectory", "root_dir");
    }

    @Test
    public void shouldCreateRecordingAspectForClass() throws Exception {

        final Mocktail classMocktail = MocktailObjectMother
                .createClassMocktail("AspectedClass", "com.sandy");

        new MocktailClassAspectCreator(MocktailMode.RECORDING_MODE) {
            @Override
            protected void createAspectFile(Mocktail mocktail,
                    String aspectFileName, File aspectsRootDirecotry,
                    String templatedClassObjectString) throws IOException {
                assertThat(aspectFileName,
                        is("RecorderAspect" + classMocktail.getClassName()));
                assertThat(
                        templatedClassObjectString,
                        containsString("public aspect RecorderAspectAspectedClass"));
                assertThat(
                        templatedClassObjectString,
                        containsString("String fqcn = \"com.sandy.AspectedClass\";"));
                assertThat(
                        templatedClassObjectString,
                        containsString("recordingDirectoryPath = \"root_dir\";"));
                assertThat(
                        templatedClassObjectString,
                        containsString("pointcut callPointcut() : call(* com.sandy.AspectedClass.*(..));"));
                System.out.println(templatedClassObjectString);
            }
        }.createAspect(classMocktail, aspectsRootDir);
    }

    @Test
    public void shouldCreateRecordingAspectForClassWithoutPackage()
            throws Exception {

        final Mocktail classMocktail = MocktailObjectMother
                .createClassMocktail("name", "");
        new MocktailClassAspectCreator(MocktailMode.RECORDING_MODE) {
            @Override
            protected void createAspectFile(Mocktail mocktail, String fileName,
                    File directory, String templatedClassObjectString)
                    throws IOException {
                assertThat(fileName,
                        is("RecorderAspect" + classMocktail.getClassName()));
                assertThat(templatedClassObjectString,
                        containsString("public aspect RecorderAspectname"));
                assertThat(templatedClassObjectString,
                        containsString("String fqcn = \"name\";"));
                assertThat(
                        templatedClassObjectString,
                        containsString("recordingDirectoryPath = \"root_dir\";"));
                assertThat(
                        templatedClassObjectString,
                        containsString("pointcut callPointcut() : call(* name.*(..));"));
            }
        }.createAspect(classMocktail, aspectsRootDir);
    }

    @Test
    public void shouldCreatePlaybackAspectForClass() throws Exception {

        final Mocktail classMocktail = MocktailObjectMother
                .createClassMocktail("AspectedClass", "com.sandy");
        new MocktailClassAspectCreator(MocktailMode.PLAYBACK_MODE) {
            @Override
            protected void createAspectFile(Mocktail mocktail, String fileName,
                    File directory, String templatedClassObjectString)
                    throws IOException {
                System.out.println(templatedClassObjectString);
                assertThat(fileName,
                        is("PlaybackAspect" + classMocktail.getClassName()));
                assertThat(
                        templatedClassObjectString,
                        containsString("public aspect PlaybackAspectAspectedClass"));
                assertThat(
                        templatedClassObjectString,
                        containsString("String fqcn = \"com.sandy.AspectedClass\";"));
                assertThat(
                        templatedClassObjectString,
                        containsString("recordingDirectoryPath = \"root_dir\";"));
                assertThat(
                        templatedClassObjectString,
                        containsString("pointcut callPointcut() : call(* com.sandy.AspectedClass.*(..));"));
            }
        }.createAspect(classMocktail, aspectsRootDir);
    }

    @Test
    public void shouldCreatePlaybackAspectForClassWithoutPackage()
            throws Exception {

        final Mocktail classMocktail = MocktailObjectMother
                .createClassMocktail("name", "");
        new MocktailClassAspectCreator(MocktailMode.PLAYBACK_MODE) {
            @Override
            protected void createAspectFile(Mocktail mocktail, String fileName,
                    File directory, String templatedClassObjectString)
                    throws IOException {
                assertThat(fileName,
                        is("PlaybackAspect" + classMocktail.getClassName()));
                assertThat(templatedClassObjectString,
                        containsString("public aspect PlaybackAspectname"));
                assertThat(templatedClassObjectString,
                        containsString("String fqcn = \"name\";"));
                assertThat(
                        templatedClassObjectString,
                        containsString("recordingDirectoryPath = \"root_dir\";"));
                assertThat(
                        templatedClassObjectString,
                        containsString("pointcut callPointcut() : call(* name.*(..));"));
            }
        }.createAspect(classMocktail, aspectsRootDir);
    }
}
