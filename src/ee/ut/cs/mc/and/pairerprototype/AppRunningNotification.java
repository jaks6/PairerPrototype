package ee.ut.cs.mc.and.pairerprototype;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class AppRunningNotification {

	int mNotificationId;
	Notification notification;
	Context context;

	/**
	 * Creates a notification icon which notifies the user that
	 * the app is running. returns user to the main activity
	 * when clicked. 
	 */
	public AppRunningNotification(Context context){

		this.mNotificationId = 001;
		this.context = context;



		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(context)
		.setSmallIcon(R.drawable.ic_launcher)
		.setContentTitle("Pairer")
		.setContentText("Click to return to activity!")
		.setOngoing(true);
		Intent resultIntent = new Intent(context, MainActivity.class);
		// Because clicking the notification opens a new ("special") activity, there's
		// no need to create an artificial back stack.
		PendingIntent resultPendingIntent =
				PendingIntent.getActivity(
						context,
						0,
						resultIntent,
						PendingIntent.FLAG_UPDATE_CURRENT
						);

		mBuilder.setContentIntent(resultPendingIntent); //associate PendingIntent with click of notification
		this.notification= mBuilder.build();


	}

	public void display(){
		getNotificationManagerService().notify(mNotificationId, notification);
	}

	public void remove(){
		getNotificationManagerService().cancel(mNotificationId);
	}

	private NotificationManager getNotificationManagerService(){
		// Gets an instance of the NotificationManager service
		return (NotificationManager) this.context.getSystemService(Context.NOTIFICATION_SERVICE);

		
	}

}
