package ua.itaysonlab.catogram

import android.view.View
import org.telegram.messenger.*
import org.telegram.tgnet.TLRPC
import org.telegram.ui.AvatarPreviewer
import org.telegram.ui.Cells.ChatMessageCell
import org.telegram.ui.ChatActivity
import org.telegram.ui.ChatRightsEditActivity
import org.telegram.ui.Components.ShareAlert

// I've created this so CG features can be injected in a source file with 1 line only (maybe)
// Because manual editing of drklo's sources harms your mental health.
object CGFeatureHooks {
    @JvmStatic
    fun hookHideWhenBlocked(cell: ChatMessageCell, act: ChatActivity) {
        if (!CGControversive.isControversiveFeaturesEnabled()) return
        if (CatogramConfig.hideUserIfBlocked && cell.messageObject.isFromUser) {
            val isBlocked = act.messagesController.blockePeers.indexOfKey((cell.messageObject.messageOwner.from_id as TLRPC.TL_peerUser).user_id) >= 0
            cell.visibility = if (isBlocked) View.GONE else View.VISIBLE
        } else {
            cell.visibility = View.VISIBLE
        }
    }

    @JvmStatic
    fun getProperNotificationIcon(): Int {
        return if (CatogramConfig.oldNotificationIcon) R.drawable.notification else R.drawable.cg_notification
    }

    @JvmStatic
    fun injectChatActivityMsgSlideAction(cf: ChatActivity, msg: MessageObject, isChannel: Boolean, classGuid: Int) {
        when (CatogramConfig.messageSlideAction) {
            0 -> {
                // Reply (default)
                cf.showFieldPanelForReply(msg)
            }
            1 -> {
                // Save to PM
                cf.sendMessagesHelper.sendMessage(arrayListOf(msg), UserConfig.getInstance(UserConfig.selectedAccount).clientUserId.toLong(), true, 0)
            }
            2 -> {
                // Share
                cf.showDialog(object: ShareAlert(cf.parentActivity, arrayListOf(msg), null, isChannel, null, false) {
                    override fun dismissInternal() {
                        super.dismissInternal()
                        AndroidUtilities.requestAdjustResize(cf.parentActivity, classGuid)
                        if (cf.chatActivityEnterView.visibility == View.VISIBLE) {
                            cf.fragmentView.requestLayout();
                        }
                        //cf.hideActionMode()
                        cf.updatePinnedMessageView(true)
                    }
                })

                AndroidUtilities.setAdjustResizeToNothing(cf.parentActivity, classGuid);
                cf.fragmentView.requestLayout();
            }
        }
    }

    @JvmStatic
    fun injectChatActivityAvatarArraySize(cf: ChatActivity): Int {
        var objs = 0

        if (ChatObject.canBlockUsers(cf.currentChat)) objs++
        if (ChatObject.hasAdminRights(cf.currentChat)) objs++
        if (ChatObject.canAddAdmins(cf.currentChat)) objs++

        return objs
    }

    @JvmStatic
    fun injectChatActivityAvatarArrayItems(cf: ChatActivity, arr: Array<AvatarPreviewer.MenuItem>, enableMention: Boolean) {
        var startPos = if (enableMention) 3 else 2

        if (ChatObject.canBlockUsers(cf.currentChat)) {
            arr[startPos] = AvatarPreviewer.MenuItem.CG_KICK
            startPos++
        }

        if (ChatObject.hasAdminRights(cf.currentChat)) {
            arr[startPos] = AvatarPreviewer.MenuItem.CG_CHANGE_PERMS
            startPos++
        }

        if (ChatObject.canAddAdmins(cf.currentChat)) {
            arr[startPos] = AvatarPreviewer.MenuItem.CG_CHANGE_ADMIN_PERMS
            startPos++
        }
    }

    @JvmStatic
    fun injectChatActivityAvatarOnClick(cf: ChatActivity, item: AvatarPreviewer.MenuItem, user: TLRPC.User) {
        when (item) {
            AvatarPreviewer.MenuItem.CG_KICK -> {
                cf.messagesController.deleteUserFromChat(cf.currentChat.id, cf.messagesController.getUser(user.id), cf.currentChatInfo)
            }
            AvatarPreviewer.MenuItem.CG_CHANGE_PERMS, AvatarPreviewer.MenuItem.CG_CHANGE_ADMIN_PERMS -> {
                val action = if (item == AvatarPreviewer.MenuItem.CG_CHANGE_PERMS) 1 else 0 // 0 - change admin rights

                val chatParticipant = cf.currentChatInfo.participants.participants.filter {
                    it.user_id == user.id
                }[0]

                var channelParticipant: TLRPC.ChannelParticipant? = null

                var canEditAdmin: Boolean
                val allowKick: Boolean
                val canRestrict: Boolean
                val editingAdmin: Boolean

                if (ChatObject.isChannel(cf.currentChat)) {
                    channelParticipant = (chatParticipant as TLRPC.TL_chatChannelParticipant).channelParticipant

                    canEditAdmin = ChatObject.canAddAdmins(cf.currentChat)
                    if (canEditAdmin && (channelParticipant is TLRPC.TL_channelParticipantCreator || channelParticipant is TLRPC.TL_channelParticipantAdmin && !channelParticipant.can_edit)) {
                        canEditAdmin = false
                    }

                    allowKick = ChatObject.canBlockUsers(cf.currentChat) && (!(channelParticipant is TLRPC.TL_channelParticipantAdmin || channelParticipant is TLRPC.TL_channelParticipantCreator) || channelParticipant.can_edit)
                    canRestrict = allowKick
                    editingAdmin = channelParticipant is TLRPC.TL_channelParticipantAdmin
                } else {
                    allowKick = cf.currentChat.creator || chatParticipant is TLRPC.TL_chatParticipant && (ChatObject.canBlockUsers(cf.currentChat) || chatParticipant.inviter_id == cf.userConfig.getClientUserId())
                    canEditAdmin = cf.currentChat.creator
                    canRestrict = cf.currentChat.creator
                    editingAdmin = chatParticipant is TLRPC.TL_chatParticipantAdmin
                }


                val frag = ChatRightsEditActivity(
                        user.id,
                        cf.currentChatInfo.id,
                        channelParticipant?.admin_rights,
                        cf.currentChat.default_banned_rights,
                        channelParticipant?.banned_rights,
                        channelParticipant?.rank,
                        action,
                        true,
                        false
                )

                cf.presentFragment(frag)
            }
        }
    }
}