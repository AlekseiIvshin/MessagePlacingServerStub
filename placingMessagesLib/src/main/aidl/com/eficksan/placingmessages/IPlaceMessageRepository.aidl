package com.eficksan.placingmessages;

import com.eficksan.placingmessages.PlaceMessage;

interface IPlaceMessageRepository {

    /**
    * Creates message and return new message.
    */
    void addMessage(double latitude, double longitude,in  String message, in String userId, out PlaceMessage placeMessage);

    /**
    * Get near mesasges.
    */
    void getMessagesByUser(in String userId, out List<PlaceMessage> messages);

    /**
    * Removes all messages.
    */
    void removeAllMessages();

}
