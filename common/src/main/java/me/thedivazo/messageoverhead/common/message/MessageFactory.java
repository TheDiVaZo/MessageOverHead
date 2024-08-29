package me.thedivazo.messageoverhead.common.message;

import java.util.List;

public interface MessageFactory<T> {
    Message<T> createMessage(List<T> source);
}
