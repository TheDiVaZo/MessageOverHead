package me.thedivazo.messageoverhead.common.message.factory;

import me.thedivazo.messageoverhead.common.message.Message;

import java.util.List;

public interface MessageFactory<S> {
    Message createMessage(List<S> source);
}
