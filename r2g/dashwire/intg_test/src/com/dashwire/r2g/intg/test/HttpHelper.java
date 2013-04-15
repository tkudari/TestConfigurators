package com.dashwire.r2g.intg.test;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class HttpHelper {


	
	public HttpHelper() {
		super();
		//this.k = k;
	}	
	
	
	static public HttpResponse doHttpGet(String URL, String user, String pswd) {
		HttpResponse response = null;
	    // Create a new HttpClient and Post Header
	    DefaultHttpClient http = new DefaultHttpClient();
	    // Basic Auth
	    CredentialsProvider credProvider = new BasicCredentialsProvider();
	    credProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
	        new UsernamePasswordCredentials(user, pswd)); 
	    http.setCredentialsProvider(credProvider);
	    //Perform HTTP GET
	    HttpGet httpget = new HttpGet(URL);
	    Log.d("QA test", "doHttpGet() " + URL);
	    try {
	        // Execute HTTP Get Request
	        response = http.execute(httpget);
	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	    }
	    return response;
	}		
	
}
