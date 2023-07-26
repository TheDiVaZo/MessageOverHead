package thedivazo.messageoverhead.utils.text;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class DecoratedStringUtilsTest {

    @Test
    void splitText() {
        DecoratedString decoratedString = DecoratedString.valueOf("%player_name% &cmdmd");
        DecoratedStringUtils.splitText(decoratedString, 20).forEach(System.out::println);
        System.out.println("sdsdf");

    }

    @Test
    void wrapString() {
        System.out.println(DecoratedStringUtils.wrapString("&cЕеее боооой"));
    }
}