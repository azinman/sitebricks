package com.google.sitebricks.mail;

import com.google.common.base.Preconditions;
import com.google.sitebricks.mail.Mail.AuthBuilder;
import com.google.sitebricks.mail.oauth.OAuthConfig;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author dhanji@gmail.com (Dhanji R. Prasanna)
 */
class SitebricksMail implements Mail, AuthBuilder {
  private String host;
  private int port;

  // For ID command
  private String name, version, vendor, contactEmail;

  private long timeout;

  private ExecutorService bossPool;
  private ExecutorService workerPool;

  @Override
  public AuthBuilder clientOf(String host, int port) {
    Preconditions.checkArgument(null != host && !host.isEmpty(),
        "Must specify a valid hostname");
    Preconditions.checkArgument(port > 0,
        "Must specify a valid (non-zero) port");
    this.host = host;
    this.port = port;
    return this;
  }

  @Override
  public AuthBuilder id(String name, String version, String vendor, String contactEmail) {
    Preconditions.checkArgument(name != null, "name cannot be null!");
    Preconditions.checkArgument(version != null, "version cannot be null!");
    Preconditions.checkArgument(vendor != null, "vendor cannot be null!");
    Preconditions.checkArgument(contactEmail != null, "contactEmail cannot be null!");
    this.name = name;
    this.version = version;
    this.vendor = vendor;
    this.contactEmail = contactEmail;
    return this;
  }

  @Override
  public AuthBuilder timeout(long amount, TimeUnit unit) {
    this.timeout = unit.convert(amount, TimeUnit.MILLISECONDS);
    return this;
  }

  @Override
  public AuthBuilder executors(ExecutorService bossPool, ExecutorService workerPool) {
    Preconditions.checkArgument(bossPool != null, "Boss executor cannot be null!");
    Preconditions.checkArgument(workerPool != null, "Worker executor cannot be null!");
    this.bossPool = bossPool;
    this.workerPool = workerPool;
    return this;
  }

  @Override
  public MailClient prepare(Auth authType, String username, String password) {
    Preconditions.checkArgument(authType != Auth.OAUTH, "Pleause use prepareOAuth() instead.");
    if (null == bossPool) {
      bossPool = Executors.newCachedThreadPool();
      workerPool = Executors.newCachedThreadPool();
    }

    MailClientConfig config = new MailClientConfig(host, port, authType, username, password,
        timeout, name, version, vendor, contactEmail);

    return new NettyImapClient(config, bossPool, workerPool);
  }

  @Override
  public MailClient prepareOAuth(String username, OAuthConfig config) {
    if (null == bossPool) {
      bossPool = Executors.newCachedThreadPool();
      workerPool = Executors.newCachedThreadPool();
    }

    return new NettyImapClient(
        new MailClientConfig(host, port, username, config, timeout, name, version, vendor, contactEmail),
        bossPool, workerPool);
  }
}
