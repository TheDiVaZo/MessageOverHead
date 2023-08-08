package thedivazo.messageoverhead.util.text.handler;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import thedivazo.messageoverhead.util.text.decor.TextFormat;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class FormatComponent implements BaseComponent {
    private final TextFormat format;
}
