import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


import org.mocktail.MocktailContainer;
import org.mocktail.repository.ObjectRepository;
import org.mocktail.util.UniqueIdGenerator;

public aspect RecorderAspectUserDao {

	ObjectRepository objectRepository = MocktailContainer.getInstance().getObjectRepository();
	UniqueIdGenerator uniqueIdGenerator = MocktailContainer.getInstance().getUniqueIdGenerator();
	String fqcn = "org.mocktail.mock.jdbc.user.UserDao";

	 		pointcut callPointcutget() : call(* org.mocktail.mock.jdbc.user.UserDao.get(..));
	
		
		Object around() : callPointcutget() {
			
			String fileSeparator = "/";
			String recordingDirectoryPath = "/Users/shrikant/code/github/mocktail/plugin-samples/mocktail-jdbc/target/generated/recordings";
			recordingDirectoryPath += fileSeparator + fqcn.replaceAll("\\.", fileSeparator);
	
			if (!(new File(recordingDirectoryPath)).exists()) {
				(new File(recordingDirectoryPath)).mkdirs();
			}
			
			// Create the unique id of param objects to be recorded
			String methodName="get";
			String recordingFileName = uniqueIdGenerator.getUniqueId(methodName, thisJoinPoint.getArgs()) + "";
			System.out.println(thisEnclosingJoinPointStaticPart.getSignature().getName());
			
			
			Object objectToBeRecorded = null;
			// Get the object to be recorded
			// Ask Recorder to save the recording file
			if (!objectRepository.objectAlreadyExist(recordingFileName,
					recordingDirectoryPath)) {
				System.out.println("Recording not already in place so doing the recording");
				objectToBeRecorded = proceed();
				objectRepository.saveObject(objectToBeRecorded, recordingFileName,
						recordingDirectoryPath);
			} else {
				System.out.println("object already exists so not saving it");
				objectToBeRecorded = objectRepository.getObject(recordingFileName, recordingDirectoryPath);
			}
	
			
			return objectToBeRecorded;
		}	
	 		pointcut callPointcutsave() : call(* org.mocktail.mock.jdbc.user.UserDao.save(..));
	
		
		Object around() : callPointcutsave() {
			
			String fileSeparator = "/";
			String recordingDirectoryPath = "/Users/shrikant/code/github/mocktail/plugin-samples/mocktail-jdbc/target/generated/recordings";
			recordingDirectoryPath += fileSeparator + fqcn.replaceAll("\\.", fileSeparator);
	
			if (!(new File(recordingDirectoryPath)).exists()) {
				(new File(recordingDirectoryPath)).mkdirs();
			}
			
			// Create the unique id of param objects to be recorded
			String methodName="save";
			String recordingFileName = uniqueIdGenerator.getUniqueId(methodName, thisJoinPoint.getArgs()) + "";
			System.out.println(thisEnclosingJoinPointStaticPart.getSignature().getName());
			
			
			Object objectToBeRecorded = null;
			// Get the object to be recorded
			// Ask Recorder to save the recording file
			if (!objectRepository.objectAlreadyExist(recordingFileName,
					recordingDirectoryPath)) {
				System.out.println("Recording not already in place so doing the recording");
				objectToBeRecorded = proceed();
				objectRepository.saveObject(objectToBeRecorded, recordingFileName,
						recordingDirectoryPath);
			} else {
				System.out.println("object already exists so not saving it");
				objectToBeRecorded = objectRepository.getObject(recordingFileName, recordingDirectoryPath);
			}
	
			
			return objectToBeRecorded;
		}	
	 		pointcut callPointcutdelete() : call(* org.mocktail.mock.jdbc.user.UserDao.delete(..));
	
		
		Object around() : callPointcutdelete() {
			
			String fileSeparator = "/";
			String recordingDirectoryPath = "/Users/shrikant/code/github/mocktail/plugin-samples/mocktail-jdbc/target/generated/recordings";
			recordingDirectoryPath += fileSeparator + fqcn.replaceAll("\\.", fileSeparator);
	
			if (!(new File(recordingDirectoryPath)).exists()) {
				(new File(recordingDirectoryPath)).mkdirs();
			}
			
			// Create the unique id of param objects to be recorded
			String methodName="delete";
			String recordingFileName = uniqueIdGenerator.getUniqueId(methodName, thisJoinPoint.getArgs()) + "";
			System.out.println(thisEnclosingJoinPointStaticPart.getSignature().getName());
			
			
			Object objectToBeRecorded = null;
			// Get the object to be recorded
			// Ask Recorder to save the recording file
			if (!objectRepository.objectAlreadyExist(recordingFileName,
					recordingDirectoryPath)) {
				System.out.println("Recording not already in place so doing the recording");
				objectToBeRecorded = proceed();
				objectRepository.saveObject(objectToBeRecorded, recordingFileName,
						recordingDirectoryPath);
			} else {
				System.out.println("object already exists so not saving it");
				objectToBeRecorded = objectRepository.getObject(recordingFileName, recordingDirectoryPath);
			}
	
			
			return objectToBeRecorded;
		}	
	 		pointcut callPointcutupdate() : call(* org.mocktail.mock.jdbc.user.UserDao.update(..));
	
		
		Object around() : callPointcutupdate() {
			
			String fileSeparator = "/";
			String recordingDirectoryPath = "/Users/shrikant/code/github/mocktail/plugin-samples/mocktail-jdbc/target/generated/recordings";
			recordingDirectoryPath += fileSeparator + fqcn.replaceAll("\\.", fileSeparator);
	
			if (!(new File(recordingDirectoryPath)).exists()) {
				(new File(recordingDirectoryPath)).mkdirs();
			}
			
			// Create the unique id of param objects to be recorded
			String methodName="update";
			String recordingFileName = uniqueIdGenerator.getUniqueId(methodName, thisJoinPoint.getArgs()) + "";
			System.out.println(thisEnclosingJoinPointStaticPart.getSignature().getName());
			
			
			Object objectToBeRecorded = null;
			// Get the object to be recorded
			// Ask Recorder to save the recording file
			if (!objectRepository.objectAlreadyExist(recordingFileName,
					recordingDirectoryPath)) {
				System.out.println("Recording not already in place so doing the recording");
				objectToBeRecorded = proceed();
				objectRepository.saveObject(objectToBeRecorded, recordingFileName,
						recordingDirectoryPath);
			} else {
				System.out.println("object already exists so not saving it");
				objectToBeRecorded = objectRepository.getObject(recordingFileName, recordingDirectoryPath);
			}
	
			
			return objectToBeRecorded;
		}	
		
}

