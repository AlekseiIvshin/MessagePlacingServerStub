package com.eficksan.placingmessages;

import com.eficksan.placingmessages.PlaceMessage;

interface IPlaceMessageRepository {

    /**
    * Creates message and return new message.
    */
    PlaceMessage addMessage(double latitude, double longitude, String message, String userId);

    /**
    * Get near mesasges.
    */
    List<PlaceMessage> getMessagesByUser(String userId);

    /**
    * Removes all messages.
    */
    void removeAllMessages();

}
