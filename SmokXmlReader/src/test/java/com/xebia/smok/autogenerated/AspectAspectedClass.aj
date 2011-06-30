import java.io.File;

import com.xebia.smok.SmokContainer;
import com.xebia.smok.SmokContext;
import com.xebia.smok.repository.ObjectRepository;
import com.xebia.smok.util.UniqueIdGenerator;

public aspect AspectAspectedClass {

	ObjectRepository objectRepository = SmokContainer.getObjectRepository();
	UniqueIdGenerator uniqueIdGenerator = SmokContainer.getUniqueIdGenerator();
	
	String fqcn = ".AspectedClass";
	pointcut callPointcut() : call(* .AspectedClass.*(..));
	
	
	Object around() : callPointcut() {
		// Get the Directory path form SmokContext where we have to store the
		// file
		String recordingDirectoryPath = SmokContext.getSmokContext()
				.getRecordingDirectory();
		
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
