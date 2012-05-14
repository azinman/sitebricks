package com.google.sitebricks.mail.imap;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author dhanji@gmail.com (Dhanji R. Prasanna)
 */
class IdExtractor implements Extractor<Map<String, String>> {
  private static final Pattern PARENS = Pattern.compile("([(].*[)])");

  @Override
  public Map<String, String> extract(List<String> messages) {
    Map<String, String> vendorData = Maps.newLinkedHashMap();
    // There should always be 1.
    for (String message : messages) {
      Matcher matcher = PARENS.matcher(message);
      if (matcher.find()) {
        String group = matcher.group(1);

        // Strip parens.
        group = group.substring(1, group.length() - 1);
        String[] pieces = group.split("[ \"]+");
        int start = 0;
        for (int i = 0; i < pieces.length; i++) {
          if (pieces[i].length() == 0) {
            start = i;
            break;
          }
        }
        for (int i = start; i < pieces.length; i += 2) {
          String key = pieces[i].toUpperCase();
          String value = pieces[i+1].toUpperCase();
          vendorData.put(key, value);
        }
      }
    }
    return vendorData;
  }
}
