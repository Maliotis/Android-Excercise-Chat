package com.maliotis.muzmatch_excercise.controller


/**
 * Created by petrosmaliotis on 11/09/2020.
 */
object Conversation {

    public fun sendMessageFrom(text: String, from: String, channel: String): String? {
        val contentId = RealmOperations.createContentWithText(text)
        if (contentId != null) {
            val messageId = RealmOperations.createMessageToChannelWithUser(contentId, channel, from)
            if (messageId != null)
                return messageId
        }
        return null
    }

    /**
     * Stub.
     *
     * Will be implemented as a feature in the future
     */
    public fun sendStickerFrom(sticker: String, from: String, channel: String) {

    }


}