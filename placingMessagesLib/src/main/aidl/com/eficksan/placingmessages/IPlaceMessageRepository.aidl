package com.eficksan.placingmessages;

import com.eficksan.placingmessages.PlaceMessage;
import android.net.Uri;

interface IPlaceMessageRepository {

    /**
    * Creates message and return new message.
    */
    PlaceMessage addMessage(double latitude, double longitude,in  String message, in String userId);

    /**
    * Get near mesasges.
    */
    List<PlaceMessage> getMessagesByUser(in String userId);

    /**
    * Removes all messages.
    */
    void removeAllMessages();

    /**
    * Saves messages to CVS file and return uri to this file.
    */
    Uri saveMessagesToCsv();

}
