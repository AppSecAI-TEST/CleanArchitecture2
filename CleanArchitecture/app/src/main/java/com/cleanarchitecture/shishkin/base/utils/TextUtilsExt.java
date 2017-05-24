package com.cleanarchitecture.shishkin.base.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.Locale;

/**
 * {@code ViewUtils} contains static methods which operate on {@code String}
 * and {@code CharSequence}.
 */
public class TextUtilsExt {

    /**
     * Returns true if the cahr sequence is null or 0-length.
     *
     * @param cs the string to be examined
     * @return true if str is null or zero length
     */
    public static boolean isEmpty(@Nullable final CharSequence cs) {
        return TextUtils.isEmpty(cs);
    }

    /**
     * Returns true if a and b are equal, including if they are both null.
     * <p><i>Note: In platform versions 1.1 and earlier, this method only worked well if
     * both the arguments were instances of String.</i></p>
     *
     * @param a first CharSequence to check
     * @param b second CharSequence to check
     * @return true if a and b are equal
     */
    public static boolean equals(@Nullable final CharSequence a, @Nullable final CharSequence b) {
        return TextUtils.equals(a, b);
    }

    /**
     * Compares the given strings ignoring case.
     * <p/>
     * <p>The strings are compared one {@code char} at a time. This is not suitable
     * for case-insensitive string comparison for all locales.
     * Use a {@link java.text.Collator} instead.
     */
    public static boolean equalsIgnoreCase(@Nullable final CharSequence cs1, @Nullable final CharSequence cs2) {
        final String str1 = (cs1 == null ? null : cs1.toString());
        final String str2 = (cs2 == null ? null : cs2.toString());
        return equalsIgnoreCase(str1, str2);
    }

    /**
     * Compares the given strings ignoring case.
     * <p/>
     * <p>The strings are compared one {@code char} at a time. This is not suitable
     * for case-insensitive string comparison for all locales.
     * Use a {@link java.text.Collator} instead.
     */
    public static boolean equalsIgnoreCase(@Nullable final String str1, @Nullable final String str2) {
        return (str1 == null ? str2 == null : str1.equalsIgnoreCase(str2));
    }

    /**
     * Converts this string to lower case, using the rules of {@code locale}.
     * <p/>
     * <p>Most case mappings are unaffected by the language of a {@code Locale}. Exceptions include
     * dotted and dotless I in Azeri and Turkish locales, and dotted and dotless I and J in
     * Lithuanian locales. On the other hand, it isn't necessary to provide a Greek locale to get
     * correct case mapping of Greek characters: any locale will do.
     * <p/>
     * <p>See <a href="http://www.unicode.org/Public/UNIDATA/SpecialCasing.txt">http://www.unicode.org/Public/UNIDATA/SpecialCasing.txt</a>
     * for full details of context- and language-specific special cases.
     *
     * @return a new lower case string, or {@code this} if it's already all lower case.
     */
    @Nullable
    public static String toLowerCase(@Nullable final String s) {
        return (s == null ? null : s.toLowerCase(Locale.getDefault()));
    }

    /**
     * Converts this this string to upper case, using the rules of {@code locale}.
     * <p/>
     * <p>Most case mappings are unaffected by the language of a {@code Locale}. Exceptions include
     * dotted and dotless I in Azeri and Turkish locales, and dotted and dotless I and J in
     * Lithuanian locales. On the other hand, it isn't necessary to provide a Greek locale to get
     * correct case mapping of Greek characters: any locale will do.
     * <p/>
     * <p>See <a href="http://www.unicode.org/Public/UNIDATA/SpecialCasing.txt">http://www.unicode.org/Public/UNIDATA/SpecialCasing.txt</a>
     * for full details of context- and language-specific special cases.
     *
     * @return a new upper case string, or {@code this} if it's already all upper case.
     */
    @Nullable
    public static String toUpperCase(@Nullable final String s) {
        return (s == null ? null : s.toUpperCase(Locale.getDefault()));
    }

    /**
     * Returns a string containing the tokens joined by delimiters.
     *
     * @param tokens an array objects to be joined. Strings will be formed from
     *               the objects by calling object.toString().
     */
    @NonNull
    public static String join(final CharSequence delimiter, @NonNull final Object[] tokens) {
        return TextUtils.join(delimiter, tokens);
    }

    /**
     * Returns a string containing the tokens joined by delimiters.
     *
     * @param tokens an array objects to be joined. Strings will be formed from
     *               the objects by calling object.toString().
     */
    @NonNull
    public static String join(final CharSequence delimiter, @NonNull final Iterable tokens) {
        return TextUtils.join(delimiter, tokens);
    }

    /**
     * String.split() returns [''] when the string to be split is empty. This returns []. This does
     * not remove any empty strings from the result. For example split("a,", ","  ) returns {"a", ""}.
     *
     * @param text       the string to split
     * @param expression the regular expression to match
     * @return an array of strings. The array will be empty if text is empty
     * @throws NullPointerException if expression or text is null
     */
    @NonNull
    public static String[] split(@NonNull final String text, @NonNull final String expression) {
        return TextUtils.split(text, expression);
    }

    /**
     * Returns a string containing the given subsequence of this string.
     * The returned string shares this string's <a href="#backing_array">backing array</a>.
     *
     * @param start the start offset.
     * @param end   the end+1 offset.
     */
    @NonNull
    public static String safeSubString(final String source, final int start, final int end) {
        final String subString;
        if (source == null || start > end || source.length() < start) {
            subString = "";
        } else if (source.length() < end) {
            subString = source.substring(start, source.length());
        } else {
            subString = source.substring(start, end);
        }
        return subString;
    }

    /**
     * Sets the current primary clip on the clipboard.  This is the clip that
     * is involved in normal cut and paste operations.
     *
     * @param text The clipped data item to set.
     */
    public static void putTextToClipboard(@NonNull Context context, @NonNull final String text) {
        final ClipboardManager clipboard = ApplicationUtils.getSystemService(context, Context.CLIPBOARD_SERVICE);
        final ClipData clip = ClipData.newPlainText(null, text);
        clipboard.setPrimaryClip(clip);
    }

    private TextUtilsExt() {
    }

}
