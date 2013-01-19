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

package org.apache.ws.commons.tcpmon;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.mocktail.repository.DiskObjectRepository;
import org.mocktail.xml.domain.MocktailMode;

/**
 * a connection listens to a single current connection
 */
class Connection extends Thread {
	public static final String DEFAULT_CACHE_LOCATION = "src/test/resources";

	/**
	 * Field active
	 */
	boolean active;

	/**
	 * Field fromHost
	 */
	String fromHost;

	/**
	 * Field time
	 */
	String time;

	/**
	 * Field elapsed time
	 */
	long elapsedTime;

	/**
	 * Field inputText
	 */
	Socket inSocket = null;

	/**
	 * Field outSocket
	 */
	Socket outSocket = null;

	/**
	 * Field clientThread
	 */
	Thread clientThread = null;

	/**
	 * Field serverThread
	 */
	Thread serverThread = null;

	/**
	 * Field rr1
	 */
	SocketRR rr1 = null;

	/**
	 * Field rr2
	 */
	SocketRR rr2 = null;

	/**
	 * Field inputStream
	 */
	InputStream inputStream = null;

	/**
	 * Field HTTPProxyHost
	 */
	String HTTPProxyHost = null;

	/**
	 * Field HTTPProxyPort
	 */
	int HTTPProxyPort = 80;

	private String targetHost;

	private int targetPort;

	private boolean proxySelected;

	private int listenPort;

	private boolean xmlFormatSelected;

	private String className;

	private MocktailMode mocktailMode;

	private String methodName;

	private String cacheLocation;

	public boolean isXmlFormatSelected() {
		return xmlFormatSelected;
	}

	public void setXmlFormatSelected(boolean xmlFormatSelected) {
		this.xmlFormatSelected = xmlFormatSelected;
	}

	/**
	 * Constructor Connection
	 * 
	 * @param l
	 */
	public Connection(Listener l, String targetHost, int targetPort,
			int listenPort) {
		HTTPProxyHost = l.HTTPProxyHost;
		HTTPProxyPort = l.HTTPProxyPort;
		this.targetHost = targetHost;
		this.targetPort = targetPort;
		this.listenPort = listenPort;
	}

	/**
	 * Constructor Connection
	 * 
	 * @param l
	 * @param s
	 * @param mocktailMode
	 * @param objectId
	 * @param methodName 
	 */
	public Connection(Socket s, String targetHost, int targetPort,
			int listenPort, String objectId, String methodName, MocktailMode mocktailMode) {
		inSocket = s;
		this.targetHost = targetHost;
		this.targetPort = targetPort;
		this.listenPort = listenPort;
		this.className = objectId;
		this.methodName = methodName;
		this.mocktailMode = mocktailMode;
		start();
	}

	/**
	 * Constructor Connection
	 * 
	 * @param l
	 * @param in
	 */
	public Connection(InputStream in) {
		inputStream = in;
		start();
	}

