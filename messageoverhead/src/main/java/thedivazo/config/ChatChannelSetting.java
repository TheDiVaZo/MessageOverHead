package thedivazo.config;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ChatChannelSetting {
    private final boolean enable;
    private final String name;
    private final List<String> modelBubbles;
}
