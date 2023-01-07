package thedivazo.listener.chatlistener;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;

public interface ChatListener<E1 extends Event, E2 extends Event> extends Listener {

    void onPlayerChat(E1 event);

    void onPlayerPrivateChat(E2 event);

   void disableListener();

}
