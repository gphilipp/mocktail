import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


import org.mocktail.MocktailContainer;
import org.mocktail.repository.ObjectRepository;
import org.mocktail.util.UniqueIdGenerator;
import org.mocktail.MethodMocktail;

public aspect RecorderAspectUserDao {

    MocktailContainer mocktailContainer = MocktailContainer.getInstance();

	ObjectRepository objectRepository = mocktailContainer.getObjectRepository();
	UniqueIdGenerator uniqueIdGenerator = mocktailContainer.getUniqueIdGenerator();
	String fqcn = "org.mocktail.mock.jdbc.user.UserDao";

	 		pointcut callPointcutget() : call(* org.mocktail.mock.jdbc.user.UserDao.get(..));
	
		
		Object around() : callPointcutget() {
			
			String fileSeparator = "/";
			String recordingBasePath = "/Users/shrikant/code/github/mocktail/plugin-samples/mocktail-jdbc/target/generated/recordings";
			
			MethodMocktail methodMocktail = mocktailContainer.getMethodMocktail();
			
			String methodName="get";
			String recordingFileName = uniqueIdGenerator.getUniqueId(methodName, thisJoinPoint.getArgs()) + "";
			Object objectToBeRecorded = null;
			
			if(methodMocktail != null){
			    methodMocktail.setRecordingBasePath(recordingBasePath);
			    //String methodMocktailPath = methodMocktail.getRecordingDirectoryPath();
			    String methodMocktailPath = recordingBasePath + fileSeparator + methodMocktail.getFqcn().replaceAll("\\.", fileSeparator) + fileSeparator+methodMocktail.getMethodName();
			    System.out.println(methodMocktailPath);
			    
			    //File methodMocktailFile = new File(methodMocktailPath);
			    //if (!methodMocktailFile.exists()) {
				  //  methodMocktailFile.mkdirs();
			    //}
			    
			    System.out.println("test1");
			    
			    if(objectRepository.objectAlreadyExist(recordingFileName,
			    		methodMocktailPath)){
			        System.out.println("MethodMocktail - object already exists so not saving it:"+recordingFileName+":"+methodMocktailPath);
			        objectToBeRecorded = objectRepository.getObject(recordingFileName, methodMocktailPath);
			    } else {
				    // Ask Recorder to save the recording file
				    System.out.println("test2");
				    System.out.println("MethodMocktail - Recording not already in place so doing the recording:"+recordingFileName+":"+methodMocktailPath);
				    
				    System.out.println("test3");
				    File methodMocktailFile = new File(methodMocktailPath);
				    System.out.println("test4");
			        if (!methodMocktailFile.exists()) {
				        methodMocktailFile.mkdirs();
			        }
				    objectToBeRecorded = proceed();
				    objectRepository.saveObject(objectToBeRecorded, recordingFileName,
				    		methodMocktailPath);
			    }
			} else {
			
			    String recordingDirectoryPath = recordingBasePath + fileSeparator + fqcn.replaceAll("\\.", fileSeparator);
			
                if (!(new File(recordingDirectoryPath)).exists()) {
				    (new File(recordingDirectoryPath)).mkdirs();
			    }
			    
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
			}
	
			return objectToBeRecorded;
		}	
	 		pointcut callPointcutsave() : call(* org.mocktail.mock.jdbc.user.UserDao.save(..));
	
		
		Object around() : callPointcutsave() {
			
			String fileSeparator = "/";
			String recordingBasePath = "/Users/shrikant/code/github/mocktail/plugin-samples/mocktail-jdbc/target/generated/recordings";
			
			MethodMocktail methodMocktail = mocktailContainer.getMethodMocktail();
			
			String methodName="save";
			String recordingFileName = uniqueIdGenerator.getUniqueId(methodName, thisJoinPoint.getArgs()) + "";
			Object objectToBeRecorded = null;
			
			if(methodMocktail != null){
			    methodMocktail.setRecordingBasePath(recordingBasePath);
			    //String methodMocktailPath = methodMocktail.getRecordingDirectoryPath();
			    String methodMocktailPath = recordingBasePath + fileSeparator + methodMocktail.getFqcn().replaceAll("\\.", fileSeparator) + fileSeparator+methodMocktail.getMethodName();
			    System.out.println(methodMocktailPath);
			    
			    //File methodMocktailFile = new File(methodMocktailPath);
			    //if (!methodMocktailFile.exists()) {
				  //  methodMocktailFile.mkdirs();
			    //}
			    
			    System.out.println("test1");
			    
			    if(objectRepository.objectAlreadyExist(recordingFileName,
			    		methodMocktailPath)){
			        System.out.println("MethodMocktail - object already exists so not saving it:"+recordingFileName+":"+methodMocktailPath);
			        objectToBeRecorded = objectRepository.getObject(recordingFileName, methodMocktailPath);
			    } else {
				    // Ask Recorder to save the recording file
				    System.out.println("test2");
				    System.out.println("MethodMocktail - Recording not already in place so doing the recording:"+recordingFileName+":"+methodMocktailPath);
				    
				    System.out.println("test3");
				    File methodMocktailFile = new File(methodMocktailPath);
				    System.out.println("test4");
			        if (!methodMocktailFile.exists()) {
				        methodMocktailFile.mkdirs();
			        }
				    objectToBeRecorded = proceed();
				    objectRepository.saveObject(objectToBeRecorded, recordingFileName,
				    		methodMocktailPath);
			    }
			} else {
			
			    String recordingDirectoryPath = recordingBasePath + fileSeparator + fqcn.replaceAll("\\.", fileSeparator);
			
                if (!(new File(recordingDirectoryPath)).exists()) {
				    (new File(recordingDirectoryPath)).mkdirs();
			    }
			    
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
			}
	
			return objectToBeRecorded;
		}	
	 		pointcut callPointcutdelete() : call(* org.mocktail.mock.jdbc.user.UserDao.delete(..));
	
		
		Object around() : callPointcutdelete() {
			
			String fileSeparator = "/";
			String recordingBasePath = "/Users/shrikant/code/github/mocktail/plugin-samples/mocktail-jdbc/target/generated/recordings";
			
			MethodMocktail methodMocktail = mocktailContainer.getMethodMocktail();
			
			String methodName="delete";
			String recordingFileName = uniqueIdGenerator.getUniqueId(methodName, thisJoinPoint.getArgs()) + "";
			Object objectToBeRecorded = null;
			
			if(methodMocktail != null){
			    methodMocktail.setRecordingBasePath(recordingBasePath);
			    //String methodMocktailPath = methodMocktail.getRecordingDirectoryPath();
			    String methodMocktailPath = recordingBasePath + fileSeparator + methodMocktail.getFqcn().replaceAll("\\.", fileSeparator) + fileSeparator+methodMocktail.getMethodName();
			    System.out.println(methodMocktailPath);
			    
			    //File methodMocktailFile = new File(methodMocktailPath);
			    //if (!methodMocktailFile.exists()) {
				  //  methodMocktailFile.mkdirs();
			    //}
			    
			    System.out.println("test1");
			    
			    if(objectRepository.objectAlreadyExist(recordingFileName,
			    		methodMocktailPath)){
			        System.out.println("MethodMocktail - object already exists so not saving it:"+recordingFileName+":"+methodMocktailPath);
			        objectToBeRecorded = objectRepository.getObject(recordingFileName, methodMocktailPath);
			    } else {
				    // Ask Recorder to save the recording file
				    System.out.println("test2");
				    System.out.println("MethodMocktail - Recording not already in place so doing the recording:"+recordingFileName+":"+methodMocktailPath);
				    
				    System.out.println("test3");
				    File methodMocktailFile = new File(methodMocktailPath);
				    System.out.println("test4");
			        if (!methodMocktailFile.exists()) {
				        methodMocktailFile.mkdirs();
			        }
				    objectToBeRecorded = proceed();
				    objectRepository.saveObject(objectToBeRecorded, recordingFileName,
				    		methodMocktailPath);
			    }
			} else {
			
			    String recordingDirectoryPath = recordingBasePath + fileSeparator + fqcn.replaceAll("\\.", fileSeparator);
			
                if (!(new File(recordingDirectoryPath)).exists()) {
				    (new File(recordingDirectoryPath)).mkdirs();
			    }
			    
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
			}
	
			return objectToBeRecorded;
		}	
	 		pointcut callPointcutupdate() : call(* org.mocktail.mock.jdbc.user.UserDao.update(..));
	
		
		Object around() : callPointcutupdate() {
			
			String fileSeparator = "/";
			String recordingBasePath = "/Users/shrikant/code/github/mocktail/plugin-samples/mocktail-jdbc/target/generated/recordings";
			
			MethodMocktail methodMocktail = mocktailContainer.getMethodMocktail();
			
			String methodName="update";
			String recordingFileName = uniqueIdGenerator.getUniqueId(methodName, thisJoinPoint.getArgs()) + "";
			Object objectToBeRecorded = null;
			
			if(methodMocktail != null){
			    methodMocktail.setRecordingBasePath(recordingBasePath);
			    //String methodMocktailPath = methodMocktail.getRecordingDirectoryPath();
			    String methodMocktailPath = recordingBasePath + fileSeparator + methodMocktail.getFqcn().replaceAll("\\.", fileSeparator) + fileSeparator+methodMocktail.getMethodName();
			    System.out.println(methodMocktailPath);
			    
			    //File methodMocktailFile = new File(methodMocktailPath);
			    //if (!methodMocktailFile.exists()) {
				  //  methodMocktailFile.mkdirs();
			    //}
			    
			    System.out.println("test1");
			    
			    if(objectRepository.objectAlreadyExist(recordingFileName,
			    		methodMocktailPath)){
			        System.out.println("MethodMocktail - object already exists so not saving it:"+recordingFileName+":"+methodMocktailPath);
			        objectToBeRecorded = objectRepository.getObject(recordingFileName, methodMocktailPath);
			    } else {
				    // Ask Recorder to save the recording file
				    System.out.println("test2");
				    System.out.println("MethodMocktail - Recording not already in place so doing the recording:"+recordingFileName+":"+methodMocktailPath);
				    
				    System.out.println("test3");
				    File methodMocktailFile = new File(methodMocktailPath);
				    System.out.println("test4");
			        if (!methodMocktailFile.exists()) {
				        methodMocktailFile.mkdirs();
			        }
				    objectToBeRecorded = proceed();
				    objectRepository.saveObject(objectToBeRecorded, recordingFileName,
				    		methodMocktailPath);
			    }
			} else {
			
			    String recordingDirectoryPath = recordingBasePath + fileSeparator + fqcn.replaceAll("\\.", fileSeparator);
			
                if (!(new File(recordingDirectoryPath)).exists()) {
				    (new File(recordingDirectoryPath)).mkdirs();
			    }
			    
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
			}
	
			return objectToBeRecorded;
		}	
	}