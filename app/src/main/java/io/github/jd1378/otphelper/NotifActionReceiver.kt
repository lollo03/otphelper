package io.github.jd1378.otphelper

import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.AndroidEntryPoint
import io.github.jd1378.otphelper.data.IgnoredNotifSetRepository
import io.github.jd1378.otphelper.utils.Clipboard
import io.github.jd1378.otphelper.utils.NotificationSender
import javax.inject.Inject
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotifActionReceiver : BroadcastReceiver() {

  @Inject lateinit var ignoredNotifSetRepository: IgnoredNotifSetRepository

  companion object {
    const val INTENT_ACTION_CODE_COPY = "io.github.jd1378.otphelper.actions.code_copy"
    const val INTENT_ACTION_IGNORE_NOTIFICATION = "io.github.jd1378.otphelper.actions.ignore_notif"

    fun getActiveNotification(context: Context, notificationId: Int): Notification? {
      val notificationManager =
          context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      val barNotifications = notificationManager.activeNotifications
      return barNotifications.firstOrNull { it.id == notificationId }?.notification
    }
  }

  override fun onReceive(context: Context?, intent: Intent?) {
    if (context === null || intent === null) return

    if (intent.action == INTENT_ACTION_CODE_COPY) {
      var notif = getActiveNotification(context, R.id.code_detected_notify_id)
      if (notif != null) {
        var code = notif.extras.getString("code")

        if (code != null) {
          NotificationSender.sendDetectedNotif(
              context, notif.extras, code, copied = Clipboard.copyCodeToClipboard(context, code))
        }
      }
    }

    if (intent.action == INTENT_ACTION_IGNORE_NOTIFICATION) {
      var notif = getActiveNotification(context, R.id.code_detected_notify_id)
      if (notif != null) {

        var ignoreWord = notif.extras.getString("ignore_word")

        if (ignoreWord != null) {

          @OptIn(DelicateCoroutinesApi::class)
          GlobalScope.launch { ignoredNotifSetRepository.addIgnoredNotif(ignoreWord) }

          NotificationManagerCompat.from(context).cancel(R.id.code_detected_notify_id)

          Toast.makeText(context, R.string.wont_detect_code_from_this_notif, Toast.LENGTH_LONG)
              .show()
        }
      }
    }

    var cancelNotifId = intent.getIntExtra("cancel_notif_id", -1)
    if (cancelNotifId != -1) {
      NotificationManagerCompat.from(context).cancel(cancelNotifId)
    }
  }
}
