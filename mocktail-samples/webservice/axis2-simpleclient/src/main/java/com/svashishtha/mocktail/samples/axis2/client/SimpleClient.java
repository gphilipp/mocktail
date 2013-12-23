package com.svashishtha.mocktail.samples.axis2.client;

import java.rmi.RemoteException;

import com.svashishtha.mocktail.MocktailMode;
import com.svashishtha.mocktail.samples.axis2.client.SimpleServiceStub.ConcatRequest;
import com.svashishtha.mocktail.samples.axis2.client.SimpleServiceStub.ConcatResponse;
import com.svashishtha.mocktail.tcpcache.TcpCache;

public class SimpleClient {
	public static void main(String[] args) throws RemoteException {
	    //SimpleServiceStub service = new SimpleServiceStub("http://localhost:8080/axis2/services/SimpleService");
		
	    //for tcpmon to work - start
		TcpCache tcpCache = new TcpCache(1234, "127.0.0.1", 8080, SimpleClient.class , "main", MocktailMode.RECORDING_NEW);
		SimpleServiceStub service = new SimpleServiceStub("http://localhost:1234/axis2/services/SimpleService");
		//for tcpmon to work - end
		
		ConcatRequest request = new ConcatRequest();
		request.setS1("abc");
		request.setS2("123");
		ConcatResponse response = service.concat(request);
		System.out.println(response.getConcatResponse());
//		tcpCache.halt();
	}
}