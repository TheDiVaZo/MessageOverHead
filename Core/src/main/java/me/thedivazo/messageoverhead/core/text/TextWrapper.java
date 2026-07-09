package me.thedivazo.messageoverhead.core.text;

import org.intellij.lang.annotations.RegExp;

public interface TextWrapper {
    String plainText();
    TextWrapper[] split(@RegExp String separator);
}
