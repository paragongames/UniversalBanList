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

package rip.paragon.universalbanlist.test;

import org.joda.time.DateTime;
import org.junit.Test;
import rip.paragon.universalbanlist.util.TimeFormatters;

import java.time.Month;

/**
 * @author Levi Taylor
 * @since September 14, 2020
 * Tests parsing with {@link DateTime dates}.
 */
public class DateParserTest {

    @Test
    public void testDateParsing() {
        // A date in the past
        DateTime date = TimeFormatters.DATE_FORMATTER.parseDateTime("12 September, 2018");

        assert date.getDayOfMonth() == 12 && date.getMonthOfYear() == Month.SEPTEMBER.getValue() && date.getYear() == 2018 : String.format(
                "Day should be 12, month should be September (%d), year should be 2018, not %d, %d, %d",
                Month.SEPTEMBER.getValue(),
                date.getDayOfMonth(),
                date.getMonthOfYear(),
                date.getYear()
        );

        // A date in the future...we hope!
        date = TimeFormatters.DATE_FORMATTER.parseDateTime("5 January, 2049");

        assert date.getDayOfMonth() == 5 && date.getMonthOfYear() == Month.JANUARY.getValue() && date.getYear() == 2049 : String.format(
                "Day should be 5, month should be January (%d), year should be 2049, not %d, %d, %d",
                Month.JANUARY.getValue(),
                date.getDayOfMonth(),
                date.getMonthOfYear(),
                date.getYear()
        );
    }

}
