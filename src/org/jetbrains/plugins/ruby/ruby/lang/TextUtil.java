/*
 * Copyright 2000-2008 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.plugins.ruby.ruby.lang;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 19.07.2006
 */
public class TextUtil {
    @NonNls
    public static final String DOTS = "...";

    private static final Pattern CID_PATTERN = Pattern.compile("[_A-Za-z][_A-Za-z0-9]*");
    private static final Pattern UPPER_CASE_ID_PATTERN = Pattern.compile("[A-Z][_a-z0-9]*");
    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile("[_a-z][_A-Za-z0-9]*");
    private static final Pattern FID_PATTERN = Pattern.compile("[_a-z][_A-Za-z0-9]*(!|\\?)");
    private static final Pattern AID_PATTERN = Pattern.compile("[_a-z][_A-Za-z0-9]*=");
    private static final Pattern EOL_SPLIT_PATTERN = Pattern.compile(" *(\r|\n|\r\n)+ *");
    @NonNls
    public static final String EMPTY_STRING = "";
    @NonNls
    public static final String SPACE_STRING = " ";

    public static boolean isEol(final char c){
        return c=='\n' || c== '\r';
    }

    /**
     * @param c character to inspect
     * @return true if c is whitespace char
     */
    public static boolean isWhiteSpace(final char c) {
        return c==' ' || c=='\t' || c=='\f' || c=='\r' || c=='\13';
    }

    /**
     * @param c character to inspect
     * @return true if c is whitespace char or End of line
     */
    public static boolean isWhiteSpaceOrEol(final char c) {
        return isWhiteSpace(c) || c == '\n';
    }

    /**
     * @param c Character
     * @return corresponding close delim
     */
    public static char getOpenDelim(final char c) {
        if (c == ')') return '(';
        if (c == '>') return '<';
        if (c == '}') return '{';
        if (c == ']') return '[';
        return c;
    }

    /**
     * @param c Character
     * @return corresponding close delim
     */
    public static char getCloseDelim(final char c) {
        if (c == '(') return ')';
        if (c == '<') return '>';
        if (c == '{') return '}';
        if (c == '[') return ']';
        return c;
    }

/*
    case 'i': ReOptions.RE_OPTION_IGNORECASE;
    case 'x': ReOptions.RE_OPTION_EXTENDED;
    case 'm': ReOptions.RE_OPTION_MULTILINE;
    case 'o': ReOptions.RE_OPTION_ONCE;
    case 'n':
    case 'e':
    case 's':
    case 'u':
*/
    /**
    * @param c Char to inspect
    * @return true, if symbol c can be regexp modifier
    */
    public static boolean isRegexpModifier(final char c) {
        return c=='i' || c=='x' || c=='m' || c=='o' || c=='n' || c=='e' || c=='s' || c=='u';
    }

    /**
     * Splits string by lines.
     * @param string String to split
     * @return array of strings
     */
    public static String[] splitByLines(final String string){
        return EOL_SPLIT_PATTERN.split(string);
    }


    /**
     * Unions many strings in one
     * @param strings Srtings to concat
     * @return The result of concantenation
     */
    public static String concat(final String ... strings){
        final StringBuilder result = new StringBuilder(strings[0]);
        for (int i=1; i< strings.length; i++){
            if (strings[i].length() > 0){
                result.append(SPACE_STRING);
                result.append(strings[i]);
            }
        }
        return result.toString();
    }

    /**
     * @param c Character to inspect
     * @return True if c is a brace like char, false otherwise
     */
    public static boolean isBraceLikeDelim(final char c){
        return isOpenBrace(c) || isCloseBrace(c) || c == '<' || c == '>';
    }

    /**
     * @param c Character to inspect
     * @return True if c is a open brace, false otherwise
     */
    public static boolean isOpenBrace(final char c){
        return c=='(' || c=='{' || c=='[';
    }

    /**
     * @param c Character to inspect
     * @return True if c is a close brace, false otherwise
     */
    public static boolean isCloseBrace(final char c){
        return c==')' || c=='}' || c==']';
    }

    /**
     * Checks if content is an identifier or a constant
     * @param content string to check
     * @return true if content is an identifier or a constant
     */
    public static boolean isCID(final String content) {
        return CID_PATTERN.matcher(content).matches();
    }

    /**
     * Checks if content is a FID
     * @param content string to check
     * @return true if content is an identifier or a constant
     */
    public static boolean isFID(final String content) {
        return FID_PATTERN.matcher(content).matches();
    }

    /**
     * Checks if content is a AID
     * @param content string to check
     * @return true if content is an identifier or a constant
     */
    public static boolean isAID(final String content) {
        return AID_PATTERN.matcher(content).matches();
    }

    public static boolean isUpperCaseId(final String content) {
        return UPPER_CASE_ID_PATTERN.matcher(content).matches();
    }

    public static boolean isIdentifier(final String content) {
        return IDENTIFIER_PATTERN.matcher(content).matches();
    }


    public static boolean isQuote(final char c){
        return c=='"' || c == '\'';
    }

    public static char getCorrespondingChar(final char c) {
        if (isOpenBrace(c)){
            return getCloseDelim(c);
        }
        if (isCloseBrace(c)){
            return getOpenDelim(c);
        }
        if (isQuote(c)){
            return c;
        }
        return (char) -1;
    }

    /**
     * @param s String
     * @return true if string is empty or equals null.
     */
    public static boolean isEmpty(final String s) {
        return (s == null || EMPTY_STRING.equals(s));
    }

    /**
     * @param s String
     * @return true if string is empty, equals null or contains only whitespeces.
     */
    public static boolean isEmptyOrWhitespaces(final String s) {
        return (s == null || EMPTY_STRING.equals(s.trim()));
    }

    /**
     * Truncates string from buffer to given width. Width of string will be less
     * or equal to <code>width</code>. If initial string greater than width the
     * beginning of the string will be replaced with <code>DOTS</code>.
     * For example:
     *     "admin/login/secure/LoginController" -> "...ure/LoginController" 
     * @param buff String
     * @param width Width in pixels
     * @param fontMetrics Font metrics uses for string width computing.
     */
    public static void truncWithDots(final StringBuffer buff,
                                     final int width,
                                     final FontMetrics fontMetrics) {
        if (width > 0 && fontMetrics.stringWidth(buff.toString()) > width) {
            buff.insert(0, DOTS);
            while (fontMetrics.stringWidth(buff.toString()) > width) {
                buff.delete(2,3);
            }
        }
    }

    @NotNull
    public static String wrapInParens(@NotNull final String s){
        return "(" + s + ")";
    }

    @NotNull
    public static String replaceEOLS(@NotNull final String s){
        return s.replace("\r\n","\n").replace("\r","\n");
    }

    public static String getAsNotNull(final String str) {
        return str != null ? str : EMPTY_STRING;
    }
}
