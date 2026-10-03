package org.sbbpl.plugins;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class MendCountTest {
    @ParameterizedTest
    @ValueSource(ints = {-3, -2, -1, 0, 1, Integer.MAX_VALUE})
    void acceptsOnlyDocumentedStates(int value) {
        assertEquals(value, MendCount.validate(value));
    }

    @Test void rejectsUndefinedNegativeState() {
        assertThrows(IllegalArgumentException.class, () -> MendCount.validate(-4));
    }

    @Test void subtractionNeverGrantsUnlimitedMode() {
        assertEquals(0, MendCount.add(5, -6));
        assertEquals(0, MendCount.add(5, Integer.MIN_VALUE));
    }

    @Test void detectsOverflowWithoutWrappingIntoSpecialMode() {
        assertThrows(IllegalArgumentException.class, () -> MendCount.add(Integer.MAX_VALUE, 1));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -2, -3})
    void specialStatesRequireExplicitSet(int mode) {
        assertThrows(IllegalArgumentException.class, () -> MendCount.add(mode, 100));
    }
}
