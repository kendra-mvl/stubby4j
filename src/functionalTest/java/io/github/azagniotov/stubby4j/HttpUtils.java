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

import static io.github.azagniotov.stubby4j.HttpClientUtils.buildHttpClient;
import static io.github.azagniotov.stubby4j.server.ssl.SslUtils.TLS_v1_2;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.apache.v2.ApacheHttpTransport;
import io.github.azagniotov.stubby4j.utils.StringUtils;
import java.io.IOException;
import org.apache.http.impl.client.CloseableHttpClient;

final class HttpUtils {

    private static final HttpRequestFactory WEB_CLIENT;

    static {
        CloseableHttpClient apacheHttpClient = null;
        try {
            apacheHttpClient = buildHttpClient(TLS_v1_2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        WEB_CLIENT = new ApacheHttpTransport(apacheHttpClient, false).createRequestFactory(request -> {
            // This is dumb. Google client throws exception if response code is not 200
            request.setThrowExceptionOnExecuteError(false);
        });
    }

    private HttpUtils() {}

    static HttpRequest constructHttpRequest(final String method, final String targetUrl) throws IOException {
        return WEB_CLIENT.buildRequest(method, new GenericUrl(targetUrl), null);
    }

    static HttpRequest constructHttpRequest(final String method, final String targetUrl, final String content)
            throws IOException {
        return WEB_CLIENT.buildRequest(
                method, new GenericUrl(targetUrl), new ByteArrayContent(null, StringUtils.getBytesUtf8(content)));
    }
}
