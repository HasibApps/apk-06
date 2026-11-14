package com.example.ytdl

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.PowerManager

/** Foreground service: keeps the app process alive (with a CPU wake-lock) while downloads run in the background. */
class DownloadService : Service() {
    private var wl: PowerManager.WakeLock? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        DM.init(this)
        val n = DM.buildNotif(this)
        try {
            if (Build.VERSION.SDK_INT >= 29) startForeground(DM.NID, n, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
            else startForeground(DM.NID, n)
        } catch (_: Throwable) { stopSelf(); return START_NOT_STICKY }
        try {
            if (wl == null) {
                wl = getSystemService(PowerManager::class.java)
                    ?.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "ytdl:download")
                    ?.apply { setReferenceCounted(false) }
            }
            if (wl?.isHeld != true) wl?.acquire(6 * 60 * 60 * 1000L)
        } catch (_: Throwable) {}
        return START_NOT_STICKY
    }

    // User swiped the app away from recents → downloads keep running (foreground service + process stay alive).
    override fun onTaskRemoved(rootIntent: Intent?) { super.onTaskRemoved(rootIntent) }

    override fun onDestroy() {
        try { if (wl?.isHeld == true) wl?.release() } catch (_: Throwable) {}
        super.onDestroy()
    }
}
