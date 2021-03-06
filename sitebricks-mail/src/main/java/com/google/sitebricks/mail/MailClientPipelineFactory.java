package com.google.sitebricks.mail;

import com.google.sitebricks.mail.Mail.Auth;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.jboss.netty.handler.ssl.*;
import org.jboss.netty.util.HashedWheelTimer;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

/**
 * @author dhanji@gmail.com (Dhanji R. Prasanna)
 */
class MailClientPipelineFactory implements ChannelPipelineFactory {
  private final MailClientHandler mailClientHandler;
  private final MailClientConfig config;
  private final HashedWheelTimer timer = new HashedWheelTimer();
  private final SslBufferPool sslPool = new SslBufferPool();

  public MailClientPipelineFactory(MailClientHandler mailClientHandler, MailClientConfig config) {
    this.mailClientHandler = mailClientHandler;
    this.config = config;
  }

  public ChannelPipeline getPipeline() throws Exception {
    // Create a default pipeline implementation.
    ChannelPipeline pipeline = Channels.pipeline();

    if (config.getAuthType() != Auth.PLAIN) {
      SSLContext sslContext = SSLContext.getDefault();
      // Fix https://github.com/netty/netty/issues/832 with SSLContext
      sslContext.getServerSessionContext().setSessionCacheSize(1); // 1 cached session
      sslContext.getServerSessionContext().setSessionTimeout(60); // seconds
      SSLEngine sslEngine = sslContext.createSSLEngine();
      String[] enabledProtocols = {"SSLv3", "TLSv1"};
      sslEngine.setEnabledProtocols(enabledProtocols);
      sslEngine.setUseClientMode(true);
      SslHandler sslHandler = new SslHandler(sslEngine, sslPool, false, ImmediateExecutor.INSTANCE, timer, 10000);
      sslHandler.setEnableRenegotiation(true);
      // Explicitly allow netty to propogate ssl exceptions
      sslHandler.setCloseOnSSLException(true);

      pipeline.addLast("ssl", sslHandler);
    }

    pipeline.addLast("decoder", new StringDecoder());
    pipeline.addLast("encoder", new StringEncoder());

    // and then business logic.
    pipeline.addLast("handler", mailClientHandler);

    return pipeline;
  }
}
