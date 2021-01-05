package ua.itaysonlab.catogram.vkui.icon_replaces

import android.util.SparseIntArray
import androidx.core.os.bundleOf
import org.telegram.messenger.R
import ua.itaysonlab.catogram.vkui.newSparseInt

class VkIconReplace: BaseIconReplace() {
    override val replaces: SparseIntArray = newSparseInt(
            // Settings - Main
            R.drawable.menu_settings to R.drawable.settings_outline_28,
            R.drawable.menu_language to R.drawable.globe_outline_28,
            R.drawable.menu_secret to R.drawable.lock_outline_28,
            R.drawable.menu_data to R.drawable.services_outline_28,
            R.drawable.menu_chats to R.drawable.messages_outline_28,
            R.drawable.menu_folders to R.drawable.cube_box_outline_28,
            R.drawable.menu_support2 to R.drawable.help_outline_28,
            R.drawable.menu_help to R.drawable.help_outline_28,
            R.drawable.menu_policy to R.drawable.check_shield_outline_28,
            // Settings - CG
            R.drawable.msg_theme to R.drawable.palette_outline_28,
            R.drawable.group_log to R.drawable.grid_square_outline_28,
            R.drawable.round_attach_money_24 to R.drawable.payment_card_outline_28,
            // Messages
            R.drawable.input_bot2 to R.drawable.keyboard_bots_outline_28,
            R.drawable.input_notify_off to R.drawable.notification_disable_outline_28,
            R.drawable.input_notify_on to R.drawable.notifications_28,
            R.drawable.input_attach to R.drawable.attach_outline_28,
            R.drawable.input_mic to R.drawable.voice_outline_28,
            R.drawable.input_video to R.drawable.videocam_outline_28,
            R.drawable.input_schedule to R.drawable.calendar_outline_28,
            R.drawable.input_keyboard to R.drawable.keyboard_outline_28,
            R.drawable.input_bot1 to R.drawable.chevron_right_circle_outline_28,
            R.drawable.input_smile to R.drawable.smile_outline_28,
            R.drawable.input_sticker to R.drawable.sticker_outline_28,
            R.drawable.input_gif to R.drawable.picture_outline_28,
    )
}
