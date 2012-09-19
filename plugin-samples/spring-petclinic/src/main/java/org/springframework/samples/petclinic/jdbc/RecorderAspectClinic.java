package org.springframework.samples.petclinic.jdbc;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


import org.mocktail.MocktailContainer;
import org.mocktail.repository.ObjectRepository;
import org.mocktail.util.UniqueIdGenerator;
import org.mocktail.MethodMocktail;
import java.lang.reflect.Method;
import org.aspectj.lang.reflect.MethodSignature;
import java.lang.Void;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;


@Aspect
public class RecorderAspectClinic {

    @Around("execution(* org.springframework.samples.petclinic.Clinic.getVets(..)) && target(org.springframework.samples.petclinic.jdbc.SimpleJdbcClinic)")
    public Object advice(ProceedingJoinPoint pjp) throws Throwable {


    MocktailContainer mocktailContainer = MocktailContainer.getInstance();

	ObjectRepository objectRepository = mocktailContainer.getObjectRepository();
	UniqueIdGenerator uniqueIdGenerator = mocktailContainer.getUniqueIdGenerator();
	String fqcn = "org.springframework.samples.petclinic.jdbc.Clinic";

	 	//	pointcut callPointcutgetVets() : call(* org.springframework.samples.petclinic.jdbc.Clinic.getVets(..) && target(org.springframework.samples.petclinic.jdbc.SimpleJdbcClinic));
//	 		pointcut callPointcutgetVets() : call(* org.springframework.samples.petclinic.Clinic.getVets(..)) && target(org.springframework.samples.petclinic.jdbc.SimpleJdbcClinic);
			
			String fileSeparator = "/";
			String recordingBasePath = "/Users/shrikant/code/github/mocktail/plugin-samples/spring-petclinic/target/generated/recordings";
			
			MethodMocktail methodMocktail = mocktailContainer.getMethodMocktail();
			
			String methodName="getVets";
			String recordingFileName = uniqueIdGenerator.getUniqueId(methodName, pjp.getArgs()) + "";
			Object objectToBeRecorded = null;
			Method method = ((MethodSignature) pjp.getSignature()).getMethod();
			boolean voidReturnType = false;
            if( method.getReturnType().equals(Void.TYPE)){
                voidReturnType = true;
            }
            
            System.out.println("EXECUTING ASPECT NOW");
            System.out.println("++++++++++++++++++++");
			
			if(methodMocktail != null){
			   
			    methodMocktail.setRecordingBasePath(recordingBasePath);
			    String methodMocktailPath = recordingBasePath + fileSeparator + methodMocktail.getFqcn().replaceAll("\\.", fileSeparator) + fileSeparator+methodMocktail.getMethodName();
			    //check if it's recording mode or playback mode
			    
			    boolean recordingMode = !(methodMocktail.isPlaybackMode());
			    String originalRecordingFileName = recordingFileName;
			    recordingFileName = methodName;
			    if(methodMocktail.getMethodCalls(originalRecordingFileName) > 0){
			        recordingFileName = recordingFileName + "_" + methodMocktail.getMethodCalls(originalRecordingFileName);
			    } else {
			        recordingFileName = recordingFileName + "_0"; 
			    }
			    
			    boolean objectExistsInRepository = objectRepository.objectAlreadyExist(recordingFileName , methodMocktailPath);
			    System.out.println("the recording file name is:"+recordingFileName + " and methodMocktailPath is:"+methodMocktailPath+"> object exists?"+objectExistsInRepository+":recordingMode?"+ recordingMode);
			    

			    if(recordingMode && objectExistsInRepository){
			        //save it again
			        //get the name of the recordingFile
			        System.out.println("MethodMocktail - Recording not already in place so doing the recording:"+recordingFileName+":"+methodMocktailPath);
			        
			        objectToBeRecorded = pjp.proceed();
			        if(!voidReturnType){
				        objectRepository.saveObject(objectToBeRecorded, recordingFileName,
				    	    	methodMocktailPath);
				    }
                } else if(objectExistsInRepository){
                	System.out.println("MethodMocktail - object already exists so not saving it:"+recordingFileName+":"+methodMocktailPath);
			        objectToBeRecorded = objectRepository.getObject(recordingFileName, methodMocktailPath);		   
			    } else {
				    // Ask Recorder to save the recording file
				    
				    
				    File methodMocktailFile = new File(methodMocktailPath);
			        if (!methodMocktailFile.exists()) {
				        methodMocktailFile.mkdirs();
			        }
				    objectToBeRecorded = pjp.proceed();
				    if(!voidReturnType){
				        System.out.println("MethodMocktail - Recording not already in place so doing the recording:"+recordingFileName+":"+methodMocktailPath);
				        objectRepository.saveObject(objectToBeRecorded, recordingFileName,
				    	    	methodMocktailPath);
				    }
			    }
			    
			    methodMocktail.registerWithMethodCallsMap(originalRecordingFileName);
			} else {
			
			    String recordingDirectoryPath = recordingBasePath + fileSeparator + fqcn.replaceAll("\\.", fileSeparator);
			
                if (!(new File(recordingDirectoryPath)).exists()) {
				    (new File(recordingDirectoryPath)).mkdirs();
			    }
			    
			    // Get the object to be recorded
			    // Ask Recorder to save the recording file
			    if (!objectRepository.objectAlreadyExist(recordingFileName,
			    		recordingDirectoryPath)) {
			    	
			    	objectToBeRecorded = pjp.proceed();
			    	if(!voidReturnType){
			    	    System.out.println("Recording not already in place so doing the recording:"+recordingFileName+":methodName:"+methodName);
			    	    objectRepository.saveObject(objectToBeRecorded, recordingFileName,
			    		    	recordingDirectoryPath);
			    	}
			    } else {
			    	System.out.println("object already exists so not saving it:"+recordingFileName+":methodName:"+methodName);
			    	objectToBeRecorded = objectRepository.getObject(recordingFileName, recordingDirectoryPath);
			    }
			}
	
			return objectToBeRecorded;
            }
	}
