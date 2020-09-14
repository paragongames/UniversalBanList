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

import org.joda.time.Period;
import org.junit.Test;
import rip.paragon.universalbanlist.util.TimeFormatters;

/**
 * @author Levi Taylor
 * @since September 14, 2020
 * Tests parsing with {@link Period periods}.
 */
public class PeriodParserTest {

    @Test
    public void testPeriodParsing() {
        // 5 years - something uncommon on UBL
        Period period = TimeFormatters.DURATION_FORMATTER.parsePeriod("5 Years");

        assert period.getYears() == 5 : "Years should be 5, not " + period.getYears();

        // 5 years, 2 months - also uncommon
        period = TimeFormatters.DURATION_FORMATTER.parsePeriod("5 Years, 2 Months");

        assert period.getYears() == 5 && period.getMonths() == 2 : String.format(
                "Years and months should be 5 and 2, not %d and %d",
                period.getYears(),
                period.getMonths()
        );

        // Something common on UBL - they like to use an excess amount of months instead of formatting to years.
        period = TimeFormatters.DURATION_FORMATTER.parsePeriod("71 Months");

        assert period.getMonths() == 71 : "Months should be 71, not " + period.getMonths();

        // Something else they could also use
        period = TimeFormatters.DURATION_FORMATTER.parsePeriod("1 Month");

        assert period.getMonths() == 1 : "Months should be 1, not " + period.getMonths();

        // Just in case a UBL admin failed English... (also works with other time specifications)
        period = TimeFormatters.DURATION_FORMATTER.parsePeriod("1 Months");

        assert period.getMonths() == 1 : "Months should be 1, not " + period.getMonths();

        // Weeks - could also be common on UBL
        period = TimeFormatters.DURATION_FORMATTER.parsePeriod("3 Weeks");

        assert period.getWeeks() == 3 : "Weeks should be 3, not " + period.getWeeks();

        // Minutes - should never happen unless they're testing something
        period = TimeFormatters.DURATION_FORMATTER.parsePeriod("2 Minutes");

        assert period.getMinutes() == 2 : "Minutes should be 2, not " + period.getMinutes();

        // Seconds - same case as above
        period = TimeFormatters.DURATION_FORMATTER.parsePeriod("2 Seconds");

        assert period.getSeconds() == 2 : "Seconds should be 2, not " + period.getSeconds();

        // Something they would probably never do, but we want to have compatibility for anyways
        period = TimeFormatters.DURATION_FORMATTER.parsePeriod("2 Years, 10 Months, 5 Weeks, 1 Second");

        assert period.getYears() == 2 && period.getMonths() == 10 && period.getWeeks() == 5 && period.getSeconds() == 1;
    }

}
