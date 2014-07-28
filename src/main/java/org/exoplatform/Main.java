package org.exoplatform;

import org.exoplatform.provider.ApplePushNotificationsProvider;
import org.exoplatform.provider.GoogleCloudMessagingProvider;

public class Main {

	public static void main(String[] args) {
		
		System.out.println("=========================================");
        System.out.println("Starting the Push Notifications Providers");
        System.out.println("=========================================");
		
        ApplePushNotificationsProvider appleProvider = new ApplePushNotificationsProvider();
        appleProvider.sendHelloWorldNotification();
        appleProvider.retrieveDisabledDevices();
//        provider.terminate();
        
        GoogleCloudMessagingProvider googleProvider = new GoogleCloudMessagingProvider();
        googleProvider.sendHelloWorldNotification();
        googleProvider.retrieveDisabledDevices();
        googleProvider.terminate();
		
        System.out.println("====");
        System.out.println("Done");
        System.out.println("====");
        
//        System.exit(0);

	}

}
