package thedivazo.messageoverhead.util.text.handler;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class TextComponent implements BaseComponent {
    private final String text;
}
