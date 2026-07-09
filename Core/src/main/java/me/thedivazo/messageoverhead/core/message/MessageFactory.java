package me.thedivazo.messageoverhead.core.message;

import me.thedivazo.messageoverhead.core.Author;
import me.thedivazo.messageoverhead.core.Message;

public interface MessageFactory {
    Message create(Author author, String message);
}
