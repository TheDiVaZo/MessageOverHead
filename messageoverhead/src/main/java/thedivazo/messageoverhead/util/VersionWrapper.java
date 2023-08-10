package thedivazo.messageoverhead.util;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@EqualsAndHashCode
@AllArgsConstructor
@Getter
public class VersionWrapper implements Comparable<VersionWrapper> {

    private static final Pattern VERSION_PATTERN = Pattern.compile("([0-9]+)\\.([0-9]+)(\\.([0-9]+))?");

    private int major;
    private int minor;
    private int patch;

    @Override
    public int compareTo(VersionWrapper o) {
        if (major - o.major == 0) {
            if (minor - o.minor == 0) {
                return patch - o.patch;
            }
            else return minor - o.minor;
        }
        else return major - o.major;
    }

    public static VersionWrapper valueOf(String version) {
        return valueOf(version, VERSION_PATTERN, 1,2,4);
    }

    public static VersionWrapper valueOf(String version, Pattern pattern, int majorGroup,int minorGroup,int patchGroup) {
        Matcher matcher = pattern.matcher(version);
        if (!matcher.find()) throw new IllegalArgumentException("The value '+"+version+"+' is not version");
        String wMajor = matcher.group(majorGroup);
        String wMinor = matcher.group(minorGroup);
        String wPatch = matcher.group(patchGroup);
        return new VersionWrapper(
                Integer.parseInt(wMajor),
                Integer.parseInt(wMinor),
                Objects.isNull(wPatch) || wPatch.isBlank() || wPatch.isEmpty() ? 0:Integer.parseInt(wPatch));
    }

    @Override
    public String toString() {
        return String.format("%s.%s.%s", major, minor, patch);
    }

    public boolean greater(VersionWrapper versionWrapper) {
        return this.compareTo(versionWrapper) > 0;
    }

    public boolean less(VersionWrapper versionWrapper) {
        return this.compareTo(versionWrapper) < 0;
    }

    public boolean equalsMajor(VersionWrapper versionWrapper) {
        return this.major == versionWrapper.major;
    }

    public boolean equalsMinor(VersionWrapper versionWrapper) {
        return this.minor == versionWrapper.minor;
    }

    public boolean greaterMajor(VersionWrapper versionWrapper) {
        return this.major > versionWrapper.major;
    }

    public boolean greaterMinor(VersionWrapper versionWrapper) {
        return this.minor > versionWrapper.minor;
    }

    public boolean lessMajor(VersionWrapper versionWrapper) {
        return this.major < versionWrapper.major;
    }

    public boolean lessMinor(VersionWrapper versionWrapper) {
        return this.minor < versionWrapper.minor;
    }


}
