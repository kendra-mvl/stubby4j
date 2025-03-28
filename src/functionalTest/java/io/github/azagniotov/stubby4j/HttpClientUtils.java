/*
 * Copyright (c) 2012-2024 Alexander Zagniotov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.azagniotov.stubby4j;

import static java.util.Arrays.asList;

import io.github.azagniotov.stubby4j.server.ssl.SslUtils;
import java.net.ProxySelector;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.ssl.SSLContexts;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.http2.client.HTTP2Client;
import org.eclipse.jetty.http2.client.http.HttpClientTransportOverHTTP2;
import org.eclipse.jetty.util.ssl.SslContextFactory;

public final class HttpClientUtils {

    private HttpClientUtils() {}

    static CloseableHttpClient buildHttpClient(final String tlsVersion) throws Exception {
        return buildHttpClient(tlsVersion, buildSSLContextWithRemoteCertificateLoaded(tlsVersion));
    }

    private static CloseableHttpClient buildHttpClient(final String tlsVersion, final SSLContext sslContext)
            throws Exception {

        System.out.println("Running tests using TLS version: " + tlsVersion);

        SSLEngine engine = sslContext.createSSLEngine();
        engine.setEnabledProtocols(new String[] {tlsVersion});
        System.out.println("SSLEngine [client] enabled protocols: ");
        System.out.println(new HashSet<>(asList(engine.getEnabledProtocols())));

        final SSLConnectionSocketFactory sslSocketFactory =
                new SSLConnectionSocketFactory(sslContext, new DefaultHostnameVerifier());

        return HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setSocketTimeout(45000)
                        .setConnectTimeout(45000)
                        .build())
                // When .useSystemProperties(), the FakeX509TrustManager gets exercised
                // .useSystemProperties()
                .setSSLSocketFactory(sslSocketFactory)
                .setMaxConnTotal(200)
                .setMaxConnPerRoute(20)
                .setConnectionTimeToLive(-1, TimeUnit.MILLISECONDS)
                .setRoutePlanner(new SystemDefaultRoutePlanner(ProxySelector.getDefault()))
                .disableRedirectHandling()

                // In ProxyConfigWithStubsTest.shouldReturnProxiedRequestResponse_WhenStubsWereNotMatched():
                //
                // I had to set this header to avoid "Not in GZIP format java.util.zip.ZipException: Not in GZIP format"
                // error:
                // The 'null' overrides the default value "gzip", also I had to .disableContentCompression() on
                // WEB_CLIENT
                .disableContentCompression()
                .disableAutomaticRetries()
                .build();
    }

    static SSLContext buildSSLContextWithRemoteCertificateLoaded(final String tlsVersion) throws Exception {
        //
        // 1. Download and save the remote self-signed certificate from the stubby4j server with TLS at localhost:7443
        //    This opens an SSL connection to the specified hostname and port and prints the SSL certificate.
        // ---------------------------------------------------------------------------------
        // $ echo quit | openssl s_client -showcerts -servername localhost -connect "localhost":7443 > FILE_NAME.pem
        //
        //
        // 2. Optionally, you can perform verification using cURL. Note: the -k (or --insecure) option is NOT used
        // ---------------------------------------------------------------------------------
        // $ curl -X GET --cacert FILE_NAME.pem  --tls-max 1.1  https://localhost:7443/hello -v
        //
        //
        // 3. Finally, load the saved self-signed certificate to a keystore
        // ---------------------------------------------------------------------------------
        // $ keytool -import -trustcacerts -alias stubby4j -file FILE_NAME.pem -keystore FILE_NAME.jks
        //
        //
        // 4. Load the generated FILE_NAME.jks file into the trust store of SSLContext, which then can be
        //    used to create an SSL socket factory for your web client. The STUBBY_SELF_SIGNED_TRUST_STORE
        //    was created using the following code:
        //
        // https://github.com/azagniotov/stubby4j/blob/737f1f16650ce78a9a63f8f3e23c60ba2769cdb4/src/main/java/io/github/azagniotov/stubby4j/server/ssl/SslUtils.java#L168-L172
        // ---------------------------------------------------------------------------------
        return SSLContexts.custom()
                .setProtocol(tlsVersion)
                .loadTrustMaterial(SslUtils.SELF_SIGNED_CERTIFICATE_TRUST_STORE, null)
                .build();
    }

    static SslContextFactory jettyClientSslContextFactory(final String tlsProtocol) {
        final SslContextFactory sslContextFactory = new SslContextFactory.Client();

        sslContextFactory.setEndpointIdentificationAlgorithm("HTTPS");
        sslContextFactory.setProtocol(tlsProtocol);
        sslContextFactory.setTrustStore(SslUtils.SELF_SIGNED_CERTIFICATE_TRUST_STORE);

        return sslContextFactory;
    }

    static HttpClient jettyHttpClientOnHttp11() {
        return new HttpClient();
    }

    static HttpClient jettyHttpClientOnHttp11WithClientSsl(final String tlsProtocol) {
        return new HttpClient(jettyClientSslContextFactory(tlsProtocol));
    }

    static HttpClient jettyHttpClientOnHttp20() {
        final HTTP2Client http2Client = new HTTP2Client();

        final HttpClientTransportOverHTTP2 transport = new HttpClientTransportOverHTTP2(http2Client);

        final HttpClient httpClient = new HttpClient(transport);
        httpClient.setMaxConnectionsPerDestination(4);

        return httpClient;
    }

    static HttpClient jettyHttpClientOnHttp20WithClientSsl(final String tlsProtocol) {
        final SslContextFactory sslContextFactory = jettyClientSslContextFactory(tlsProtocol);

        final HTTP2Client http2Client = new HTTP2Client();
        http2Client.addBean(sslContextFactory);

        final HttpClientTransportOverHTTP2 transport = new HttpClientTransportOverHTTP2(http2Client);
        transport.setUseALPN(true);

        final HttpClient httpClient = new HttpClient(transport, sslContextFactory);
        httpClient.setMaxConnectionsPerDestination(4);

        return httpClient;
    }
}
