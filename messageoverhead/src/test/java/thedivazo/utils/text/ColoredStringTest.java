package thedivazo.utils.text;

import net.md_5.bungee.api.ChatColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ColoredStringTest {

    @Test
    void valueOfTest1() {
        String test1 = ColoredString.valueOf("Hello &ceveryone, I'm &l{#123456:#654321 TheDiVaZo}. &r&eI am an active Java and NodeJS developer. &cI like to play games and watch peppa pig").toString();
        String expected = String.format("Hello %severyone, I'm %s%sT%sh%se%sD%si%sV%sa%sZ%so. %s%sI am an active Java and NodeJS developer. %sI like to play games and watch peppa pig",
                "§c",
                "§l",
                ChatColor.of("#123456"),
                ChatColor.of("#1C364F"),
                ChatColor.of("#273849"),
                ChatColor.of("#313A42"),
                ChatColor.of("#3C3C3C"),
                ChatColor.of("#463D35"),
                ChatColor.of("#503F2E"),
                ChatColor.of("#5B4128"),
                ChatColor.of("#654321"),
                "§r",
                "§e",
                "§c"
        );
        assertEquals(expected.toLowerCase(), test1.toLowerCase());

    }

    @Test
    void valueOfTest2() {
        String test2 = ColoredString.valueOf("{#123456:#654321 Im} &clove &c&l&c&lto eat, but despite this, I train 2 times a week&c &c").toString();
        String expected = String.format("%sI%sm %slove %s%s%s%sto eat, but despite this, I train 2 times a week%s %s",
                ChatColor.of("#123456"),
                ChatColor.of("#654321"),
                "§c",
                "§c",
                "§l",
                "§c",
                "§l",
                "§c",
                "§c"
        );
        assertEquals(expected.toLowerCase(), test2.toLowerCase());

    }

    @Test
    void valueOfTest3() {
        String test3 = ColoredString.valueOf("{#123456:#123456 I started developing} &fas a child when I was 12{#123456:#234567  }").toString();
        String expected = String.format("%sI%s %ss%st%sa%sr%st%se%sd%s %sd%se%sv%se%sl%so%sp%si%sn%sg %sas a child when I was 12%s ",
                ChatColor.of("#123456"),
                ChatColor.of("#123456"),
                ChatColor.of("#123456"),
                ChatColor.of("#123456"),
                ChatColor.of("#123456"),
                ChatColor.of("#123456"),
                ChatColor.of("#123456"),
                ChatColor.of("#123456"),
                ChatColor.of("#123456"),
                ChatColor.of("#123456"),
                ChatColor.of("#123456"),
                ChatColor.of("#123456"),
                ChatColor.of("#123456"),
                ChatColor.of("#123456"),
                ChatColor.of("#123456"),
                ChatColor.of("#123456"),
                ChatColor.of("#123456"),
                ChatColor.of("#123456"),
                ChatColor.of("#123456"),
                ChatColor.of("#123456"),
                "§f",
                ChatColor.of("#123456")
        );
        assertEquals(expected.toLowerCase(), test3.toLowerCase());

    }

    @Test
    void splitTest1() {
        ColoredString test1 = ColoredString.valueOf("&cI also used to play football and swim. &e&lI love swimming, &bI like it when the &r&3cart tickles the belly.");
        assertEquals("§cI", test1.subSequence(0,1).toString());
        assertEquals("§cI also used to play football", test1.subSequence(0,28).toString());
        assertEquals("§cI also used to play football and swim. §e§lI love", test1.subSequence(0,45).toString());
    }
}