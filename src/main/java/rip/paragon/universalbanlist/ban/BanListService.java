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

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.jetbrains.annotations.NotNull;
import rip.paragon.universalbanlist.UniversalBanList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

/**
 * @author Levi Taylor
 * @since September 14, 2020
 * The service used to manage ban entries.
 */
public class BanListService {

    public static final BanListService INSTANCE = new BanListService();

    private BanListService() {}

    /**
     * Gets a {@link List list} of {@link BanEntry bans} based on the specified {@link Predicate selector}.
     *
     * This method should be called asynchronously.
     *
     * @param selector The {@link Predicate selector} to determine if a {@link BanEntry ban} should be added to the result.
     * @return The list of {@link BanEntry bans} that matched the specified {@link Predicate selector}.
     */
    public @NotNull List<@NotNull BanEntry> getBanEntries(@NotNull Predicate<@NotNull BanEntry> selector) {
        try {
            URLConnection connection = UniversalBanList.INSTANCE.getBanListURL().openConnection();

            // If this is a HTTP connection, check the response code
            if (connection instanceof HttpURLConnection) {
                HttpURLConnection httpConn = (HttpURLConnection) connection;
                int httpCode = httpConn.getResponseCode();

                // We don't know how to handle non-OK response codes, log and exit now
                if (httpCode != HttpURLConnection.HTTP_OK) {
                    UniversalBanList.INSTANCE.getLogger().severe("Non-OK HTTP response code returned " + httpCode);

                    return Collections.emptyList();
                }
            }

            // The reader
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            // The CSV records
            List<@NotNull CSVRecord> records = new ArrayList<>(
                    CSVParser.parse(
                            reader,

                            CSVFormat.DEFAULT
                            .withHeader(BanEntry.RECORD_MAPPINGS)
                            .withSkipHeaderRecord()
                    ).getRecords()
            );

            // The result
            List<@NotNull BanEntry> entries = new ArrayList<>();

            // Deserialize the records and add them to the result if it matches the selector
            for (CSVRecord record : records) {
                BanEntry entry;

                try {
                    entry = BanEntry.deserialize(record);
                } catch (IOException e) {
                    // Some records in the spreadsheet are malformed.
                    // This is unfortunately due to the incompetency of the Reddit hosting platform.
                    // Because of this, we just silence any exception and continue.
                    continue;
                }

                // Add the ban to the result if it passes the predicate's test
                if (selector.test(entry)) {
                    entries.add(entry);
                }
            }

            return entries;
        } catch (IOException e) {
            UniversalBanList.INSTANCE.getLogger().severe("Couldn't fetch records");
            e.printStackTrace();

            return Collections.emptyList();
        }
    }

    /**
     * Fetches a live version of the ban list from the URL specified in the plugin's configuration file.
     * @return A {@link CompletableFuture future} containing a {@link List list} of {@link BanEntry ban entries}.
     *         Not all ban entries in the result may be active; some may be expired.
     *         It should be assumed that the list is un-modifiable.
     */
    public @NotNull CompletableFuture<@NotNull List<@NotNull BanEntry>> retrieve() {
        return CompletableFuture.supplyAsync(() -> this.getBanEntries(entry -> true));
    }

    /**
     * Gets all bans related to the specified {@link UUID}, whether active or expired.
     * @param uuid The player's UUID
     * @return A {@link CompletableFuture completable future} containing a {@link List} containing ban entries
     *         related to the specified player UUID. If there are no entries, the list will be empty.
     */
    public @NotNull CompletableFuture<@NotNull List<@NotNull BanEntry>> getBanEntries(@NotNull UUID uuid) {
        return CompletableFuture.supplyAsync(() -> this.getBanEntries(entry -> entry.playerID.equals(uuid)));
    }

}
