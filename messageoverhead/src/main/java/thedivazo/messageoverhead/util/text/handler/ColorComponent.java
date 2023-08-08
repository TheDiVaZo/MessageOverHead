package thedivazo.messageoverhead.util.text.handler;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import thedivazo.messageoverhead.util.text.decor.TextColor;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class ColorComponent implements BaseComponent {
    private final TextColor color;
}
