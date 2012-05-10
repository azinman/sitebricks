package com.google.sitebricks.util;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * A modification of the GNU implementation to keep the timezone component in
 * parsing mail date formats.
 *
 */
public class MailDateFormat {
  private static final String[] MONTHS = {
    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
  };

 /**
   * Parses the given date in the format specified by
   * draft-ietf-drums-msg-fmt-08 in the current TimeZone.
   * @param text the formatted date to be parsed
   */
  public static GregorianCalendar parse(String text)
  {
    int start = 0, end = -1;
    int len = text.length();
    GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
    calendar.clear();
    ParsePosition pos = new ParsePosition(start);
    try
      {
        // Advance to date
        if (Character.isLetter(text.charAt(start)))
          {
            start = skipNonWhitespace(text, start, len);
          }
        start = skipWhitespace(text, start, len);
        pos.setIndex(start);
        end = skipNonWhitespace(text, start + 1, len);
        int date = Integer.parseInt(text.substring(start, end));
        // Advance to month
        start = skipWhitespace(text, end + 1, len);
        pos.setIndex(start);
        end = skipNonWhitespace(text, start + 1, len);
        String monthText = text.substring(start, end);
        int month = -1;
        for (int i = 0; i < 12; i++)
          {
            if (MONTHS[i].equals(monthText))
              {
                month = i;
                break;
              }
          }
        if (month == -1)
          {
            pos.setErrorIndex(end);
            return null;
          }
        // Advance to year
        start = skipWhitespace(text, end + 1, len);
        pos.setIndex(start);
        end = skipNonWhitespace(text, start + 1, len);
        int year = Integer.parseInt(text.substring(start, end));
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, date);
        // Advance to hour
        start = skipWhitespace(text, end + 1, len);
        pos.setIndex(start);
        end = skipToColon(text, start + 1, len);
        int hour = Integer.parseInt(text.substring(start, end));
        calendar.set(Calendar.HOUR, hour);
        // Advance to minute
        start = end + 1;
        pos.setIndex(start);
        end = skipToColon(text, start + 1, len);
        int minute = Integer.parseInt(text.substring(start, end));
        calendar.set(Calendar.MINUTE, minute);
        // Advance to second
        start = end + 1;
        pos.setIndex(start);
        end = skipNonWhitespace(text, start + 1, len);
        int second = Integer.parseInt(text.substring(start, end));
        calendar.set(Calendar.SECOND, second);

        if (end != len)
          {
            start = skipWhitespace(text, end + 1, len);
            if (start != len)
              {
                // Trailing characters, therefore timezone
                end = skipNonWhitespace(text, start + 1, len);
                char pm = text.charAt(start);
                if (Character.isLetter(pm))
                  {
                    TimeZone tz =
                      TimeZone.getTimeZone(text.substring(start, end));
                    calendar.set(Calendar.ZONE_OFFSET, tz.getRawOffset());
                  }
                else
                  {
                    int zoneOffset = 0;
                    zoneOffset +=
                      600 * Character.digit(text.charAt(++start), 10);
                    zoneOffset +=
                      60 * Character.digit(text.charAt(++start), 10);
                    zoneOffset +=
                      10 * Character.digit(text.charAt(++start), 10);
                    zoneOffset +=
                      Character.digit(text.charAt(++start), 10);
                    zoneOffset *= 60000; // minutes -> ms
                    if ('-' == pm)
                      {
                        zoneOffset = -zoneOffset;
                      }
                    calendar.set(Calendar.ZONE_OFFSET, zoneOffset);
                  }
              }
          }
        pos.setIndex(end);

        return calendar;
      }
    catch (NumberFormatException e)
      {
        pos.setErrorIndex(Math.max(start, end));
      }
    catch (StringIndexOutOfBoundsException e)
      {
        pos.setErrorIndex(Math.max(start, end));
      }
    return null;
  }

  private static int skipWhitespace(final String text, int pos, final int len)
  {
    while (pos < len && Character.isWhitespace(text.charAt(pos)))
      {
        pos++;
      }
    return pos;
  }

  private static int skipNonWhitespace(final String text, int pos, final int len)
  {
    while (pos < len && !Character.isWhitespace(text.charAt(pos)))
      {
        pos++;
      }
    return pos;
  }

  private static int skipToColon(final String text, int pos, final int len)
  {
    while (pos < len && text.charAt(pos) != ':')
      {
        pos++;
      }
    return pos;
  }
}