package io.github.jd1378.otphelper.data

import io.github.jd1378.otphelper.data.local.PreferenceDataStoreConstants
import io.github.jd1378.otphelper.data.local.PreferenceDataStoreHelper
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class IgnoredNotifSetRepository
@Inject
constructor(private val preferenceDataStoreHelper: PreferenceDataStoreHelper) {

  fun getIgnoredNotifSetStream(): Flow<Set<String>> {
    return preferenceDataStoreHelper.getPreference(
        PreferenceDataStoreConstants.IGNORED_NOTIF_SET, emptySet())
  }

  suspend fun getIgnoredNotifSet(): Set<String> {
    return preferenceDataStoreHelper.getFirstPreference(
        PreferenceDataStoreConstants.IGNORED_NOTIF_SET, emptySet())
  }

  suspend fun setIgnoredNotifSet(value: Set<String>) {
    return preferenceDataStoreHelper.putPreference(
        PreferenceDataStoreConstants.IGNORED_NOTIF_SET, value)
  }

  suspend fun addIgnoredNotif(ignoredNotif: String) {
    var ignores = this.getIgnoredNotifSet().toMutableSet()
    ignores.add(ignoredNotif)
    this.setIgnoredNotifSet(ignores)
  }

  suspend fun removeIgnoredNotif(ignoredNotif: String) {
    var ignores = this.getIgnoredNotifSet().toMutableSet()
    ignores.remove(ignoredNotif)
    this.setIgnoredNotifSet(ignores)
  }

  suspend fun hasIgnoredNotif(ignoredNotif: String): Boolean {
    return this.getIgnoredNotifSet().contains(ignoredNotif)
  }
}
