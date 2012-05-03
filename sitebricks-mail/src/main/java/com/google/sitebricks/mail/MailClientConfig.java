package com.google.sitebricks.mail;

import com.google.sitebricks.mail.Mail.Auth;
import com.google.sitebricks.mail.oauth.OAuthConfig;

/**
 * @author dhanji@gmail.com (Dhanji R. Prasanna)
 */
class MailClientConfig {
  private final String host;
  private final int port;
  private final Auth authType;
  private final String username;
  private final String password;
  private final long timeout;
  private final String nameId;
  private final String versionId;
  private final String vendorId;
  private final String contactId;
  private final OAuthConfig oAuthConfig;

  private final boolean gmail;

  public MailClientConfig(String host, int port, Auth authType, String username, String password,
                          long timeout, String nameId, String versionId, String vendorId, String contactId) {
    this.host = host;
    this.port = port;
    this.authType = authType;
    this.username = username;
    this.password = password;
    this.timeout = timeout;
    this.nameId = nameId;
    this.versionId = versionId;
    this.vendorId = vendorId;
    this.contactId = contactId;
    oAuthConfig = null;

    this.gmail = isGmail(host);
  }

  public MailClientConfig(String host,
                          int port,
                          String username,
                          OAuthConfig config,
                          long timeout,
                          String nameId, String versionId, String vendorId, String contactId) {
    this.host = host;
    this.port = port;
    this.authType = Auth.OAUTH;
    this.username = username;
    this.password = null;
    this.timeout = timeout;
    this.nameId = nameId;
    this.versionId = versionId;
    this.vendorId = vendorId;
    this.contactId = contactId;
    oAuthConfig = config;

    this.gmail = isGmail(host);
  }

  private static boolean isGmail(String host) {
    return host.contains("imap.gmail.com") || host.contains("imap.googlemail.com");
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  public Auth getAuthType() {
    return authType;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public long getTimeout() {
    return timeout;
  }

  public boolean useGmailExtensions() {
    return gmail;
  }

  public OAuthConfig getOAuthConfig() {
    return oAuthConfig;
  }

  public String getNameId() {
    return nameId;
  }

  public String getVersionId() {
    return versionId;
  }

  public String getVendorId() {
    return vendorId;
  }

  public String getContactId() {
    return contactId;
  }
}
