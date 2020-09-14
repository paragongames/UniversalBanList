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

package rip.paragon.universalbanlist.ban;

import org.apache.commons.csv.CSVRecord;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.joda.time.Period;
import rip.paragon.universalbanlist.util.TimeFormatters;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Levi Taylor
 * @since September 12, 2020
 * Represents a ban entry. May be expired.
 */
public final class BanEntry {

    /* The Minecraft username at the time of the ban */
    public final @NotNull String username;

    /* The player UUID */
    public final @NotNull UUID playerID;

    /* The ban reason */
    public final @NotNull String reason;

    /* The date/time of the ban */
    public final @NotNull DateTime banTime;

    /* The original length of the ban. Will be null if permanent. */
    public final @Nullable Period banLength;

    /* The time when the ban expires. Will be null if permanent. */
    public final @Nullable DateTime expireDate;

    /* The URL to the Reddit post regarding this case, in the format of https://redd.it/<uid> */
    public final @NotNull String caseURL;

    public BanEntry(
            @NotNull String username,
            @NotNull UUID playerID,
            @NotNull String reason,
            @NotNull DateTime banTime,
            @Nullable Period banLength,
            @Nullable DateTime expireDate,
            @NotNull String caseURL
    ) {
        this.username = username;
        this.playerID = playerID;
        this.reason = reason;
        this.banTime = banTime;
        this.banLength = banLength;
        this.expireDate = expireDate;
        this.caseURL = caseURL;
    }

    /**
     * Gets the remaining time of this ban.
     *
     * The use of this method is not recommended because the amount of days in a month can vary.
     * If you want to check if this ban is active, you should use {@link BanEntry#isActive()} instead.
     *
     * @return The remaining time, in milliseconds.
     *         Will be negative if the ban has expired. In this case, the result is the amount of time
     *         since the ban has expired.
     *         If the ban is permanent, the result will be {@link Long#MAX_VALUE max value}.
     */
    public long getRemainingTime() {
        return this.expireDate != null ? this.expireDate.getMillis() - System.currentTimeMillis() : Long.MAX_VALUE;
    }

    /**
     * Gets if this ban is active.
     *
     * This does not check the current time remaining, but rather checks if the current date is before
     * the expiry date (in which case means the ban is active.)
     *
     * @return If this ban is active
     */
    public boolean isActive() {
        return this.expireDate == null || DateTime.now().isBefore(this.expireDate);
    }

    /**
     * Gets if this ban is permanent.
     * @return If this ban is permanent
     */
    public boolean isPermanent() {
        return this.expireDate == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || this.getClass() != o.getClass()) return false;

        BanEntry entry = (BanEntry) o;

        return this.username.equals(entry.username) &&
                this.playerID.equals(entry.playerID) &&
                this.reason.equals(entry.reason) &&
                this.banTime.equals(entry.banTime) &&
                Objects.equals(this.banLength, entry.banLength) &&
                Objects.equals(this.expireDate, entry.expireDate) &&
                this.caseURL.equals(entry.caseURL);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.username,
                this.playerID,
                this.reason,
                this.banTime,
                this.banLength,
                this.expireDate,
                this.caseURL
        );
    }

    @Override
    public String toString() {
        return "BanEntry{" +
                "username='" + this.username + '\'' +
                ", playerID=" + this.playerID +
                ", reason='" + this.reason + '\'' +
                ", banTime=" + this.banTime +
                ", banLength=" + this.banLength +
                ", expireDate=" + this.expireDate +
                ", caseURL='" + this.caseURL + '\'' +
                '}';
    }

    /* The header used in CSV parsing */
    public static final String[] RECORD_MAPPINGS = {
            "IGN",
            "UUID",
            "Reason",
            "Date Banned",
            "Length of Ban",
            "Expiry Date",
            "Case"
    };

    /**
     * Deserializes a {@link CSVRecord CSV record} into a {@link BanEntry ban entry}.
     * @param record The {@link CSVRecord CSV record} containing ban data
     * @return The {@link BanEntry ban entry} instance, with the data parsed from the {@link CSVRecord CSV record}.
     * @throws IOException If any data in the {@link CSVRecord CSV record} provided is considered invalid.
     */
    public static @NotNull BanEntry deserialize(@NotNull CSVRecord record) throws IOException {
        // Ensure all records are mapped before continuing on
        for (String mapping : RECORD_MAPPINGS) {
            if (!record.isMapped(mapping)) {
                throw new IOException(String.format("Mapping %s is not present", mapping));
            }
        }

        // Start the parsing process

        // Username at the time of the ban
        String username = record.get("IGN");

        // Player UUID
        UUID playerID;
        try {
            playerID = UUID.fromString(record.get("UUID"));
        } catch (IllegalArgumentException e) {
            throw new IOException("Invalid UUID " + record.get("UUID"), e);
        }

        // Ban reason
        String reason = record.get("Reason");

        // Date banned
        DateTime dateBanned;

        try {
            dateBanned = TimeFormatters.DATE_FORMATTER.parseDateTime(record.get("Date Banned"));
        } catch (IllegalArgumentException e) {
            throw new IOException("Couldn't parse ban date", e);
        }

        // Ban length
        Period banLength = null;

        if (!record.get("Length of Ban").equals("Permanent")) {
            try {
                banLength = TimeFormatters.DURATION_FORMATTER.parsePeriod(record.get("Length of Ban"));
            } catch (IllegalArgumentException e) {
                throw new IOException("Couldn't parse ban length", e);
            }
        }

        // Expiry date
        DateTime expireDate = null;
        if (!record.get("Expiry Date").equals("Never")) {
            try {
                expireDate = TimeFormatters.DATE_FORMATTER.parseDateTime(record.get("Expiry Date"));
            } catch (IllegalArgumentException e) {
                throw new IOException("Couldn't parse expiry date");
            }
        }

        // Case URL
        String caseURL = record.get("Case");

        return new BanEntry(username, playerID, reason, dateBanned, banLength, expireDate, caseURL);
    }

}
