package com.google.sitebricks.mail.oauth;

import com.google.common.base.Preconditions;

/**
 * @author dhanji@gmail.com (Dhanji R. Prasanna)
 */
public class OAuthConfig {
  public volatile String email;
  public volatile String accessToken;
  public volatile String refreshToken;

  public OAuthConfig(String email, String accessToken, String refreshToken) {
    Preconditions.checkArgument(email != null && !email.isEmpty());
    Preconditions.checkArgument(accessToken != null && !accessToken.isEmpty());
    Preconditions.checkArgument(refreshToken != null && !refreshToken.isEmpty());

    this.email = email;
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
  }
}
