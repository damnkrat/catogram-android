package ua.itaysonlab.catogram.stickerkang

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import org.telegram.messenger.*
import org.telegram.tgnet.TLRPC
import java.io.File

class KangBridge: NotificationCenter.NotificationCenterDelegate {
    companion object {
        private const val MAX_STICKER_COUNT = 120
        private const val RESIZE_AMPL = 512
        private const val STICKERS_BOT_ID = 429000L

        private const val NO_SET_ERR_RU = "Не выбран набор стикеров."
        private const val NO_SET_ERR_EN = "Invalid pack selected."

        private const val DEFAULT_EMOJI = "\uD83E\uDD14"
    }

    init {
        for (a in 0 until UserConfig.MAX_ACCOUNT_COUNT) {
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.didReceiveNewMessages)
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.fileDidLoad)
        }
    }

    private var isCurrentKangAnimated = false
    private var currentKangingSticker: TLRPC.Document? = null
    private var currentKangingStickerFile: File? = null
    private var currentKangingStickerAccountID = -1

    private var messageCallback: ((MessageObject) -> Unit)? = null
    private var fileLoadCallback: ((File) -> Unit)? = null

    private var stickerPackName = ""
    private var stickerPackID = ""

    fun prepareKang(doc: TLRPC.Document) {
        if (!MessageObject.isAnimatedStickerDocument(doc, true) && !MessageObject.isStickerDocument(doc)) {
            Toast.makeText(ApplicationLoader.applicationContext, "This document is not a sticker!", Toast.LENGTH_LONG).show()
            return
        }

        if (currentKangingSticker != null) {
            Toast.makeText(ApplicationLoader.applicationContext, "Please wait - other sticker is now kanging!", Toast.LENGTH_LONG).show()
            return
        }

        currentKangingStickerAccountID = UserConfig.selectedAccount
        isCurrentKangAnimated = MessageObject.isAnimatedStickerDocument(doc, true)
        currentKangingSticker = doc

        val acc = MessagesController.getInstance(UserConfig.selectedAccount).getUser(UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId())
        stickerPackName = "CGKang ${if (isCurrentKangAnimated) "animated pack" else "pack"} by @${acc.username}"
        stickerPackID = "catogram_kang_${acc.id}_${if (isCurrentKangAnimated) "anim" else "base"}"

        notifyStatus(KangStatus.FETCHING)
        FileLoader.getInstance(0).loadFile(currentKangingSticker, null, 999, 0)
        awaitForFile {
            currentKangingStickerFile = it
            startKang(0)
        }
    }

    private fun awaitForMessage(cb: (MessageObject) -> Unit) {
        messageCallback = cb
    }

    private fun awaitForFile(cb: (File) -> Unit) {
        fileLoadCallback = cb
    }

    private fun sendMessage(msg: String) {
        SendMessagesHelper.getInstance(currentKangingStickerAccountID).sendMessage(msg, STICKERS_BOT_ID, null, null, false, null, null, null, true, 0)
    }

    private fun startKang(packCount: Int) {
        notifyStatus(KangStatus.CHECKING)
        sendMessage("/addsticker")
        awaitForMessage {
            sendMessage("${stickerPackID}_$packCount")
            awaitForMessage {
                if (it.messageText == NO_SET_ERR_EN || it.messageText == NO_SET_ERR_RU) {
                    notifyStatus(KangStatus.CREATING)
                    createPack(packCount) {
                        uploadSticker {
                            sendMessage(DEFAULT_EMOJI)
                            awaitForMessage {
                                publish {
                                    notifyStatus(KangStatus.DONE)
                                }
                            }
                        }
                    }
                } else {
                    notifyStatus(KangStatus.UPLOADING)
                    uploadSticker { obj ->
                        if (obj.messageText.contains(MAX_STICKER_COUNT.toString())) {
                            notifyStatus(KangStatus.SWITCHING)
                            val c = packCount + 1
                            createPack(c) {
                                uploadSticker { uso ->
                                    sendMessage(DEFAULT_EMOJI)
                                    awaitForFile {
                                        publish {
                                            notifyStatus(KangStatus.DONE)
                                        }
                                    }
                                }
                            }
                        } else {
                            sendMessage(DEFAULT_EMOJI)
                            awaitForMessage {
                                sendMessage("/done")
                                notifyStatus(KangStatus.DONE)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun publish(cb: () -> Unit) {
        sendMessage("/publish")
        awaitForMessage {
            sendMessage("<${stickerPackName}>")
            awaitForMessage {
                sendMessage("/skip")
                awaitForMessage {
                    cb()
                }
            }
        }
    }

    private fun cleanup() {
        currentKangingSticker = null
        currentKangingStickerFile = null
    }

    private fun uploadSticker(callback: (MessageObject) -> Unit) {
        SendMessagesHelper.prepareSendingDocuments(AccountInstance.getInstance(currentKangingStickerAccountID), arrayListOf<String>(currentKangingStickerFile!!.absolutePath), arrayListOf<String>(currentKangingStickerFile!!.absolutePath), null, "", null, STICKERS_BOT_ID, null, null, null, false, 0);
        awaitForMessage {
            callback(it)
        }
    }

    private fun createPack(packCount: Int, callback: () -> Unit) {
        sendMessage(if (isCurrentKangAnimated) "/newanimated" else "/newpack")
        awaitForMessage {
            sendMessage(stickerPackName)
            awaitForMessage {
                callback()
            }
        }
    }

    private fun notifyStatus(stat: KangStatus) {
        Log.d("CatogramKang", stat.guiText)
        Toast.makeText(ApplicationLoader.applicationContext, stat.guiText, Toast.LENGTH_LONG).show()
    }

    enum class KangStatus(val guiText: String) {
        CHECKING("Checking stickerpack..."),
        FETCHING("Fetching image..."),
        RESIZING("Resizing image..."),
        UPLOADING("Uploading sticker..."),
        CREATING("Creating stickerpack..."),
        SWITCHING("Switching stickerpack..."),
        DONE("Done!"),
    }

    fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }

    override fun didReceivedNotification(id: Int, account: Int, vararg args: Any) {
        if (id != NotificationCenter.didReceiveNewMessages && id != NotificationCenter.fileDidLoad) return
        if (account != currentKangingStickerAccountID) return

        when (id) {
            NotificationCenter.didReceiveNewMessages -> {
                val uid = args[0] as Long
                val msgs = args[1] as ArrayList<MessageObject>

                if (uid == STICKERS_BOT_ID && messageCallback != null) {
                    messageCallback!!.invoke(msgs[0])
                    messageCallback = null
                }
            }

            NotificationCenter.fileDidLoad -> {
                val file = args[1] as File
                if (fileLoadCallback != null) {
                    fileLoadCallback!!.invoke(file)
                    fileLoadCallback = null
                }
            }
        }
    }
}