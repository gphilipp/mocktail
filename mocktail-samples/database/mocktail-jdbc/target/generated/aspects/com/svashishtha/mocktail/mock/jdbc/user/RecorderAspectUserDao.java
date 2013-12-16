package com.svashishtha.mocktail.mock.jdbc.user;

import java.io.File;
import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import com.svashishtha.mocktail.metadata.MethodMocktail;
import com.svashishtha.mocktail.metadata.MocktailContainer;
import com.svashishtha.mocktail.repository.ObjectRepository;
import com.svashishtha.mocktail.metadata.util.UniqueIdGenerator;

@Aspect
public class RecorderAspectUserDao {
    
    private MocktailContainer mocktailContainer;
    private ObjectRepository objectRepository;
    private UniqueIdGenerator uniqueIdGenerator;
    private String fqcn;
    private String fileSeparator;
    private String recordingBasePath;
    private String methodName;

    public RecorderAspectUserDao(){
        mocktailContainer = MocktailContainer.getInstance();

        objectRepository = mocktailContainer
                .getObjectRepository();
        uniqueIdGenerator = mocktailContainer
                .getUniqueIdGenerator();
        fqcn = "com.svashishtha.mocktail.mock.jdbc.user.UserDao";

        fileSeparator = "/";
        recordingBasePath = "/Users/shrikant/code/github/mocktail/mocktail-samples/database/mocktail-jdbc/target/generated/recordings";
    }
    
            
    @Around("execution(* com.svashishtha.mocktail.mock.jdbc.user.UserDao.get(..))")
        
    public Object adviceget(ProceedingJoinPoint pjp) throws Throwable {
        String recordingFileName = uniqueIdGenerator.getUniqueId("get", pjp.getArgs()) + "";
        String methodName = "get";
        return executeAspect(pjp, recordingFileName, methodName);
    }
            
    @Around("execution(* com.svashishtha.mocktail.mock.jdbc.user.UserDao.save(..))")
        
    public Object advicesave(ProceedingJoinPoint pjp) throws Throwable {
        String recordingFileName = uniqueIdGenerator.getUniqueId("save", pjp.getArgs()) + "";
        String methodName = "save";
        return executeAspect(pjp, recordingFileName, methodName);
    }
            
    @Around("execution(* com.svashishtha.mocktail.mock.jdbc.user.UserDao.delete(..))")
        
    public Object advicedelete(ProceedingJoinPoint pjp) throws Throwable {
        String recordingFileName = uniqueIdGenerator.getUniqueId("delete", pjp.getArgs()) + "";
        String methodName = "delete";
        return executeAspect(pjp, recordingFileName, methodName);
    }
            
    @Around("execution(* com.svashishtha.mocktail.mock.jdbc.user.UserDao.update(..))")
        
    public Object adviceupdate(ProceedingJoinPoint pjp) throws Throwable {
        String recordingFileName = uniqueIdGenerator.getUniqueId("update", pjp.getArgs()) + "";
        String methodName = "update";
        return executeAspect(pjp, recordingFileName, methodName);
    }
            
    @Around("execution(* com.svashishtha.mocktail.mock.jdbc.user.UserDao.getAll(..))")
        
    public Object advicegetAll(ProceedingJoinPoint pjp) throws Throwable {
        String recordingFileName = uniqueIdGenerator.getUniqueId("getAll", pjp.getArgs()) + "";
        String methodName = "getAll";
        return executeAspect(pjp, recordingFileName, methodName);
    }
        
    private Object executeAspect(ProceedingJoinPoint pjp,
            String recordingFileName, String methodName) throws Throwable {
        boolean voidReturnType = isVoidReturnType(pjp);
        System.out.println("EXECUTING ASPECT NOW");
        System.out.println("++++++++++++++++++++");

        Object objectToBeRecorded = null;
        MethodMocktail methodMocktail = mocktailContainer.getMethodMocktail();
        if (methodMocktail != null) {
            objectToBeRecorded = mocktailForEachTestMethod(pjp,
                    recordingFileName, voidReturnType, methodMocktail,
                    methodName);
        } else {
            objectToBeRecorded = mocktailWithSharedData(pjp, recordingFileName,
                    voidReturnType, methodName);
        }

        return objectToBeRecorded;
    }