	/**
	 * Method run
	 */
	public void run() {
		cacheLocation = MocktailConfig.INSTANCE.getProperty("recordingDir");
		if(cacheLocation  == null || "".equals(cacheLocation)){
			cacheLocation = DEFAULT_CACHE_LOCATION;
		}
		try {
			active = true;
			HTTPProxyHost = System.getProperty("http.proxyHost");
			if ((HTTPProxyHost != null) && HTTPProxyHost.equals("")) {
				HTTPProxyHost = null;
			}
			if (HTTPProxyHost != null) {
				String tmp = System.getProperty("http.proxyPort");
				if ((tmp != null) && tmp.equals("")) {
					tmp = null;
				}
				if (tmp == null) {
					HTTPProxyPort = 80;
				} else {
					HTTPProxyPort = Integer.parseInt(tmp);
				}
			}
			if (inSocket != null) {
				fromHost = (inSocket.getInetAddress()).getHostName();
			} else {
				fromHost = "resend";
			}
			String dateformat = TCPMon.getMessage("dateformat00",
					"yyyy-MM-dd HH:mm:ss");
			DateFormat df = new SimpleDateFormat(dateformat);
			time = df.format(new Date());
			// FIXME
			InputStream incomingStream = inputStream;
			OutputStream inSocketOutputStream = null;
			InputStream outSocketInputStream = null;
			OutputStream outSocketOutputStream = null;
			if (incomingStream == null) {
				incomingStream = inSocket.getInputStream();
			}
			if (inSocket != null) {
				inSocketOutputStream = inSocket.getOutputStream();
			}
			String bufferedData = null;
			StringBuffer buf = null;
			// FIXME
			if (isProxySelected()) {
				bufferedData = processForProxy(incomingStream);
			} else {

				//
				// Change Host: header to point to correct host
				//
				byte[] b1 = new byte[1];
				buf = new StringBuffer();
				String s1;
				String lastLine = null;
				for (;;) {
					int len;
					len = incomingStream.read(b1, 0, 1);
					if (len == -1) {
						break;
					}
					s1 = new String(b1);
					buf.append(s1);
					if (b1[0] != '\n') {
						continue;
					}

					// we have a complete line
					String line = buf.toString();
					buf.setLength(0);

					// check to see if we have found Host: header
					if (line.startsWith("Host: ")) {
						// we need to update the hostname to target host
						String newHost = "Host: " + targetHost + ":"
								+ listenPort + "\r\n";
						bufferedData = bufferedData.concat(newHost);
						break;
					}

					// add it to our headers so far
					if (bufferedData == null) {
						bufferedData = line;
					} else {
						bufferedData = bufferedData.concat(line);
					}

					// failsafe
					if (line.equals("\r\n")) {
						break;
					}
					if ("\n".equals(lastLine) && line.equals("\n")) {
						break;
					}
					lastLine = line;
				}
				if (bufferedData != null) {
					int idx = (bufferedData.length() < 50) ? bufferedData
							.length() : 50;
					s1 = bufferedData.substring(0, idx);
					int i = s1.indexOf('\n');
					if (i > 0) {
						s1 = s1.substring(0, i - 1);
					}
					s1 = s1 + "                           "
							+ "                       ";
					s1 = s1.substring(0, 51);
				}
			}
			if (targetPort == -1) {
				targetPort = 80;
			}
			
			try {
				if(MocktailMode.RECORDING_NEW != mocktailMode){
					if(isObjectExistInCache()){
						writeResponseFromCache(inSocketOutputStream);
						return;
					}
				}
				outSocket = new Socket(targetHost, targetPort);
				outSocketOutputStream = outSocket.getOutputStream();
				if (bufferedData != null) {
					byte[] bytes = bufferedData.getBytes();
					System.err
							.println("The bytes to be written on output socket are:"
									+ new String(bytes, "UTF-8"));
					outSocketOutputStream.write(bytes);
				}
			} catch (ConnectException e) {
				e.printStackTrace();
				System.err
						.println("Could not connect to target host, instead trying to get from cache:"
								+ e.getMessage());
				writeResponseFromCache(inSocketOutputStream);
				return;
			}

			boolean format = isXmlFormat();

			// sends the request to endpoint
			// this is the channel to the endpoint
			rr1 = new SocketRR(this, inSocket, incomingStream, outSocket,
					outSocketOutputStream, format, 1, "request:", className, methodName,
					mocktailMode, cacheLocation);

			// gets the response from endpoint
			// create the response slow link from the inbound slow link
			// this is the channel from the endpoint
			outSocketInputStream = outSocket.getInputStream();
			rr2 = new SocketRR(this, outSocket, outSocketInputStream, inSocket,
					inSocketOutputStream, format, 0, "response:", className, methodName,
					mocktailMode, cacheLocation);

			while ((rr1 != null) || (rr2 != null)) {
				// Only loop as long as the connection to the target
				// machine is available - once that's gone we can stop.
				// The old way, loop until both are closed, left us
				// looping forever since no one closed the 1st one.

				if ((null != rr1) && rr1.isDone()) {
					System.err.println("DONEDONE");

					rr1 = null;
				}

				if ((null != rr2) && rr2.isDone()) {
					System.err.println("DONE");
					rr2 = null;
				}

				synchronized (this) {
					this.wait(100); // Safety just incase we're not told to wake
									// up.
				}
			}
			active = false;
		} catch (Exception e) {
			StringWriter st = new StringWriter();
			PrintWriter wr = new PrintWriter(st);

			e.printStackTrace(wr);
			wr.close();
			System.out.println(st.toString());
			halt();
		}
	}
	
