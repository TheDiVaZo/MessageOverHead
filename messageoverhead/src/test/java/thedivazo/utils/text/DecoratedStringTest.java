package thedivazo.utils.text;

import org.junit.jupiter.api.Test;
import thedivazo.utils.text.element.Chunk;
import thedivazo.utils.text.customize.TextColor;
import thedivazo.utils.text.customize.TextFormatting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class DecoratedStringTest {

    @Test
    void SimpleTestValueOf() {
        DecoratedString decoratedString = DecoratedString.valueOf("&cHello everyone, &bmy name is &e&lTheDiVaZo");
        DecoratedString result = DecoratedString.builder()
                .chunk(Chunk.builder()
                        .setColor(TextColor.RED)
                        .setText("Hello everyone, ")
                        .build())
                .chunk(Chunk.builder()
                        .setColor(TextColor.of("#55FFFF"))
                        .setText("my name is ")
                        .build())
                .chunk(Chunk.builder()
                        .setColor(TextColor.YELLOW)
                        .setText("TheDiVaZo")
                        .setFormat(TextFormatting.BOLD)
                        .build())
                .build();
        assertEquals(result, decoratedString);
    }

    @Test
    void redundantTestValueOf() {
        DecoratedString decoratedString = DecoratedString.valueOf("&c&c&c&c&c&c&c&c&c&c&c&c&c&c&c&c&c&c&cHello everyone, &b&b&b&b&b&b&b&b&b&b&b&b&b&b&b&b&bmy name is &e&e&e&l&l&l&e&e&e&e&e&e&e&e&e&l&l&l&e&e&e&l&e&e&l&l&l&l&l&e&l&l&l&l&l&l&lTheDiVaZo");
        DecoratedString result = DecoratedString.builder()
                .chunk(Chunk.builder()
                        .setColor(TextColor.RED)
                        .setText("Hello everyone, ")
                        .build())
                .chunk(Chunk.builder()
                        .setColor(TextColor.of("#55FFFF"))
                        .setText("my name is ")
                        .build())
                .chunk(Chunk.builder()
                        .setColor(TextColor.YELLOW)
                        .setText("TheDiVaZo")
                        .setFormat(TextFormatting.BOLD)
                        .build())
                .build();
        assertEquals(result, decoratedString);
    }

    @Test
    void RandomBeginAndEndColorTestValueOf() {
        DecoratedString decoratedString = DecoratedString.valueOf("&b&e&1&4&3&6&4&2&6&f&2&cHello everyone, &bmy name is &e&lTheDiVaZo&c&3&2&5&4&c&d&e&f&3&3&2&1&0&5");
        DecoratedString result = DecoratedString.builder()
                .chunk(Chunk.builder()
                        .setColor(TextColor.RED)
                        .setText("Hello everyone, ")
                        .build())
                .chunk(Chunk.builder()
                        .setColor(TextColor.of("#55FFFF"))
                        .setText("my name is ")
                        .build())
                .chunk(Chunk.builder()
                        .setColor(TextColor.YELLOW)
                        .setText("TheDiVaZo")
                        .setFormat(TextFormatting.BOLD)
                        .build())
                .build();
        assertEquals(result, decoratedString);
    }

    @Test
    void formatSavingValueOf() {
        DecoratedString decoratedString = DecoratedString.valueOf("&cHello everyone, &b&lmy &cname is &e&nTheDiVaZo");
        DecoratedString result = DecoratedString.builder()
                .chunk(Chunk.builder()
                        .setColor(TextColor.RED)
                        .setText("Hello everyone, ")
                        .build())
                .chunk(Chunk.builder()
                        .setColor(TextColor.of("#55FFFF"))
                        .setText("my ")
                        .setFormat(TextFormatting.BOLD)
                        .build())
                .chunk(Chunk.builder()
                        .setColor(TextColor.RED)
                        .setText("name is ")
                        .setFormat(TextFormatting.BOLD)
                        .build())
                .chunk(Chunk.builder()
                        .setColor(TextColor.YELLOW)
                        .setText("TheDiVaZo")
                        .setFormat(TextFormatting.BOLD)
                        .setFormat(TextFormatting.UNDERLINE)
                        .build())
                .build();
        assertEquals(result, decoratedString);
    }

    @Test
    void manyFormatValueOf() {
        DecoratedString decoratedString = DecoratedString.valueOf("&cHello everyone, &b&lmy &cname is &e&k&l&m&n&o&k&l&m&n&o&k&l&m&n&o&k&l&m&n&o&k&l&m&n&o&k&l&m&n&o&k&l&m&n&o&k&l&m&n&oTheDiVaZo");
        DecoratedString result = DecoratedString.builder()
                .chunk(Chunk.builder()
                        .setColor(TextColor.RED)
                        .setText("Hello everyone, ")
                        .build())
                .chunk(Chunk.builder()
                        .setColor(TextColor.of("#55FFFF"))
                        .setText("my ")
                        .setFormat(TextFormatting.BOLD)
                        .build())
                .chunk(Chunk.builder()
                        .setColor(TextColor.RED)
                        .setText("name is ")
                        .setFormat(TextFormatting.BOLD)
                        .build())
                .chunk(Chunk.builder()
                        .setColor(TextColor.YELLOW)
                        .setText("TheDiVaZo")
                        .setFormat(TextFormatting.BOLD)
                        .setFormat(TextFormatting.UNDERLINE)
                        .setFormat(TextFormatting.MAGIC)
                        .setFormat(TextFormatting.ITALIC)
                        .setFormat(TextFormatting.UNDERLINE)
                        .setFormat(TextFormatting.STRIKETHROUGH)
                        .build())
                .build();
        assertEquals(result, decoratedString);
    }

    @Test
    void hexValueOf() {
        DecoratedString decoratedString = DecoratedString.valueOf("&#1a2b3cHello everyone, &#abcdef&lmy &cname is &#123456TheDiVaZo");
        DecoratedString expected = DecoratedString.builder()
                .chunk(Chunk.builder()
                        .setColor(TextColor.of("#1a2b3c"))
                        .setText("Hello everyone, ")
                        .build())
                .chunk(Chunk.builder()
                        .setColor(TextColor.of("#abcdef"))
                        .setText("my ")
                        .setFormat(TextFormatting.BOLD)
                        .build())
                .chunk(Chunk.builder()
                        .setColor(TextColor.RED)
                        .setText("name is ")
                        .setFormat(TextFormatting.BOLD)
                        .build())
                .chunk(Chunk.builder()
                        .setColor(TextColor.of("#123456"))
                        .setText("TheDiVaZo")
                        .setFormat(TextFormatting.BOLD)
                        .build())
                .build();
        assertEquals(expected, decoratedString);
    }

    @Test
    void simpleTestSubSequence() {
        DecoratedString decoratedString = DecoratedString.valueOf("&cI study computer science &eand &blove programming");
        DecoratedString sub1 = (DecoratedString) decoratedString.subSequence(0, 25);
        DecoratedString sub2 = (DecoratedString) decoratedString.subSequence(25, 29);
        DecoratedString sub3 = (DecoratedString) decoratedString.subSequence(29, 45);
        System.out.println(sub1.getMinecraftColoredString());
        System.out.println(sub2.getMinecraftColoredString());
        System.out.println(sub3.getMinecraftColoredString());

        DecoratedString expectedSub1 = DecoratedString.builder()
                .chunk(Chunk.builder()
                        .setColor(TextColor.RED)
                        .setText("I study computer science ")
                        .build())
                .build();
        DecoratedString expectedSub2 = DecoratedString.builder()
                .chunk(Chunk.builder()
                        .setColor(TextColor.YELLOW)
                        .setText("and ")
                        .build())
                .build();
        DecoratedString expectedSub3 = DecoratedString.builder()
                .chunk(Chunk.builder()
                        .setColor(TextColor.of("#55ffff"))
                        .setText("love programming")
                        .build())
                .build();

        assertEquals(expectedSub1, sub1);
        assertEquals(expectedSub2, sub2);
        assertEquals(expectedSub3, sub3);
    }

    @Test
    void unevenColorTestSubSequence() {
        DecoratedString decoratedString = DecoratedString.valueOf("&cI stu&ldy compu&4ter scie&mnce &r&ean&ld &blove prog&1&oramming");
        DecoratedString sub1 = (DecoratedString) decoratedString.subSequence(0, 25);
        DecoratedString sub2 = (DecoratedString) decoratedString.subSequence(25, 29);
        DecoratedString sub3 = (DecoratedString) decoratedString.subSequence(29, 45);
        System.out.println(sub1.getMinecraftColoredString());
        System.out.println(sub2.getMinecraftColoredString());
        System.out.println(sub3.getMinecraftColoredString());

    }

    @Test
    void indexOf() {
        DecoratedString decoratedString = DecoratedString.valueOf("&cHaHa &eEeEe &cHaHaJ &eEeEe &cHaHa");
        System.out.println(decoratedString.indexOf(DecoratedString.valueOf("&cHaHaJ"), 3));
    }

    @Test
    void lastIndexOf() {
        DecoratedString decoratedString = DecoratedString.valueOf("&cHaHa &eEeEe &cHaHaJ &eEeEe &cHaHa");
        System.out.println(decoratedString.lastIndexOf(DecoratedString.valueOf("&cHaHaJ"), 100));
    }

}