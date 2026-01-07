package com.leon.rest_api.utils;

import com.leon.rest_api.dto.DecodingResult;
import com.leon.rest_api.service.EncodingCandidate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.util.List;

@Component
public class TextFileUtils {

    private static final Logger logger = LoggerFactory.getLogger(TextFileUtils.class);

    public static final List<String> ENCODINGS_TO_TRY = List.of(
            "utf-8", "gbk", "gb2312", "big5", "cp950",
            "utf-16", "utf-16-le", "utf-16-be"
    );
//    # encodings_to_try = [
//            #     "utf-8", "utf-8-sig", "gbk", "gb2312", "big5", "cp950", "hz",
//            #     "iso-8859-1", "iso-8859-2", "iso-8859-15", "windows-1252",
//            #     "euc-jp", "euc-kr", "shift_jis", "cp936", "utf-16", "utf-16-le", "utf-16-be"
//            # ]

    /**
     * Test all encodings in encoding_list and return the one with the least replacement errors.
     * Returns: DecodingResult(encoding, language, total_lines)
     * Throws IllegalArgumentException if no encoding succeeds.
     */
    public static DecodingResult decodingVerification(String filePath, List<EncodingCandidate> encodingList) {
        logger.info("Start decoding verification");
        String bestEncoding = null;
        String bestLang = null;
        int bestTotalLines = 0;
        Long leastReplaceCount = null;

        for (EncodingCandidate encInfo : encodingList) {
            String enc = encInfo.encoding();
            String lang = encInfo.lang();

            if (enc == null || lang == null) {
                continue;
            }

            try {
                // Handle utf-8-sig manually if needed, or rely on Java's UTF-8 handling BOM if possible.
                // Java's "UTF-8" usually handles BOM, but "utf-8-sig" is not a standard Java charset name.
                // We map "utf-8-sig" to "UTF-8" for Java Charset lookup, but keep the original name for logging if needed.
                String javaCharsetName = enc.equalsIgnoreCase("utf-8-sig") ? "UTF-8" : enc;

                Charset charset = Charset.forName(javaCharsetName);
                CharsetDecoder decoder = charset.newDecoder()
                        .onMalformedInput(CodingErrorAction.REPLACE)
                        .onUnmappableCharacter(CodingErrorAction.REPLACE);

                int lines = 0;
                long replaceCount = 0;

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), decoder))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        lines++;
                        for (char c : line.toCharArray()) {
                            if (c == '\uFFFD') {  //this symbol --> �
                                replaceCount++;
                            }
                        }
                    }
                }

                logger.info("Encoding [{}] Language: {} → replace_count: {}", enc, lang, replaceCount);

                if (leastReplaceCount == null || replaceCount < leastReplaceCount) {
                    leastReplaceCount = replaceCount;
                    bestEncoding = enc;
                    bestLang = lang;
                    bestTotalLines = lines;
                }

                //perfect decoded, close case
                if(replaceCount==0){
                    logger.info("Perfectly decoded!");
                    break;
                }

            } catch (Exception e) {
                String msg = "❌ Failed to decode with '%s': %s".formatted(enc, e.getMessage());
                logger.error(msg);
            }
        }

        if (bestEncoding != null) {
            logger.info("Best encoding: [{}], Language: [{}], Replacements: {}", bestEncoding, bestLang, leastReplaceCount);
            return new DecodingResult(bestEncoding, bestLang, bestTotalLines);
        }

        throw new IllegalArgumentException("No valid encoding found for file: " + filePath);
    }
}
