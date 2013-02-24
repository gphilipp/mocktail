/*
 * Copyright 2004,2005 The Apache Software Foundation.
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

package com.svashishtha.mocktail.tcpcache;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.LoggerFactory;


import com.svashishtha.mocktail.MocktailMode;
import com.svashishtha.mocktail.repository.DiskObjectRepository;
import com.svashishtha.mocktail.repository.ObjectRepository;

/**
 * this class handles the pumping of data from the incoming socket to the
 * outgoing socket
 */
class SocketRR extends Thread {
    private static final org.slf4j.Logger log = LoggerFactory
            .getLogger(SocketRR.class);

	/**
     * Field inSocket
     */
    Socket inSocket = null;

    /**
     * Field outSocket
     */
    Socket outSocket = null;

    /**
     * Field in
     */
    InputStream inputStream = null;

    /**
     * Field out
     */
    OutputStream outputStream = null;

    /**
     * Field xmlFormat
     */
    boolean xmlFormat;

    /**
     * Field done
     */
    volatile boolean done = false;

    /**
     * Field tmodel
     */
    volatile long elapsed = 0;
    
    /**
     * Field tableIndex
     */
    int tableIndex = 0;

    /**
     * Field type
     */
    String type = null;

    /**
     * Field myConnection
     */
    Connection myConnection = null;

	private String response = "";

	private String className;

	private MocktailMode mocktailMode;

	private String methodName;

	private Object cacheLocation;

    private boolean cachingOn;


    /**
     * Constructor SocketRR
     *
     * @param c
     * @param inputSocket
     * @param inputStream
     * @param outputSocket
     * @param outputStream
     * @param _textArea
     * @param format
     * @param tModel
     * @param index
     * @param type
     * @param mocktailMode 
     * @param className 
     * @param methodName 
     * @param cachingOn 
     * @param slowLink
     */
    public SocketRR(Connection c, Socket inputSocket,
                    InputStream inputStream, Socket outputSocket,
                    OutputStream outputStream, 
                    boolean format,  int index,
                    final String type, String className, String methodName, MocktailMode mocktailMode, boolean cachingOn, String cacheLocation) {
        inSocket = inputSocket;
        this.inputStream = inputStream;
        this.outSocket = outputSocket;
        this.outputStream = outputStream;
        this.xmlFormat = format;
        this.tableIndex = index;
        this.type = type;
        this.myConnection = c;
		this.className = className;
		this.methodName = methodName;
		this.mocktailMode = mocktailMode;
		this.cacheLocation = cacheLocation;
		this.cachingOn = cachingOn;
        start();
    }

    /**
     * Method isDone
     *
     * @return boolean
     */
    public boolean isDone() {
        return done;
    }

    public String getElapsed() {
    		return String.valueOf(elapsed);
    }
    
