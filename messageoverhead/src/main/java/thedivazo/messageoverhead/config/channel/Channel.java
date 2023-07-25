package thedivazo.messageoverhead.config.channel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

@Builder
@EqualsAndHashCode
@AllArgsConstructor
@Getter
public class Channel {
    private @Nullable String name;
    private Type type;


}
