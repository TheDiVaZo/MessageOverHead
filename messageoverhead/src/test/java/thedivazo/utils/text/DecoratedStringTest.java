package thedivazo.utils.text;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DecoratedStringTest {

    @Test
    void valueOf() {
        DecoratedString decoratedString = DecoratedString.valueOf("&cВсем привет &lЯ был &e&mкогда то странных &bd");
        System.out.println(decoratedString.toMinecraftColoredString());
    }

}