/*
 * Copyright (C) Paragon Games 2020. All rights reserved.
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package rip.paragon.universalbanlist.util;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

/**
 * @author Levi Taylor
 * @since September 13, 2020
 * Contains formatters for parsing human-readable strings into instances like {@link org.joda.time.DateTime dates}
 * and {@link org.joda.time.Period periods}. These formatters can also be used to format back to human-readable strings.
 */
public class TimeFormatters {

    private TimeFormatters() {}

    /* Used to format durations, such as "18 Months", "12 Months, 2 Weeks", "2 Years, 12 Months" etc. */
    public static final PeriodFormatter DURATION_FORMATTER = new PeriodFormatterBuilder()
            .appendYears()
            .appendSuffix(" Year", " Years")
            .appendSeparator(", ")

            .appendMonths()
            .appendSuffix(" Month", " Months")
            .appendSeparator(", ")

            .appendWeeks()
            .appendSuffix(" Week", " Weeks")
            .appendSeparator(", ")

            .appendMinutes()
            .appendSuffix(" Minute", " Minutes")
            .appendSeparator(", ")

            .appendSeconds()
            .appendSuffix(" Second", " Seconds")
            .appendSeparator(", ")

            .toFormatter();

    /* Used for parsing dates such as "12 September, 2022" */
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("dd MMMM, yyyy");

}
