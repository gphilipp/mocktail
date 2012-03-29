package org.mocktail;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.mojo.aspectj.AjcCompileMojo;
import org.mocktail.aj.creator.MocktailAspectsCreator;
import org.mocktail.xml.domain.Mocktail;
import org.mocktail.xml.domain.MocktailMode;
import org.mocktail.xml.reader.XStreamMocktailXmlReader;

// *TODO: Chaning phase from process-classes to validate to just make sure that aspects generation happends after this mojo execution
/**
 * Goal which touches a timestamp file.
 * 
 * @goal mocktail
 * 
 * @phase validate
 */
public class MocktailMojo extends AjcCompileMojo {
	/**
	 * @parameter expression=�${aspectsDirectory}�
	 *            default-value="${target}/generated/aspects"
	 * @required
	 */
	private File aspectsDirectory;

	/**
	 * @parameter expression=�${mocktailconfig}� default-value="mocktail.xml"
	 * @required
	 */
	private File configuration;

	/**
	 * @parameter expression=�${recordingDir}� default-value="src/recording"
	 * @required
	 */
	private File recordingDir;
	
	/**
	 * @parameter expression=�${mode}�
	 *            default-value="recording"
	 * @required
	 */
	private String mode;

	public void execute() throws MojoExecutionException {

		System.out.println("Executing the mocktail mojo");
		if (!aspectsDirectory.exists()) {
			aspectsDirectory.mkdirs();
		}
		XStreamMocktailXmlReader configReader = new XStreamMocktailXmlReader();
		MocktailContainer.initializeContainer(recordingDir.getAbsolutePath());
		try {
			List<Mocktail> mocktails = configReader.readXml(new FileInputStream(
					configuration));
			System.out.println("\n\n " + mocktails + "\n\n");
			//TODO:A hack for time being as we will be either generating recording/playback aspects at a time
			if (mode.equalsIgnoreCase(MocktailMode.RECORDING_MODE.getModeDirectory())) {
				MocktailAspectsCreator.ASPECTS_CREATOR.createAspects(mocktails,
						aspectsDirectory, MocktailMode.RECORDING_MODE);
			} else {
				MocktailAspectsCreator.ASPECTS_CREATOR.createAspects(mocktails,
						aspectsDirectory, MocktailMode.PLAYBACK_MODE);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("Generated aspect files at "
				+ aspectsDirectory.getAbsolutePath());
		//TODO: Commented out for time being need to read about how to extend a plugin as we can't set the property of the plugin
		/*AjcCompileMojo ajcCompileMojo = new AjcCompileMojo();
		Class<?> superclass = ajcCompileMojo.getClass().getSuperclass();
		setValue(superclass, ajcCompileMojo, "source", source);
		setValue(superclass, ajcCompileMojo, "target", target);
		setValue(superclass, ajcCompileMojo, "aspectDirectory",
				aspectsDirectory.getAbsolutePath());
		setValue(superclass.getSuperclass(), ajcCompileMojo, "project", project);
		setValue(superclass.getSuperclass(), ajcCompileMojo, "basedir", basedir);

		System.out.println("************************************");
		System.out.println("************************************");
		System.out.println("Executing ajc mojo");
		//TODO: Needs to be fixed| Throwing exception right now
//		ajcCompileMojo.execute();
		System.out.println("************************************");
		System.out.println("************************************");*/

	}

	@SuppressWarnings("rawtypes")
	public void setValue(Class classToBeSetOn, Object o, String fieldName,
			Object value) throws RuntimeException {
		Field field;
		try {
			field = classToBeSetOn.getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(o, value);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}
}
