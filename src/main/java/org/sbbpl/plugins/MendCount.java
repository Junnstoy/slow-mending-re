package org.sbbpl.plugins;

/** Values below zero are modes, never the result of subtraction. */
public final class MendCount {
    private MendCount() {}

    public static int validate(int value) {
        if (value < -3) throw new IllegalArgumentException("次数必须为非负整数，或 -1/-2/-3。");
        return value;
    }

    public static int add(int current, int delta) {
        validate(current);
        if (current < 0) throw new IllegalArgumentException("特殊次数请使用 set 模式修改。");
        long result = (long) current + delta;
        if (result > Integer.MAX_VALUE) throw new IllegalArgumentException("次数超出整数上限。");
        // A deduction must not accidentally grant unlimited mending.
        return (int) Math.max(0L, result);
    }
}
