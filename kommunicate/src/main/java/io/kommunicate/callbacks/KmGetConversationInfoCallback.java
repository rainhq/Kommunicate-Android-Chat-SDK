package io.kommunicate.callbacks;

import android.content.Context;

import io.kommunicate.data.people.channel.Channel;

public interface KmGetConversationInfoCallback {
    void onSuccess(Channel channel, Context context);

    void onFailure(Exception e, Context context);
}