    /**
     * Method run
     */
    public void run() {
        try {
            byte[] buffer = new byte[4096];
            byte[] tmpbuffer = new byte[8192];
            int saved = 0;
            int len;
            int i1, i2;
            int i;
            int tabWidth = 3;
            boolean atMargin = true;
            int thisIndent = -1, nextIndent = -1, previousIndent = -1;
            long start = System.currentTimeMillis();
            a:
            for (; ;) {
            	
                elapsed = System.currentTimeMillis() - start;
            	
                if (done) {
                    break;
                }
                
                // try{
                // len = in.available();
                // }catch(Exception e){len=0;}
                len = buffer.length;

                // Used to be 1, but if we block it doesn't matter
                // however 1 will break with some servers, including apache
                if (len == 0) {
                    len = buffer.length;
                }
                if (saved + len > buffer.length) {
                    len = buffer.length - saved;
                }
                int len1 = 0;
                while (len1 == 0) {
                    try {
                        len1 = inputStream.read(buffer, saved, len);
                    } catch (Exception ex) {
                    	ex.printStackTrace();
                        if (done && (saved == 0)) {
                            break a;
                        }
                        len1 = -1;
                        break;
                    }
                }
                len = len1;
                if ((len == -1) && (saved == 0)) {
                    break;
                }
                if (len == -1 || inputStream.available() == 0) {
                    done = true;
                }
                
               

                // No matter how we may (or may not) format it, send it
                // on unformatted - we don't want to mess with how its
                // sent to the other side, just how its displayed
                if ((outputStream != null) && (len > 0)) {
                    outputStream.write(buffer, saved, len);
                    response = response + new String(buffer, saved, len);
                    System.out.println("The response is:"+response+"<>");
                    if(cachingOn && (response.contains("</soap:Envelope>")|| response.contains("</soapenv:Envelope>"))){
                    	saveInMocktailRepository();
                    }
                }
                
                
               if (xmlFormat) {

                    // Do XML Formatting
                    boolean inXML = false;
                    int bufferLen = saved;
                    if (len != -1) {
                        bufferLen += len;
                    }
                    i1 = 0;
                    i2 = 0;
                    saved = 0;
                    for (; i1 < bufferLen; i1++) {

                        // Except when we're at EOF, saved last char
                        if ((len != -1) && (i1 + 1 == bufferLen)) {
                            saved = 1;
                            break;
                        }
                        thisIndent = -1;
                        if ((buffer[i1] == '<')
                                && (buffer[i1 + 1] != '/')) {
                            previousIndent = nextIndent++;
                            thisIndent = nextIndent;
                            inXML = true;
                        }
                        if ((buffer[i1] == '<')
                                && (buffer[i1 + 1] == '/')) {
                            if (previousIndent > nextIndent) {
                                thisIndent = nextIndent;
                            }
                            previousIndent = nextIndent--;
                            inXML = true;
                        }
                        if ((buffer[i1] == '/')
                                && (buffer[i1 + 1] == '>')) {
                            previousIndent = nextIndent--;
                            inXML = true;
                        }
                        if (thisIndent != -1) {
                            if (thisIndent > 0) {
                                tmpbuffer[i2++] = (byte) '\n';
                            }
                            for (i = tabWidth * thisIndent; i > 0; i--) {
                                tmpbuffer[i2++] = (byte) ' ';
                            }
                        }
                        atMargin = ((buffer[i1] == '\n')
                                || (buffer[i1] == '\r'));
                        if (!inXML || !atMargin) {
                            tmpbuffer[i2++] = buffer[i1];
                        }
                    }

                    // Shift saved bytes to the beginning
                    for (i = 0; i < saved; i++) {
                        buffer[i] = buffer[bufferLen - saved + i];
                    }
                } 
               
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            done = true;
            try {
                if (outputStream != null) {
                    outputStream.flush();
                    if (null != outSocket) {
                        outSocket.shutdownOutput();
                    } else {
                        outputStream.close();
                    }
                    outputStream = null;
                }
            } catch (Exception e) {
            }
            try {
                if (inputStream != null) {
                    if (inSocket != null) {
                        inSocket.shutdownInput();
                    } else {
                        inputStream.close();
                    }
                    inputStream = null;
                }
            } catch (Exception e) {
            }
            myConnection.wakeUp();
        }
    }

	private void saveInMocktailRepository() {
		if(mocktailMode == MocktailMode.RECORDING_NEW)
			clearObjectOnDisk(className);
		if(MocktailMode.RECORDING == mocktailMode || mocktailMode == MocktailMode.RECORDING_NEW){
			saveObjectInRepository();
		}
	}

	private void saveObjectInRepository() {
		String packageName = className.substring(0, className.lastIndexOf("."));
		String location = System.getProperty("user.dir") + File.separator 
				+ cacheLocation + File.separator 
				+ packageName.replaceAll("\\.", File.separator);
		String objectId = className.substring(className.lastIndexOf(".")+1)+"."+methodName;

		// Verifying if root recording directory where all recordings exist is already their or not

		if (!(new File(location)).exists()) {
			(new File(location)).mkdirs();
		}
		ObjectRepository objectRepository = new DiskObjectRepository();
		objectRepository.saveObject(response, objectId, location);
	}

	private void clearObjectOnDisk(String objectId2) {
		String packageName = className.substring(0, className.lastIndexOf("."));
		String location = System.getProperty("user.dir") + File.separator 
				+ cacheLocation + File.separator 
				+ packageName.replaceAll("\\.", File.separator);
		String objectId = className.substring(className.lastIndexOf(".")+1)+"."+methodName;
		
		ObjectRepository objectRepository = new DiskObjectRepository();
		objectRepository.clearObjectIfAvailable(objectId, location);
	}

	public String getPayload(){
		return response;
	}

    /**
     * Method halt
     */
    public void halt() {
    	log.info("SocketRR.halt() called");
        try {
            if (inSocket != null) {
                inSocket.close();
            }
            if (outSocket != null) {
                outSocket.close();
            }
            inSocket = null;
            outSocket = null;
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            inputStream = null;
            outputStream = null;
            done = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
