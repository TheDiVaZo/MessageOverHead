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

    private static final Pattern versionPattern = Pattern.compile("([0-9]+)\\.([0-9]+)(\\.([0-9]+))?");

    public static final VersionWrapper v1_12 = VersionWrapper.valueOf("1.12");
    public static final VersionWrapper v1_12_1 = VersionWrapper.valueOf("1.12.1");
    public static final VersionWrapper v1_12_2 = VersionWrapper.valueOf("1.12.2");

    public static final VersionWrapper v1_13 = VersionWrapper.valueOf("1.13");
    public static final VersionWrapper v1_13_1 = VersionWrapper.valueOf("1.13.1");
    public static final VersionWrapper v1_13_2 = VersionWrapper.valueOf("1.13.2");

    public static final VersionWrapper v1_14 = VersionWrapper.valueOf("1.14");
    public static final VersionWrapper v1_14_1 = VersionWrapper.valueOf("1.14.1");
    public static final VersionWrapper v1_14_2 = VersionWrapper.valueOf("1.14.2");
    public static final VersionWrapper v1_14_3 = VersionWrapper.valueOf("1.14.3");

    public static final VersionWrapper v1_15 = VersionWrapper.valueOf("1.15");
    public static final VersionWrapper v1_15_1 = VersionWrapper.valueOf("1.15.1");
    public static final VersionWrapper v1_15_2 = VersionWrapper.valueOf("1.15.2");
    public static final VersionWrapper v1_15_3 = VersionWrapper.valueOf("1.15.3");

    public static final VersionWrapper v1_16 = VersionWrapper.valueOf("1.16");
    public static final VersionWrapper v1_16_1 = VersionWrapper.valueOf("1.16.1");
    public static final VersionWrapper v1_16_2 = VersionWrapper.valueOf("1.16.2");
    public static final VersionWrapper v1_16_3 = VersionWrapper.valueOf("1.16.3");
    public static final VersionWrapper v1_16_4 = VersionWrapper.valueOf("1.16.4");
    public static final VersionWrapper v1_16_5 = VersionWrapper.valueOf("1.16.5");

    public static final VersionWrapper v1_17 = VersionWrapper.valueOf("1.17");
    public static final VersionWrapper v1_17_1 = VersionWrapper.valueOf("1.17.1");

    public static final VersionWrapper v1_18 = VersionWrapper.valueOf("1.18");
    public static final VersionWrapper v1_18_1 = VersionWrapper.valueOf("1.18.1");
    public static final VersionWrapper v1_18_2 = VersionWrapper.valueOf("1.18.2");

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
        return valueOf(version, versionPattern, 1,2,4);
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
