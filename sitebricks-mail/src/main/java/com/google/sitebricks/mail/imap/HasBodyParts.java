package com.google.sitebricks.mail.imap;

import com.google.common.collect.Multimap;

import java.util.List;

/**
 * @author dhanji@gmail.com (Dhanji R. Prasanna)
 */
public interface HasBodyParts {
  List<Message.BodyPart> getBodyParts();

  Multimap<String, String> getHeaders();

  void setBody(String mimeType, String body);
  void setBodyBytes(String mimeType, byte[] body);
  void setMimeType(String mimeType);
  void createBodyParts();
}
