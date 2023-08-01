package thedivazo.messageoverhead.util.text;

import org.junit.jupiter.api.Test;

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