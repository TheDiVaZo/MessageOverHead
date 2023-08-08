package thedivazo.messageoverhead.util;

import org.junit.jupiter.api.Test;
import thedivazo.messageoverhead.util.text.Chunk;
import thedivazo.messageoverhead.util.text.decor.TextColor;
import thedivazo.messageoverhead.util.text.decor.TextFormat;

import static org.junit.jupiter.api.Assertions.*;

class VersionWrapperTest {

    private VersionWrapper MC_1_20 = VersionWrapper.valueOf("1.20");

    private VersionWrapper MC_1_18 = VersionWrapper.valueOf("1.18");
    private VersionWrapper MC_1_17_1 = VersionWrapper.valueOf("1.17.1");
    private VersionWrapper MC_1_16 = VersionWrapper.valueOf("1.16");
    private VersionWrapper MC_1_15_2 = VersionWrapper.valueOf("1.15.2");
    private VersionWrapper MC_1_14_3 = VersionWrapper.valueOf("1.14.3");
    private VersionWrapper MC_1_13 = VersionWrapper.valueOf("1.13");
    private VersionWrapper MC_1_12_1 = VersionWrapper.valueOf("1.12.1");
    private VersionWrapper MC_1_12_2 = VersionWrapper.valueOf("1.12.2");

    @Test
    void valueOf() {
        assertTrue(MC_1_18.getMajor() == 1 && MC_1_18.getMinor() == 18 && MC_1_18.getPatch() == 0);
        assertTrue(MC_1_17_1.getMajor() == 1 && MC_1_17_1.getMinor() == 17 && MC_1_17_1.getPatch() == 1);
        assertTrue(MC_1_16.getMajor() == 1 && MC_1_16.getMinor() == 16 && MC_1_16.getPatch() == 0);
        assertTrue(MC_1_15_2.getMajor() == 1 && MC_1_15_2.getMinor() == 15 && MC_1_15_2.getPatch() == 2);
        assertTrue(MC_1_14_3.getMajor() == 1 && MC_1_14_3.getMinor() == 14 && MC_1_14_3.getPatch() == 3);
        assertTrue(MC_1_13.getMajor() == 1 && MC_1_13.getMinor() == 13 && MC_1_13.getPatch() == 0);
        assertTrue(MC_1_12_1.getMajor() == 1 && MC_1_12_1.getMinor() == 12 && MC_1_12_1.getPatch() == 1);
        assertTrue(MC_1_12_2.getMajor() == 1 && MC_1_12_2.getMinor() == 12 && MC_1_12_2.getPatch() == 2);
    }

    @Test
    void greater() {
        assertTrue(MC_1_12_2.greater(MC_1_12_1));
        assertTrue(MC_1_13.greater(MC_1_12_2));
        assertTrue(MC_1_14_3.greater(MC_1_12_1));
        assertTrue(MC_1_17_1.greater(MC_1_16));
        assertTrue(MC_1_13.greater(MC_1_12_1));
    }

    @Test
    void less() {
        assertTrue(MC_1_12_1.less(MC_1_12_2));
        assertTrue(MC_1_13.less(MC_1_15_2));
        assertTrue(MC_1_14_3.less(MC_1_16));
        assertTrue(MC_1_12_2.less(MC_1_14_3));
        assertTrue(MC_1_17_1.less(MC_1_18));
    }

    @Test
    void versionToString() {
        assertEquals(MC_1_12_1, MC_1_12_1);
        assertTrue(MC_1_12_1.equalsMinor(MC_1_12_2));
        assertEquals(MC_1_13, MC_1_13);
        assertEquals(MC_1_14_3, MC_1_14_3);
        assertTrue(MC_1_14_3.equalsMajor(MC_1_15_2));
        assertTrue(MC_1_12_1.equalsMajor(MC_1_16));
        assertTrue(MC_1_13.equalsMajor(MC_1_14_3));
    }

    @Test
    void parseVersion() {
        assertEquals(VersionWrapper.valueOf("+git-Paper-17 (MC: 1.20)+"), MC_1_20);
    }
}