	private boolean isObjectExistInCache() {
		String location = System.getProperty("user.dir")
				+ File.separator + cacheLocation;
		DiskObjectRepository objectRepository = new DiskObjectRepository();
		return objectRepository.objectAlreadyExist(className, location);
	}

	private void writeResponseFromCache(OutputStream inSocketOutputStream) throws IOException{
		String packageName = className.substring(0, className.lastIndexOf("."));
		
		String location = System.getProperty("user.dir") + File.separator 
				+ cacheLocation + File.separator 
				+ packageName.replaceAll("\\.", File.separator);
		System.out.println("The class name is:"+className);
		String objectId = className.substring(className.lastIndexOf(".")+1)+"."+methodName;
		String response = getResponseFromDisk(location, objectId);
		System.err.println("The response is(((((:" + response);

		if ((inSocketOutputStream != null)) {
			inSocketOutputStream.write(response.getBytes());
		}
	}

	private String getResponseFromDisk(String location, String objectId2) {
		DiskObjectRepository objectRepository = new DiskObjectRepository();
		return (String) objectRepository.getObject(objectId2, location);
	}


	private boolean isXmlFormat() {
		return xmlFormatSelected;
	}

	private String processForProxy(InputStream tmpIn1) throws IOException,
			MalformedURLException {
		String bufferedData;
		StringBuffer buf;
		// Check if we're a proxy
		byte[] b = new byte[1];
		buf = new StringBuffer();
		String s;
		for (;;) {
			int len;
			len = tmpIn1.read(b, 0, 1);
			if (len == -1) {
				break;
			}
			s = new String(b);
			buf.append(s);
			if (b[0] != '\n') {
				continue;
			}
			break;
		}
		bufferedData = buf.toString();
		if (bufferedData.startsWith("GET ") || bufferedData.startsWith("POST ")
				|| bufferedData.startsWith("PUT ")
				|| bufferedData.startsWith("DELETE ")) {
			int start, end;
			URL url;
			start = bufferedData.indexOf(' ') + 1;
			while (bufferedData.charAt(start) == ' ') {
				start++;
			}
			end = bufferedData.indexOf(' ', start);
			String urlString = bufferedData.substring(start, end);
			if (urlString.charAt(0) == '/') {
				urlString = urlString.substring(1);
			}
			// FIXME
			if (isProxySelected()) {
				url = new URL(urlString);
				targetHost = url.getHost();
				targetPort = url.getPort();
				if (targetPort == -1) {
					targetPort = 80;
				}
				bufferedData = bufferedData.substring(0, start) + url.getFile()
						+ bufferedData.substring(end);
			} else {
				url = new URL("http://" + targetHost + ":" + targetPort + "/"
						+ urlString);
				bufferedData = bufferedData.substring(0, start)
						+ url.toExternalForm() + bufferedData.substring(end);
				targetHost = HTTPProxyHost;
				targetPort = HTTPProxyPort;
			}
		}
		return bufferedData;
	}

	private boolean isProxySelected() {
		return proxySelected;
	}

	/**
	 * Method wakeUp
	 */
	synchronized void wakeUp() {
		this.notifyAll();
	}

	/**
	 * Method halt
	 */
	public void halt() {
		try {
			if (rr1 != null) {
				rr1.halt();
			}
			if (rr2 != null) {
				rr2.halt();
			}
			if (inSocket != null) {
				inSocket.close();
			}
			inSocket = null;
			if (outSocket != null) {
				outSocket.close();
			}
			outSocket = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method remove
	 */
	public void remove() {
		try {
			halt();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
