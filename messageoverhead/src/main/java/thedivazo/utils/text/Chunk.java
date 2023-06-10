package thedivazo.utils.text;

import io.th0rgal.oraxen.shaded.jetbrains.annotations.Nullable;
import io.th0rgal.oraxen.shaded.jetbrains.annotations.Unmodifiable;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;

import java.awt.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Chunk implements CharSequence {
    @Getter
    private String text;

    @Nullable
    @Getter
    @Unmodifiable
    private ChatColor[] colors;

    public Chunk(String text, ChatColor... colors) {
        this.text = text;
        this.colors = colors;
    }

    public Chunk(String text, Color... colors) {
        this.text = text;
        this.colors = Arrays.stream(colors).map(ChatColor::of).collect(Collectors.toList()).toArray(ChatColor[]::new);
    }



    public Chunk(String text) {
        this.text = text;
        this.colors = null;
    }

    public boolean isEmpty() {
        return text.isEmpty();
    }

    public boolean contentEquals(StringBuffer sb) {
        return text.contentEquals(sb);
    }

    public boolean contentEquals(CharSequence cs) {
        return text.contentEquals(cs);
    }

    public boolean equalsIgnoreCase(String anotherString) {
        return text.equalsIgnoreCase(anotherString);
    }

    public int compareTo(String anotherString) {
        return text.compareTo(anotherString);
    }

    public int compareToIgnoreCase(String str) {
        return text.compareToIgnoreCase(str);
    }

    public boolean regionMatches(int toffset, String other, int ooffset, int len) {
        return text.regionMatches(toffset, other, ooffset, len);
    }

    public boolean regionMatches(boolean ignoreCase, int toffset, String other, int ooffset, int len) {
        return text.regionMatches(ignoreCase, toffset, other, ooffset, len);
    }

    public boolean startsWith(String prefix, int toffset) {
        return text.startsWith(prefix, toffset);
    }

    public boolean startsWith(String prefix) {
        return text.startsWith(prefix);
    }

    public boolean endsWith(String suffix) {
        return text.endsWith(suffix);
    }

    public int indexOf(int ch) {
        return text.indexOf(ch);
    }

    public int indexOf(int ch, int fromIndex) {
        return text.indexOf(ch, fromIndex);
    }

    public int lastIndexOf(int ch) {
        return text.lastIndexOf(ch);
    }

    public int lastIndexOf(int ch, int fromIndex) {
        return text.lastIndexOf(ch, fromIndex);
    }

    public int indexOf(String str) {
        return text.indexOf(str);
    }

    public int indexOf(String str, int fromIndex) {
        return text.indexOf(str, fromIndex);
    }

    public int lastIndexOf(String str) {
        return text.lastIndexOf(str);
    }

    public int lastIndexOf(String str, int fromIndex) {
        return text.lastIndexOf(str, fromIndex);
    }

    public Chunk substring(int beginIndex) {
        return new Chunk(text.substring(beginIndex), colors);
    }

    public Chunk concat(String str) {
        return new Chunk(text.concat(str), colors);
    }

    public Chunk replace(char oldChar, char newChar) {
        return new Chunk(text.replace(oldChar, newChar), colors);
    }
    
    public Chunk replaceFirst(String regex, String replacement) {
        return new Chunk(text.replaceFirst(regex, replacement), colors);
    }

    public Chunk replaceAll(String regex, String replacement) {
        return new Chunk(text.replaceAll(regex, replacement), colors);
    }

    public Chunk replace(CharSequence target, CharSequence replacement) {
        return new Chunk(text.replace(target, replacement), colors);
    }

    public Chunk[] split(String regex, int limit) {
        return Arrays.stream(text.split(regex, limit)).map(line->new Chunk(line, colors)).collect(Collectors.toList()).toArray(Chunk[]::new);
    }

    public Chunk[] split(String regex) {
        return Arrays.stream(text.split(regex)).map(line->new Chunk(line, colors)).collect(Collectors.toList()).toArray(Chunk[]::new);
    }

    public Chunk toLowerCase(Locale locale) {
        return new Chunk(text.toLowerCase(locale), colors);
    }

    public Chunk toLowerCase() {
        return new Chunk(text.toLowerCase(), colors);
    }

    public Chunk toUpperCase(Locale locale) {
        return new Chunk(text.toUpperCase(locale), colors);
    }

    public Chunk toUpperCase() {
        return new Chunk(text.toUpperCase(), colors);
    }

    public Chunk trim() {
        return new Chunk(text.trim(), colors);
    }

    public Chunk strip() {
        return new Chunk(text.strip(), colors);
    }

    public Chunk substring(int beginIndex, int endIndex) {
        return new Chunk(text.substring(beginIndex, endIndex), colors);
    }

    public boolean matches(String regex) {
        return text.matches(regex);
    }

    public boolean contains(CharSequence s) {
        return text.contains(s);
    }

    public Chunk stripLeading() {
        return new Chunk(text.stripLeading(), colors);
    }

    public Chunk stripTrailing() {
        return new Chunk(text.stripTrailing(), colors);
    }

    public boolean isBlank() {
        return text.isBlank();
    }

    public Stream<Chunk> lines() {
        return text.lines().map(line->new Chunk(line, colors));
    }

    public char[] toCharArray() {
        return text.toCharArray();
    }

    public Chunk intern() {
        return new Chunk(text.intern(), colors);
    }

    public Chunk repeat(int count) {
        return new Chunk(text.repeat(count), colors);
    }

    @Override
    public int length() {
        return text.length();
    }

    @Override
    public char charAt(int index) {
        return text.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        CharSequence charSequence = text.subSequence(start, end);
        return new Chunk(String.valueOf(charSequence), colors);
    }

    @Override
    public String toString() {
        return Arrays.stream(colors).map(ChatColor::toString).collect(Collectors.joining("")) + text;
    }

    public String toNoColorString() {
        return text;
    }
}
