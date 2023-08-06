package thedivazo.messageoverhead.util.text.decor.colorutil;

import com.google.common.collect.Collections2;
import lombok.Builder;
import lombok.Singular;
import org.apache.commons.lang3.ArrayUtils;
import thedivazo.messageoverhead.util.text.DecoratedChar;
import thedivazo.messageoverhead.util.text.DecoratedString;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Builder
public class GradientText {
    @Singular
    private final List<GradientChunk> gradientChunks;



    public DecoratedString toDecoratedString() {
        List<DecoratedChar> result = new ArrayList<>();
        for (GradientChunk gradientChunk : gradientChunks) {
            result.addAll(gradientChunk.toChunkList());
        }
        return DecoratedString.valueOf(result);
    }
}
