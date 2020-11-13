package ua.itaysonlab.catogram

import android.app.Activity
import android.content.SharedPreferences
import com.google.android.exoplayer2.util.Log
import org.telegram.messenger.ApplicationLoader
import org.telegram.messenger.MessagesController
import ua.itaysonlab.CatogramInit
import ua.itaysonlab.catogram.preferences.ktx.boolean
import ua.itaysonlab.catogram.preferences.ktx.int
import ua.itaysonlab.catogram.stickerkang.KangBridge
import ua.itaysonlab.catogram.ui.CatogramToasts
import ua.itaysonlab.catogram.vkui.icon_replaces.BaseIconReplace
import ua.itaysonlab.catogram.vkui.icon_replaces.NoIconReplace
import ua.itaysonlab.catogram.vkui.icon_replaces.VkIconReplace

object CatogramConfig {
    private val sharedPreferences: SharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE)

    var hideProxySponsor by sharedPreferences.boolean("advanced_hideproxysponsor", true)

    var hidePhoneNumber by sharedPreferences.boolean("advanced_hidephonenumber", true)
    var forceNewYear by sharedPreferences.boolean("advanced_forcenewyear", false)
    var forcePacman by sharedPreferences.boolean("advanced_forcepacman", false)
    var noRounding by sharedPreferences.boolean("advanced_norounding", false)
    var systemFonts by sharedPreferences.boolean("advanced_systemfonts", false)
    var systemFontsTT by sharedPreferences.boolean("advanced_systemfonts_tt", false)
    var noVibration by sharedPreferences.boolean("advanced_novibration", false)
    var forceNewYearDrawer by sharedPreferences.boolean("advanced_nydrawer", false)
    var noTyping by sharedPreferences.boolean("advanced_notyping", false)
    var drawerAvatar by sharedPreferences.boolean("cg_drawer_avatar", false)
    var flatStatusbar by sharedPreferences.boolean("cg_flat_statusbar", false)
    var flatActionbar by sharedPreferences.boolean("cg_flat_actionbar", false)
    var drawerBlur by sharedPreferences.boolean("cg_drawer_blur", false)
    var drawerDarken by sharedPreferences.boolean("cg_drawer_darken", false)
    var controversiveNoReading by sharedPreferences.boolean("cg_ct_read", false)
    var controversiveNoSecureFlag by sharedPreferences.boolean("cg_ct_flag", false)
    var useBiometricPrompt by sharedPreferences.boolean("cg_newbiometric", true)
    var accentNotification by sharedPreferences.boolean("cg_notification_accent", false)
    var contactsNever by sharedPreferences.boolean("contactsNever", false)
    var searchInActionbar by sharedPreferences.boolean("cg_chats_searchbar", false)
    var confirmCalls by sharedPreferences.boolean("cg_confirmcalls", false)
    var profiles_noEdgeTapping by sharedPreferences.boolean("cg_prof_edge", false)
    var profiles_openOnTap by sharedPreferences.boolean("cg_prof_open", false)
    var profiles_alwaysExpand by sharedPreferences.boolean("cg_prof_expand", false)
    var hideKeyboardOnScroll by sharedPreferences.boolean("cg_hidekbd", false)
    var newTabs_noUnread by sharedPreferences.boolean("cg_notabnum", false)
    var newTabs_hideAllChats by sharedPreferences.boolean("cg_ntallchats", false)
    var forceSVDrawer by sharedPreferences.boolean("cg_sv_drawer", false)
    var forceHLDrawer by sharedPreferences.boolean("cg_hl_drawer", false)

    var redesign_messageOption by sharedPreferences.int("cg_messageOption", 0)
    var redesign_iconOption by sharedPreferences.int("cg_iconoption", 0)
    var redesign_SlideDrawer by sharedPreferences.boolean("cg_redesign_slidedrawer", false)

    var slider_stickerAmplifier by sharedPreferences.int("cg_stickamplifier", 100)
    var archiveOnPull by sharedPreferences.boolean("cg_archonpull", true)
    var useCupertinoLib by sharedPreferences.boolean("advanced_cupertino", false)
    var useTgxMenuSlide by sharedPreferences.boolean("advanced_tgxslide", false)
    var useTgxMenuSlideSheet by sharedPreferences.boolean("advanced_tgxslide_sheet", false)
    var useAirUiPopup by sharedPreferences.boolean("advanced_airui", false)

    var forwardNoAuthorship by sharedPreferences.boolean("cg_forward_no_authorship", false)
    var msgForwardDate by sharedPreferences.boolean("cg_msg_fwd_date", false)
    var forceLeftFab by sharedPreferences.boolean("cg_fab_invertgravity", false)

    var newTabs_iconsV2_mode by sharedPreferences.int("cg_tabs_v2", 0)
    var hqVoice by sharedPreferences.boolean("cg_hq", false)

    var iconReplacement by sharedPreferences.int("cg_iconpack", 0)
    var hideUserIfBlocked by sharedPreferences.boolean("cg_hide_msg_if_blocked", false)
    var oldNotificationIcon by sharedPreferences.boolean("cg_old_notification", false)

    fun getIconReplacement(): BaseIconReplace {
        return when (iconReplacement) {
            1 -> VkIconReplace()
            else -> NoIconReplace()
        }
    }

    //var flatChannelStyle by sharedPreferences.boolean("cg_flat_channels", false)
    var flatChannelStyle = true
    lateinit var kangBridge: KangBridge

    init {
        CatogramInit.init(ApplicationLoader.applicationContext)
        CatogramToasts.init(sharedPreferences)
        kangBridge = KangBridge()
    }

    private fun putBoolean(key: String, value: Boolean) {
        val preferences = MessagesController.getGlobalMainSettings()
        val editor = preferences.edit()
        editor.putBoolean(key, value)
        editor.commit()
    }

    @JvmStatic
    fun shownContactsNever() {
        contactsNever = true
        putBoolean("contactsNever", true)
    }
}
