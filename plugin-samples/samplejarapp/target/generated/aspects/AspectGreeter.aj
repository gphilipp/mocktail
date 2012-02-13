import java.io.File;

import org.mocktail.MocktailContainer;
import org.mocktail.MocktailContext;
import org.mocktail.repository.ObjectRepository;
import org.mocktail.util.UniqueIdGenerator;

public aspect AspectGreeter {

	ObjectRepository objectRepository = MocktailContainer.getObjectRepository();
	UniqueIdGenerator uniqueIdGenerator = MocktailContainer.getUniqueIdGenerator();
	
	String fqcn = "Greeter";
	// Get the Directory path form MocktailContext where we have to store the
	// file
	String recordingDirectoryPath = "c:";
	pointcut callPointcut() : call(* Greeter.*(..));
	
	
	Object around() : callPointcut() {
		
		String fileSeparator = "/";
		recordingDirectoryPath = recordingDirectoryPath + fileSeparator + fqcn.replaceAll("\\.", fileSeparator);

		if (!(new File(recordingDirectoryPath)).exists()) {
			(new File(recordingDirectoryPath)).mkdirs();
		}
		
		// Create the unique id of param objects to be recorded
		//TODO: Look into method name issue
		/*String recrodingFileName = uniqueIdGenerator.getUniqueId(thisJoinPoint.getStaticPart(), thisJoinPoint.getArgs())
				+ "";*/
		String recrodingFileName = uniqueIdGenerator.getUniqueId(thisJoinPoint.getArgs())
		+ "";
		
		
		Object objectToBeRecorded = null;
		// Get the object to be recorded
		// Ask Recorder to save the recording file
		if (!objectRepository.objectAlreadyExist(recrodingFileName,
				recordingDirectoryPath)) {
			System.out.println("Recording not already in place so doing the recording");
			objectToBeRecorded = proceed();
			objectRepository.saveObject(objectToBeRecorded, recrodingFileName,
					recordingDirectoryPath);
		} else {
			System.out.println("object already exists so not saving it");
			objectToBeRecorded = objectRepository.getObject(recrodingFileName, recordingDirectoryPath);
		}

		
		return objectToBeRecorded;
	}
}
