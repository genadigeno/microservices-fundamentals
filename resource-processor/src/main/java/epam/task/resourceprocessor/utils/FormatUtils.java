package epam.task.resourceprocessor.utils;

public class FormatUtils {
    public static String formatDuration(double seconds) {
        long wholeSeconds = (long) seconds; // Convert to whole seconds
        long minutes = wholeSeconds / 60;
        long remainingSeconds = wholeSeconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }
}
