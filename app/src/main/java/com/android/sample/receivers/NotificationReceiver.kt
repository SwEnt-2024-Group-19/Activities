import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.android.sample.R

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val activityId = intent.getIntExtra("activityId", 0)
        val activityName = intent.getStringExtra("activityName") ?: "Activity"

        val notification = NotificationCompat.Builder(context, "activity_reminders")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Upcoming Activity Tomorrow")
            .setContentText("Reminder: $activityName")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(activityId, notification)
    }
}