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

package io.github.azagniotov.stubby4j.server.ssl;

import static io.github.azagniotov.stubby4j.server.ssl.SslUtils.keyStoreAsX509Certificates;

import io.github.azagniotov.stubby4j.annotations.GeneratedCodeClassCoverageExclusion;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Set;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * This class is a wrapper around default X.509 trust manager initialized through {@link TrustManagerFactory} with the
 * default trust store and with added validation for self-signed certificates, including the default one of stubby4j's,
 * thus the "extended" part.
 */
@GeneratedCodeClassCoverageExclusion
public final class DefaultExtendedX509TrustManager implements X509TrustManager {

    private static final Set<X509Certificate> STUBBY_SELF_SIGNED_CERT;
    private static final X509TrustManager DEFAULT_TRUST_MANAGER;

    static {
        STUBBY_SELF_SIGNED_CERT = keyStoreAsX509Certificates();
        DEFAULT_TRUST_MANAGER = loadDefaultX509TrustManager();
    }

    public DefaultExtendedX509TrustManager() {}

    private static X509TrustManager loadDefaultX509TrustManager() {
        try {
            final TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            // Using null here initialises the TrustManagerFactory with the default trust store.
            trustManagerFactory.init((KeyStore) null);

            for (TrustManager trustManager : trustManagerFactory.getTrustManagers()) {
                if (trustManager instanceof X509TrustManager) {
                    return (X509TrustManager) trustManager;
                }
            }

            throw new RuntimeException("No TrustManager instance of type X509TrustManager found");
        } catch (Exception e) {
            throw new Error("Could not init default X509TrustManager", e);
        }
    }

    @Override
    public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        DEFAULT_TRUST_MANAGER.checkClientTrusted(chain, authType);
    }

    @Override
    public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        if (!isSelfSignedCertificate(chain)) {
            DEFAULT_TRUST_MANAGER.checkServerTrusted(chain, authType);
        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return DEFAULT_TRUST_MANAGER.getAcceptedIssuers();
    }

    private boolean isSelfSignedCertificate(final X509Certificate[] chain) {
        // self-signed certificates have X.509 certificate chains of size 1
        if (chain.length == 1) {
            final X509Certificate x509Certificate = chain[0];
            final CustomHostnameVerifier customHostnameVerifier = new CustomHostnameVerifier(x509Certificate);
            return STUBBY_SELF_SIGNED_CERT.contains(x509Certificate)
                    || customHostnameVerifier.isX500PrincipalNameLocalhost()
                    || customHostnameVerifier.isSubjectAltNamesContain("localhost")
                    || customHostnameVerifier.isSubjectAltNamesContain("127.0.0.1")
                    || customHostnameVerifier.isSubjectAltNamesContain("::1")
                    || customHostnameVerifier.isSubjectAltNamesContainPrivateIp();
        }

        return false;
    }
}
