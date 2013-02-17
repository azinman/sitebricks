package com.google.sitebricks.mail.imap;

import com.google.common.base.Supplier;
import com.google.common.collect.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a complete IMAP message with all body parts materialized
 * and decoded as appropriate (for example, non-UTF8 encodings are re-encoded
 * into UTF8 for raw and rich text).
 *
 * @author dhanji@gmail.com (Dhanji R. Prasanna)
 */
public class Message implements HasBodyParts, java.io.Serializable {
  static final long serialVersionUID = 0L;
  public static final Message ERROR = new Message();
  public static final Message EMPTIED = new Message();

  private MessageStatus status;
  private int imapUid;
  private long gmailMsgId = -1;

  // A header can have multiple, different values.
  private Multimap<String, String> headers = newListMultimap();
  private List<BodyPart> bodyParts = new ArrayList<BodyPart>();
  private String rootMimeType = null;

  public void setImapUid(int imapUid) {
    this.imapUid = imapUid;
  }

  public int getImapUid() {
    return imapUid;
  }

  public void setGmailMsgId(long gmailMsgId) {
    this.gmailMsgId = gmailMsgId;
  }

  public long getGmailMsgId() {
    return gmailMsgId;
  }

  public void setHeaders(Multimap<String, String> headers) {
    this.headers = headers;
  }

  public MessageStatus getStatus() {
    return status;
  }

  public void setStatus(MessageStatus status) {
    this.status = status;
  }
  public Multimap<String, String> getHeaders() {
    return headers;
  }

  public List<BodyPart> getBodyParts() {
    return bodyParts;
  }

  public void setBodyParts(List<BodyPart> parts) {
    this.bodyParts = parts;
  }

  @Override public void createBodyParts() { /* Noop */ }

  // Short hand.
  @Override public void setBody(String mimeType, String body) {
    assert bodyParts.isEmpty() : "Unexpected set body call to a multipart email";
    bodyParts.add(new BodyPart(mimeType, body));
  }

  // http://jira.codehaus.org/browse/JACKSON-739, can't have methods of same name.
  @Override public void setBodyBytes(String mimeType, byte[] body) {
    assert bodyParts.isEmpty() : "Unexpected set body call to a multipart email";
    bodyParts.add(new BodyPart(mimeType, body));
  }

  @Override public void setMimeType(String mimeType) {
    if (!mimeType.startsWith("multipart")) {
      throw new RuntimeException("Invalid root mimeType: " + mimeType);
    }
    this.rootMimeType = mimeType;
  }

  public String getRootMimeType() {
    return rootMimeType;
  }

  public String toString() {
    String gmailMsgId = status == null ? "N/a" : status.getGmailMsgId() + "";
    String threadMsgId = status == null ? "N/a" : status.getThreadId() + "";
    String from = status == null ? "N/a" : status.getFrom() + "";
    return "[Message uid=" + getImapUid() +
      " gmailid=" + gmailMsgId + " threadid=" + threadMsgId + " from="+ from + "]";
  }

  public static class BodyPart implements HasBodyParts, java.io.Serializable {
    private Multimap<String, String> headers = newListMultimap();

    private String mimeType;

    // This field is set for HTML or text emails. and is mutually exclusive with binBody.
    private String body;

    // This field is set for all binary attachment and body types.
    private byte[] binBody;

    private List<BodyPart> bodyParts;

    public BodyPart(String mimeType, String body) {
      this.mimeType = mimeType;
      this.body = body;
    }

    public BodyPart() {
    }

    public BodyPart(String mimeType, byte[] body) {
      this.mimeType = mimeType;
      this.binBody = body;
    }

    public List<BodyPart> getBodyParts() {
      return bodyParts;
    }

    @Override public void createBodyParts() {
      if (null == bodyParts)
        bodyParts = Lists.newArrayList();
    }

    public Multimap<String, String> getHeaders() {
      return headers;
    }

    public String getBody() {
      return body;
    }

    public String getMimeType() {
      return mimeType;
    }

    public void setMimeType(String mimeType) {
      this.mimeType = mimeType;
    }

    public void setBody(String mimeType, String body) {
      this.mimeType = mimeType;
      this.body = body;
    }

    public byte[] getBinBody() {
      return binBody;
    }

    public void setBodyBytes(String mimeType, byte[] binBody) {
      this.mimeType = mimeType;
      this.binBody = binBody;
    }
  }

  private static ListMultimap<String, String> newListMultimap() {
    return Multimaps.newListMultimap(
        Maps.<String, Collection<String>>newLinkedHashMap(), new ArrayListSupplier());
  }

  public static class ArrayListSupplier implements Supplier<List<String>>, java.io.Serializable {
    @Override public List<String> get() {
      return Lists.newArrayList();
    }
  }
}
