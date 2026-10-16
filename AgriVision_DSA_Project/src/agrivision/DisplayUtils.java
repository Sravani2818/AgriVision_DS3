package agrivision;

import java.util.*;

public class DisplayUtils {
    public static void line() {
        System.out.println("=".repeat(82));
    }

    public static void title(String s) {
        line();
        System.out.println(center(s, 82));
        line();
    }

    private static String center(String s, int width) {
        if (s.length() >= width) return s;
        int left = (width - s.length()) / 2;
        return " ".repeat(left) + s;
    }

    public static void pause(Scanner sc) {
        System.out.println("\nPress ENTER to continue...");
        sc.nextLine();
    }

    public static void printPositions(String text, List<Integer> positions) {
        System.out.println("Occurrences Found : " + positions.size());
        if (positions.isEmpty()) {
            System.out.println("No occurrence found.");
            return;
        }
        int shown = Math.min(positions.size(), 100);
        for (int i = 0; i < shown; i++) {
            int p = positions.get(i);
            System.out.printf("Occurrence %-3d : Position %d%n", i + 1, p);
        }
        if (positions.size() > shown) {
            System.out.println("... " + (positions.size() - shown) + " more positions omitted from display.");
        }
    }

    public static String snippet(String text, int position, int radius) {
        int from = Math.max(0, position - radius);
        int to = Math.min(text.length(), position + radius);
        String s = text.substring(from, to).replace("\n", " ").replace("\r", " ");
        return "..." + s + "...";
    }
}
