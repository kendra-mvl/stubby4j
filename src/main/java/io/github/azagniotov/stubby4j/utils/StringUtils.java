/*
 * Copyright (c) 2012-2024 Alexander Zagniotov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.azagniotov.stubby4j.utils;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.StringJoiner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class StringUtils {

    public static final String UTF_8 = "UTF-8";

    static final String NOT_PROVIDED = "Not provided";
    static final String FAILED =
            "Failed to retrieveLoadedStubs response content using relative path specified in 'file' during YAML parse time. Check terminal for warnings, and that response content exists in relative path specified in 'file'";

    private static final CharsetEncoder US_ASCII_ENCODER = StandardCharsets.US_ASCII.newEncoder();

    private static final String TEMPLATE_TOKEN_LEFT = "<%";
    private static final String TEMPLATE_TOKEN_RIGHT = "%>";
    private static final Base64.Encoder BASE_64_ENCODER = Base64.getEncoder();

    private StringUtils() {}

    public static boolean isSet(final String toTest) {
        return ObjectUtils.isNotNull(toTest) && isNotBlank(toTest);
    }

    public static String trimIfSet(final String toTest) {
        return StringUtils.isSet(toTest) ? toTest.trim() : toTest;
    }

    public static boolean isNotSet(final String toTest) {
        return !isSet(toTest);
    }

    public static String toUpper(final String toUpper) {
        if (isNotSet(toUpper)) {
            return "";
        }
        return toUpper.toUpperCase(Locale.US);
    }

    public static String toLower(final String toLower) {
        if (isNotSet(toLower)) {
            return "";
        }
        return toLower.toLowerCase(Locale.US);
    }

    public static Charset charsetUTF8() {
        return StandardCharsets.UTF_8;
    }

    public static String newStringUtf8(final byte[] bytes) {
        return new String(bytes, StringUtils.charsetUTF8());
    }

    public static byte[] getBytesUtf8(final String string) {
        return string.getBytes(StringUtils.charsetUTF8());
    }

    public static String inputStreamToString(final InputStream inputStream) {
        if (ObjectUtils.isNull(inputStream)) {
            return "Could not convert null input stream to string";
        }
        // Regex \A matches the beginning of input. This effectively tells Scanner to tokenize
        // the entire stream, from beginning to (illogical) next beginning.
        if (inputStream instanceof BufferedInputStream) {
            return new Scanner(inputStream, StringUtils.UTF_8)
                    .useDelimiter("\\A")
                    .next()
                    .trim();
        }

        return new Scanner(FileUtils.makeBuffered(inputStream), StringUtils.UTF_8)
                .useDelimiter("\\A")
                .next()
                .trim();
    }

    public static String buildToken(final String propertyName, final int capturingGroupIdx) {
        return String.format("%s.%s", propertyName, capturingGroupIdx);
    }

    public static String replaceTokens(final byte[] stringBytes, final Map<String, String> tokensAndValues) {
        return replaceTokensInString(StringUtils.newStringUtf8(stringBytes), tokensAndValues);
    }

    public static String replaceTokensInString(String template, final Map<String, String> tokensAndValues) {
        for (final Map.Entry<String, String> entry : tokensAndValues.entrySet()) {
            final String regexifiedKey =
                    String.format("%s\\s{0,}%s\\s{0,}%s", TEMPLATE_TOKEN_LEFT, entry.getKey(), TEMPLATE_TOKEN_RIGHT);
            template = template.replaceAll(regexifiedKey, entry.getValue());
        }
        return template;
    }

    public static boolean isTokenized(final String target) {
        return target.contains(TEMPLATE_TOKEN_LEFT);
    }

    public static boolean isNumeric(final String target) {
        for (char c : target.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    public static String escapeHtmlEntities(final String toBeEscaped) {
        return toBeEscaped.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }

    public static String escapeSpecialRegexCharacters(final String toEscape) {
        return toEscape.replaceAll(Pattern.quote("{"), "\\\\{")
                .replaceAll(Pattern.quote("}"), "\\\\}")
                .replaceAll(Pattern.quote("["), "\\\\[")
                .replaceAll(Pattern.quote("]"), "\\\\]");
    }

    public static String constructUserAgentName() {
        final Package pkg = StringUtils.class.getPackage();
        final String implementationVersion =
                StringUtils.isSet(pkg.getImplementationVersion()) ? pkg.getImplementationVersion() : "x.x.xx";

        return String.format("stubby4j/%s (HTTP stub client request)", implementationVersion);
    }

    /**
     * Inspired by https://github.com/openjdk/jdk/blob/master/test/lib/jdk/test/lib/security/SecurityUtils.java
     * <p>
     * Removes constraints that contain the specified constraint from the
     * specified security property. For example, List.of("SHA1") will remove
     * any constraint containing "SHA1".
     */
    public static String removeValueFromCsv(final String value, final String... constraints) {
        if (value == null) {
            return "";
        }
        final String delimiter = ",";
        return Arrays.stream(value.split(delimiter))
                .map(String::trim)
                .filter(s -> Arrays.stream(constraints).noneMatch(s::contains))
                .collect(Collectors.joining(delimiter + " ")); // with one space
    }

    public static Set<String> splitCsv(final String value) {
        if (value == null || value.trim().equals("")) {
            return new HashSet<>();
        }
        final String delimiter = ",";
        return Arrays.stream(value.split(delimiter)).map(String::trim).collect(Collectors.toSet());
    }

    public static String encodeBase64(final String toEncode) {
        return BASE_64_ENCODER.encodeToString(StringUtils.getBytesUtf8(toEncode));
    }

    public static String encodeBase16(byte[] bytes) {
        final char[] hexDigits = "0123456789abcdef".toCharArray();

        final StringBuilder sb = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            sb.append(hexDigits[(b >> 4) & 0xf]).append(hexDigits[b & 0xf]);
        }
        return sb.toString();
    }

    public static int calculateStringLength(final String post) {
        if (StringUtils.isSet(post)) {
            return StringUtils.getBytesUtf8(post).length;
        }
        return 0;
    }

    public static String objectToString(final Object fieldObject) {
        if (ObjectUtils.isNull(fieldObject)) {
            return NOT_PROVIDED;
        }

        if (fieldObject instanceof byte[]) {
            final byte[] objectBytes = (byte[]) fieldObject;
            final String toTest = StringUtils.newStringUtf8(objectBytes);

            if (!StringUtils.isUSAscii(toTest)) {
                return "Loaded file is binary - it's content is not displayable";
            } else if (toTest.equals(StringUtils.FAILED)) {
                return StringUtils.FAILED;
            }

            return new String(objectBytes, StandardCharsets.UTF_8);
        } else {
            final String valueAsStr = fieldObject.toString().trim();

            return (!valueAsStr.equalsIgnoreCase("null") ? valueAsStr : "");
        }
    }

    static String join(final String[] segments, final String delimiter) {
        final StringJoiner stringJoiner = new StringJoiner(delimiter);
        for (final String segment : segments) {
            stringJoiner.add(segment);
        }
        return stringJoiner.toString();
    }

    static String repeat(final String repeatable, final int times) {
        if (isNotSet(repeatable) || times < 0) {
            return "";
        }
        return new String(new char[times]).replace("\0", repeatable);
    }

    static String decodeUrlEncodedQuotes(final String toBeFiltered) {
        return toBeFiltered.replaceAll("%22", "\"").replaceAll("%27", "'");
    }

    static String encodeSingleQuotes(final String toBeEncoded) {
        return toBeEncoded.replaceAll("'", "%27");
    }

    static String extractFilenameExtension(final String filename) {
        final int dotLocation = filename.lastIndexOf('.');

        return filename.substring(dotLocation);
    }

    static String trimSpacesBetweenCSVElements(final String toBeFiltered) {
        return toBeFiltered.replaceAll("\",\\s+\"", "\",\"").replaceAll(",\\s+", ",");
    }

    static String removeSquareBrackets(final String toBeFiltered) {
        return toBeFiltered.replaceAll("%5B|%5D|\\[|]", "");
    }

    static boolean isWithinSquareBrackets(final String toCheck) {
        return toCheck.startsWith("%5B") && toCheck.endsWith("%5D") || toCheck.startsWith("[") && toCheck.endsWith("]");
    }

    static String decodeUrlEncoded(final String toCheck) {
        if (toCheck.contains("%2B")) {
            return toCheck.replaceAll("%2B", " ").replaceAll("\\s+", " ");
        } else if (toCheck.contains("%20")) {
            return toCheck.replaceAll("%20", " ").replaceAll("\\s+", " ");
        } else if (toCheck.contains("+")) {
            return toCheck.replaceAll(Pattern.quote("+"), " ").replaceAll("\\s+", " ");
        }

        return toCheck;
    }

    static String pluralize(final long timeUnit) {
        return timeUnit == 1 ? "" : "s";
    }

    private static boolean isUSAscii(final String toTest) {
        return US_ASCII_ENCODER.canEncode(toTest);
    }

    private static boolean isNotBlank(final CharSequence cs) {
        return !isBlank(cs);
    }

    /**
     * Licensed to the Apache Software Foundation (ASF) under one or more
     * contributor license agreements.  See the NOTICE file distributed with
     * this work for additional information regarding copyright ownership.
     * The ASF licenses this file to You under the Apache License, Version 2.0
     * (the "License"); you may not use this file except in compliance with
     * the License.  You may obtain a copy of the License at
     * <p>
     * http://www.apache.org/licenses/LICENSE-2.0
     * <p>
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     * <p>
     * Gets a CharSequence length or {@code 0} if the CharSequence is
     * {@code null}.
     *
     * @param cs a CharSequence or {@code null}
     * @return CharSequence length or {@code 0} if the CharSequence is
     * {@code null}.
     * @since 2.4
     * @since 3.0 Changed signature from length(String) to length(CharSequence)
     */
    private static int length(final CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }

    /**
     * Licensed to the Apache Software Foundation (ASF) under one or more
     * contributor license agreements.  See the NOTICE file distributed with
     * this work for additional information regarding copyright ownership.
     * The ASF licenses this file to You under the Apache License, Version 2.0
     * (the "License"); you may not use this file except in compliance with
     * the License.  You may obtain a copy of the License at
     * <p>
     * http://www.apache.org/licenses/LICENSE-2.0
     * <p>
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     *
     * <p>Checks if a CharSequence is empty (""), null or whitespace only.</p>
     *
     * <p>Whitespace is defined by {@link Character#isWhitespace(char)}.</p>
     *
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is null, empty or whitespace only
     * @since 2.0
     * @since 3.0 Changed signature from isBlank(String) to isBlank(CharSequence)
     */
    private static boolean isBlank(final CharSequence cs) {
        final int strLen = length(cs);
        if (strLen == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
