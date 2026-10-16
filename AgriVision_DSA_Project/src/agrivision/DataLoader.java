package agrivision;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class DataLoader {
    // The project always loads the prepared UTF-8 text corpus from the data folder.
    public static final String COMBINED_FILE = "data/AgriVision_Corpus/all_agriculture_text.txt";
    public static final String RECORD_FOLDER = "data/AgriVision_Corpus/records";

    public static String loadCombinedCorpus() throws IOException {
        Path path = Paths.get(COMBINED_FILE);
        if (!Files.exists(path)) {
            throw new FileNotFoundException("Corpus not found: " + path.toAbsolutePath());
        }
        return Files.readString(path, StandardCharsets.UTF_8);
    }

    public static List<CorpusRecord> loadRecords() throws IOException {
        Path folder = Paths.get(RECORD_FOLDER);
        if (!Files.isDirectory(folder)) {
            throw new FileNotFoundException("Record folder not found: " + folder.toAbsolutePath());
        }

        List<Path> files = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder, "*.txt")) {
            for (Path p : stream) files.add(p);
        }

        // The filenames contain KCC_0001, KCC_0002, ... so numerical ordering
        // gives a stable and professional display order.
        files.sort(Comparator.comparingInt(DataLoader::recordNumberFromName));

        List<CorpusRecord> result = new ArrayList<>();
        for (Path p : files) {
            result.add(parseRecord(p));
        }
        return result;
    }

    private static int recordNumberFromName(Path p) {
        Matcher m = Pattern.compile("KCC_(\\d+)_").matcher(p.getFileName().toString());
        return m.find() ? Integer.parseInt(m.group(1)) : Integer.MAX_VALUE;
    }

    private static CorpusRecord parseRecord(Path p) throws IOException {
        String text = Files.readString(p, StandardCharsets.UTF_8);
        int no = recordNumberFromName(p);

        String sourceId = field(text, "Source Record ID:");
        String idx = field(text, "Idx:");
        String crop = field(text, "Crop:");
        String state = field(text, "State:");
        String district = field(text, "District:");
        String sector = field(text, "Sector:");
        String queryType = field(text, "Query Type:");
        String season = field(text, "Season:");
        String question = section(text, "FARMER QUERY");
        String answer = section(text, "AGRICULTURAL ADVISORY / ANSWER");

        return new CorpusRecord(no, sourceId, idx, crop, state, district,
                sector, queryType, season, question, answer);
    }

    private static String field(String text, String label) {
        for (String line : text.split("\\R")) {
            if (line.startsWith(label)) return line.substring(label.length()).trim();
        }
        return "";
    }

    private static String section(String text, String heading) {
        int start = text.indexOf(heading);
        if (start < 0) return "";
        int afterHeading = text.indexOf('\n', start);
        if (afterHeading < 0) return "";

        String rest = text.substring(afterHeading + 1);
        // Remove the separator line immediately after the heading.
        rest = rest.replaceFirst("(?s)^[-=]{5,}\\s*", "");

        int next = rest.indexOf("\n\n");
        if (next >= 0) rest = rest.substring(0, next);

        // If another major heading occurs before the end, stop there.
        String[] stops = {"AGRICULTURAL ADVISORY / ANSWER", "SOURCE TEXT"};
        for (String stop : stops) {
            int pos = rest.indexOf(stop);
            if (pos >= 0) rest = rest.substring(0, pos);
        }
        return rest.trim();
    }
}
