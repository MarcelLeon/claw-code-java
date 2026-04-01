package com.example.codingagent.runtime;

/**
 * 格式化会话持续时间。
 */
public final class DurationFormatter {

    private DurationFormatter() {
    }

    /**
     * 将秒数格式化为人类可读文本。
     *
     * @param durationSeconds 持续秒数
     * @return 文本
     */
    public static String formatSeconds(long durationSeconds) {
        long safeSeconds = Math.max(durationSeconds, 0L);
        long hours = safeSeconds / 3600;
        long minutes = (safeSeconds % 3600) / 60;
        long seconds = safeSeconds % 60;
        if (hours > 0) {
            return hours + "h " + minutes + "m " + seconds + "s";
        }
        if (minutes > 0) {
            return minutes + "m " + seconds + "s";
        }
        return seconds + "s";
    }
}
