package com.eficksan.placingmessages;

import com.eficksan.placingmessages.PlaceMessage;

interface IPlacingMessages {

    /**
    * Creates message and return new message.
    */
    PlaceMessage addMessage(double latitude, double longitude, String message, String userId);

    /**
    * Get near mesasges.
    */
    List<PlaceMessage> getNearMessages(double latitude, double longitude);

    /**
    * Removes all messages.
    */
    void removeAllMessages();


}
