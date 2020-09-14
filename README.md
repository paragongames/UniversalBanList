# UniversalBanList
A modern replacement for AutoUBL.

## Why use UniversalBanList instead of AutoUBL?
- Beats AutoUBL in speed; it's able to fetch the ban list and determine if a player is banned much faster with less CPU and RAM usage.
- Import a ban list from anywhere; you can use a HTTP(S) URL, a file on the local file system, or anything as long as it's in CSV format.
- Zero caching; UniversalBanList doesn't cache any bans. AutoUBL uses much more RAM than UniversalBanList does because it caches every ban
on the ban list (currently nearly 500.) UniversalBanList only fetches data when it needs to. This also means that if you're using a local source,
such as a file, you won't need to reload the plugin for the ban list to be reloaded, which is useful in the event you want to update your own list while
your servers are running.
- Future-proof; UniversalBanList is able to parse dates and times that the UBL spreadsheet doesn't even use, like years. It also skips over any ban entries
that have malformed data due to human error.
- Secure; you can use UniversalBanList on a machine with no Internet access, as long as you import a ban list from a local source (like a file) in the plugin's configuration.
Super useful if the machine or VM is located inside a Virtual Private Cloud, or doesn't have Internet access for security purposes.
- Modern API; UniversalBanList has an API you can use to fetch bans related to a player (past or present), every ban, or bans matching a condition.
- Long Term Support; Paragon Games will maintain UniversalBanList while Paragon Network is using the Reddit platform, and most likely even after it stops using it.

## Setup
- Put the plugin into your plugins folder.
That's it unless you want to edit the plugin's configuration, like the ban message when a banned player tries to join or use your own ban list.

### Editing the ban message
You can edit the `kickMessages` section in the plugin's `config.yml` file to customize your ban message. We provide a nice one by default, but you can customize it to whatever you choose!

Placeholders:\
All placeholders should be surrounded with curly brackets (for example: `{reason}`)\
`reason` The reason for the ban\
`banDate` The date the ban was made\
`banLength` The original length of the ban, will be "Forever" if permanent\
`expireDate` The date when the ban expires, will be "Never" if permanent\
`case` The URL to the courtroom case on Reddit, usually something like `https://redd.it/<uid>`

### Using your own ban list
Most people would want to just use the Google Spreadsheet which are where all the UBL bans are stored, however there are legitimate use-cases for wanting to use your own ban list.\
You can use your own ban list as long as it can be provided in URL format. This means you can use HTTP(S) URL's, or a URL to a local file.

URL examples:\
`https://example.com/ubl_ban_list.csv` - HTTP(S)\
`file:///C:/Users/MyUser/Desktop/ubl_ban_list.csv` Local file (Windows)\
`file:///srv/myserverdirectory/plugins/UniversalBanList/my_custom_ban_list.csv` Local file (Linux)\
You can use any file or URL as long as it points to a CSV resource, and the user account used to run the server has permission to read it. Write permissions are not needed as
the plugin will never attempt to write to the file. This use-case mostly applies to Linux systems where user permissions are important.

As long as the URL points to a valid CSV resource, the plugin will use your ban list.\
You can also update the file whenever you want (or in case of a website, the data returned from the URL) as the plugin will never cache bans.

Note for using files: the CSV file must have a header at the top of it, or it will not load. Example:
```
IGN,UUID,Reason,Date Banned,Length of Ban,Expiry Date,Case
Suggesting,91ce6853-830a-4a7f-bce3-25858cd79cc2,Test ban reason,"2 July, 2020",8 Months,"5 December, 2049",https://redd.it/testurl
```
The first line being the header, the second line being an example ban.\
Note that the header fields do not matter as long as there is the correct amount, as the plugin uses its own CSV heading when parsing CSV data.

## Developers
Pull requests with appropriate changes are welcome. All Maven artifacts used are in public repositories. Please don't use artifacts that point to a local file or an artifact that is not
available to the public Internet (like an artifact that requires authentication or similar.)

This project is licensed under the MIT License.

## Known won't-fix issues
Unfortunately, some ban entries on the Universal Ban List spreadsheet contains malformed data, like invalid characters, so some bans listed on the spreadsheet will not be loaded
by the plugin.\
This is unfortunately due to the irresponsibility of the Reddit UHC/UBL admins as these malformed entries have not been fixed for months. We do not take responsibility for
these malformed entries.
