package com.example.codingagent.runtime;

import com.example.codingagent.persistence.TranscriptEntry;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

/**
 * 根据历史内容生成简短会话标题。
 */
@Component
public class SessionTitleGenerator {

    private static final Pattern NON_LETTER_OR_DIGIT = Pattern.compile("[^\\p{IsAlphabetic}\\p{IsDigit}]+");

    /**
     * 根据 transcript 生成标题。
     *
     * @param entries 会话历史
     * @return 标题；没有足够内容时返回 null
     */
    public String generate(List<TranscriptEntry> entries) {
        return entries.stream()
                .filter(entry -> "user".equals(entry.role()))
                .map(TranscriptEntry::content)
                .map(this::toKebabCase)
                .filter(value -> !value.isBlank())
                .findFirst()
                .orElse(null);
    }

    private String toKebabCase(String content) {
        if (content == null || content.isBlank()) {
            return "";
        }
        String normalized = NON_LETTER_OR_DIGIT.matcher(content.toLowerCase(Locale.ROOT))
                .replaceAll(" ")
                .trim();
        if (normalized.isBlank()) {
            return "";
        }
        List<String> parts = Arrays.stream(normalized.split("\\s+"))
                .filter(part -> !part.isBlank())
                .limit(4)
                .toList();
        return String.join("-", parts);
    }
}