    private boolean isVoidReturnType(ProceedingJoinPoint pjp) {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        boolean voidReturnType = false;
        if (method.getReturnType().equals(Void.TYPE)) {
            voidReturnType = true;
        }
        return voidReturnType;
    }

    private Object mocktailWithSharedData(ProceedingJoinPoint pjp,
            String recordingFileName, boolean voidReturnType, String methodName) throws Throwable {
        Object objectToBeRecorded;
        String recordingDirectoryPath = recordingBasePath + fileSeparator
                + fqcn.replaceAll("\\.", fileSeparator);

        if (!(new File(recordingDirectoryPath)).exists()) {
            (new File(recordingDirectoryPath)).mkdirs();
        }

        // Get the object to be recorded
        // Ask Recorder to save the recording file
        if (!objectRepository.objectAlreadyExist(recordingFileName,
                recordingDirectoryPath)) {

            objectToBeRecorded = pjp.proceed();
            if (!voidReturnType) {
                System.out
                        .println("Recording not already in place so doing the recording:"
                                + recordingFileName
                                + ":methodName:"
                                + methodName);
                objectRepository.saveObject(objectToBeRecorded,
                        recordingFileName, recordingDirectoryPath);
            }
        } else {
            System.out.println("object already exists so not saving it:"
                    + recordingFileName + ":methodName:" + methodName);
            objectToBeRecorded = objectRepository.getObject(
                    recordingFileName, recordingDirectoryPath);
        }
        return objectToBeRecorded;
    }

    private Object mocktailForEachTestMethod(ProceedingJoinPoint pjp,
            String recordingFileName, boolean voidReturnType,
            MethodMocktail methodMocktail, String methodName) throws Throwable {
        Object objectToBeRecorded;
        methodMocktail.setRecordingBasePath(recordingBasePath);
        String methodMocktailPath = recordingBasePath + fileSeparator
                + methodMocktail.getFqcn().replaceAll("\\.", fileSeparator)
                + fileSeparator + methodMocktail.getMethodName();
        // check if it's recording mode or playback mode

        boolean recordingMode = !(methodMocktail.isPlaybackMode());
        String originalRecordingFileName = recordingFileName;
        recordingFileName = methodName;
        if (methodMocktail.getMethodCalls(originalRecordingFileName) > 0) {
            recordingFileName = recordingFileName
                    + "_"
                    + methodMocktail
                            .getMethodCalls(originalRecordingFileName);
        } else {
            recordingFileName = recordingFileName + "_0";
        }

        boolean objectExistsInRepository = objectRepository
                .objectAlreadyExist(recordingFileName, methodMocktailPath);
        System.out.println("the recording file name is:"
                + recordingFileName + " and methodMocktailPath is:"
                + methodMocktailPath + "> object exists?"
                + objectExistsInRepository + ":recordingMode?"
                + recordingMode);

        if (recordingMode && objectExistsInRepository) {
            // save it again
            // get the name of the recordingFile
            System.out
                    .println("MethodMocktail - Recording not already in place so doing the recording:"
                            + recordingFileName + ":" + methodMocktailPath);

            objectToBeRecorded = pjp.proceed();
            if (!voidReturnType) {
                objectRepository.saveObject(objectToBeRecorded,
                        recordingFileName, methodMocktailPath);
            }
        } else if (objectExistsInRepository) {
            System.out
                    .println("MethodMocktail - object already exists so not saving it:"
                            + recordingFileName + ":" + methodMocktailPath);
            objectToBeRecorded = objectRepository.getObject(
                    recordingFileName, methodMocktailPath);
        } else {
            // Ask Recorder to save the recording file

            File methodMocktailFile = new File(methodMocktailPath);
            if (!methodMocktailFile.exists()) {
                methodMocktailFile.mkdirs();
            }
            objectToBeRecorded = pjp.proceed();
            if (!voidReturnType) {
                System.out
                        .println("MethodMocktail - Recording not already in place so doing the recording:"
                                + recordingFileName
                                + ":"
                                + methodMocktailPath);
                objectRepository.saveObject(objectToBeRecorded,
                        recordingFileName, methodMocktailPath);
            }
        }

        methodMocktail
                .registerWithMethodCallsMap(originalRecordingFileName);
        return objectToBeRecorded;
    }
}