package org.exoplatform.provider;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class GoogleCloudMessagingProvider {
	
	private CloseableHttpClient httpClient;
	
	private String API_KEY = "";
	private String NEXUS_7_REG_ID = "";
	
	public GoogleCloudMessagingProvider()
	{
		initProvider();
	}
	
	private void initProvider()
	{
		System.out.println("* INFO * Create the HTTP Client");
		httpClient = HttpClients.createDefault();
		Properties params = loadPropertiesFromFileAtPath("/Users/philippeexo/Work/eXo/Push-Notifications-POC/GCM/poc-infos.properties");
		API_KEY = params.getProperty("poc.project.apikey");
		NEXUS_7_REG_ID = params.getProperty("poc.device.nexus7");
		System.out.println("\tOK");
	}
	
	private HttpPost createRequestTo(String id)
	{
		HttpPost request = null;
		
		try {
			URI uri = new URIBuilder()
				.setScheme("https")
				.setHost("android.googleapis.com")
				.setPath("/gcm/send")
				.build();
			
			request = new HttpPost(uri);
			request.addHeader("Authorization", "key="+API_KEY);
			request.addHeader("Content-Type", "application/json");
			
			JSONObject jsonBody = new JSONObject();
			JSONArray ids = new JSONArray();
			ids.add(id);
			jsonBody.put("registration_ids", ids);
			JSONObject jsonData = new JSONObject();
			jsonData.put("param", "Hello World");
			jsonBody.put("data", jsonData);
			//jsonBody.put("dry_run", true);
			StringEntity body = new StringEntity(jsonBody.toJSONString());
			body.setContentType("application/json");
			request.setEntity(body);
			
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return request;
		
	}
	
	public void sendHelloWorldNotification()
	{
		System.out.println("* INFO * Sending Hello, World.");
		try {
			
			HttpPost request = createRequestTo(NEXUS_7_REG_ID);
			HttpResponse response = httpClient.execute(request);
			int responseCode = response.getStatusLine().getStatusCode();
			
			System.out.println(EntityUtils.toString(response.getEntity()));
			
			if (responseCode == 200) {
				System.out.println("\tOK");
			} else {
				System.out.println("\tStatus "+responseCode);
			}
			
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void retrieveDisabledDevices()
	{
		
	}

	public void terminate()
	{
		try {
			httpClient.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
	private Properties loadPropertiesFromFileAtPath(String path)
	{
			Properties prop = new Properties();
			try {
				prop.load(new FileInputStream(path));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return prop;
	}
	
}
