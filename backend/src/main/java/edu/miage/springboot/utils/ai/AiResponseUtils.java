package edu.miage.springboot.utils.ai;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AiResponseUtils {

    private static final Pattern JSON_PATTERN =
            Pattern.compile("\\{[\\s\\S]*?\\}");

    private AiResponseUtils() {}

    public static String extractJson(String text) {
        Matcher matcher = JSON_PATTERN.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        throw new IllegalArgumentException("No JSON found in AI response");
    }
}
