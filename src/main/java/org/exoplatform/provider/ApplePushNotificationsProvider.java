package org.exoplatform.provider;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLHandshakeException;

import com.relayrides.pushy.apns.ApnsEnvironment;
import com.relayrides.pushy.apns.ExpiredToken;
import com.relayrides.pushy.apns.FailedConnectionListener;
import com.relayrides.pushy.apns.FeedbackConnectionException;
import com.relayrides.pushy.apns.PushManager;
import com.relayrides.pushy.apns.PushManagerFactory;
import com.relayrides.pushy.apns.RejectedNotificationListener;
import com.relayrides.pushy.apns.RejectedNotificationReason;
import com.relayrides.pushy.apns.util.ApnsPayloadBuilder;
import com.relayrides.pushy.apns.util.SimpleApnsPushNotification;
import com.relayrides.pushy.apns.util.TokenUtil;

/**
 * Hello world!
 *
 */
public class ApplePushNotificationsProvider implements RejectedNotificationListener<SimpleApnsPushNotification>, 
														FailedConnectionListener<SimpleApnsPushNotification>
{
	/**
	 * 
	 */
	private PushManager<SimpleApnsPushNotification> pushManager = null;
	
	/**
	 * 
	 */
	public ApplePushNotificationsProvider()
	{
		createPushManager("/Users/philippeexo/Work/eXo/Push-Notifications-POC/APNs/PushNotificationsCertificates.p12", "amanaplanacanalpanama");
		registerErrorListeners();
	}
	
	/**
	 * 
	 * @param pathToCertificate
	 * @param password
	 */
	public ApplePushNotificationsProvider(String pathToCertificate, String password)
	{
		createPushManager(pathToCertificate, password);
		registerErrorListeners();
	}
	
	/**
	 * 
	 * @param pathToCertificate
	 * @param password
	 */
	public void createPushManager(String pathToCertificate, String password)
	{
		System.out.println("* INFO * Create and start the Push Manager");
		
		PushManagerFactory<SimpleApnsPushNotification> pushManagerFactory = null;
		
		try {
			pushManagerFactory = new PushManagerFactory<SimpleApnsPushNotification>(
			        ApnsEnvironment.getSandboxEnvironment(),
			        PushManagerFactory.createDefaultSSLContext(pathToCertificate, password));
			
			pushManager = pushManagerFactory.buildPushManager();
			
			pushManager.start();
			
			System.out.println("\tOK");
			
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void terminate() 
	{
		System.out.println("* INFO * Shutting down the Push Manager");
		try {
			pushManager.shutdown();
			Thread.sleep(3500);
			System.out.println("\tOK");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void retrieveDisabledDevices()
	{
		System.out.println("* INFO * List of disabled devices");
		try {
			for (final ExpiredToken token : pushManager.getExpiredTokens()) {
			    // Stop sending push notifications to each expired token if the expiration
			    // time is after the last time the app registered that token.
				System.out.println("* "+TokenUtil.tokenBytesToString(token.getToken()));
			}
			System.out.println("\tOK");
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (FeedbackConnectionException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 */
	private void registerErrorListeners()
	{
		if (pushManager != null && pushManager.isStarted()) {
			System.out.println("* INFO * Register rejected notification and failed connection listeners");
			pushManager.registerRejectedNotificationListener(this);
			pushManager.registerFailedConnectionListener(this);
			System.out.println("\tOK");
		}
	}
	
	/**
	 * 
	 */
	public void sendHelloWorldNotification()
	{
		System.out.println("* INFO * Sending Hello, World.");
		final byte[] token = TokenUtil.tokenStringToByteArray(
			    "<1f71d45e f10d82fe 258e65c3 978ade4a 8ef852b8 497e1165 bb74c76c c8103428>");

		final ApnsPayloadBuilder payloadBuilder = new ApnsPayloadBuilder();

		payloadBuilder.setAlertBody("Hello, World.");

		final String payload = payloadBuilder.buildWithDefaultMaximumLength();

		try {
			pushManager.getQueue().put(new SimpleApnsPushNotification(token, payload));
			System.out.println("\tOK");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
    
    /**
     * 
     */
    
	@Override
	public void handleRejectedNotification(
			PushManager<? extends SimpleApnsPushNotification> pushMan,
			SimpleApnsPushNotification notification, RejectedNotificationReason reason) {
		
        System.out.format("* WARN * %s was rejected with rejection reason %s\n", notification, reason);
		
	}

	@Override
	public void handleFailedConnection(
			PushManager<? extends SimpleApnsPushNotification> pushMan,
			Throwable cause) {
		
		System.out.format("* WARN * Connection was closed with cause %s\n", cause);
		
		if (cause instanceof SSLHandshakeException) {
            // This is probably a permanent failure, and we should shut down
            // the PushManager.
        }
		
	}
}
