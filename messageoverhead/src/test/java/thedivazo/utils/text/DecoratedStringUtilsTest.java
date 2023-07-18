package thedivazo.utils.text;

import org.junit.jupiter.api.Test;

class DecoratedStringUtilsTest {

    @Test
    void splitText() {
        DecoratedString decoratedString = DecoratedString.valueOf("&cЯ к&bрутой чел е &eбой американ бой &3уеду с табой еееее бой");
        DecoratedStringUtils.splitText(decoratedString, 1).forEach(System.out::println);
        System.out.println("sdsdf");

    }
}