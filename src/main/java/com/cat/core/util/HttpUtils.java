package com.cat.core.util;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BiConsumer;

import static com.cat.core.util.HttpUtils.ResponseCallback.SIMPLE_RESPONSE_CALLBACK;
import static java.util.stream.Collectors.toList;

//TODO
@SuppressWarnings({"unused", "WeakerAccess"})
@Slf4j
public abstract class HttpUtils {

    @FunctionalInterface
    private interface ClientCallback<T> {

        T callback(CloseableHttpClient client);

        default T callback() {
            try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
                return callback(httpclient);
            } catch (Exception e) {
                error(e);
                return null;
            }
        }
    }

    @FunctionalInterface
    interface ResponseCallback<T> {

        T callback(HttpResponse response);

        default T callback(CloseableHttpClient client, HttpUriRequest uriRequest) {
            try (CloseableHttpResponse response = client.execute(uriRequest)) {
                return this.callback(response);
            } catch (Exception e) {
                error(e);
                return null;
            }
        }

        ResponseCallback<String> SIMPLE_RESPONSE_CALLBACK = ResponseCallback::parseStringFromResponse;

        static String parseStringFromResponse(HttpResponse response) {
            try {
                return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                error(e);
                return null;
            }
        }
    }

    /**/
    public static String get(String uri, Collection<Header> headers, Collection<NameValuePair> params) {
        return ((ClientCallback<String>) client -> {
            try {
                URIBuilder builder = new URIBuilder(uri);
                Optional.ofNullable(params).ifPresent(ps -> ps.forEach(p -> builder.setParameter(p.getName(), p.getValue())));

                HttpGet httpGet = new HttpGet(builder.build());
                Optional.ofNullable(headers).ifPresent(hs -> hs.forEach(h -> setHeader(httpGet, h)));

                return SIMPLE_RESPONSE_CALLBACK.callback(client, httpGet);
            } catch (URISyntaxException e) {
                error(e);
            }
            return null;
        }).callback();
    }

    public static void main(String[] args) {
        System.out.println(get("http://www.baidu.com", null, null));
    }

    public static String get(String uri, Map<String, Object> params) {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            URIBuilder builder = new URIBuilder(uri);
            Optional.ofNullable(params).ifPresent(ps -> ps.forEach((k, v) -> builder.setParameter(k, v.toString())));

            try (CloseableHttpResponse response = httpclient.execute(new HttpGet(builder.build()))) {
                return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            error(e);
            return null;
        }
    }

    public static String get(String uri) {
        return get(uri, null);
    }

    public static String postForm(String uri, Map<String, Object> map) {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(uri);

            List<BasicNameValuePair> params = map.entrySet().stream().map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue().toString())).collect(toList());
            httpPost.setEntity(new UrlEncodedFormEntity(params));

            try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
                return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static String post(String uri, String data) {
        return post(uri, Collections.singletonList(new BasicHeader("Content-Type", "application/json")), data);
    }

    public static String post(String uri, Collection<Header> headers, String data) {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(uri);
            headers.forEach(httpPost::setHeader);
            httpPost.setEntity(new StringEntity(data));

            try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
                return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            return null;
        }
    }

    private static void error(Throwable t) {
        logger.error(t.getMessage(), t);
    }

    public static Header from(@NonNull String name, @NonNull String value) {
        return new BasicHeader(name, value);
    }

    public static Header from(@NonNull Map.Entry<String, String> entry) {
        return from(entry.getKey(), entry.getValue());
    }

    public static Header from(@NonNull NameValuePair pair) {
        return from(pair.getName(), pair.getValue());
    }

    private static void setHeader(HttpMessage httpMessage, Header header) {
        Optional.ofNullable(header).ifPresent(httpMessage::setHeader);
    }

    public static void setHeader(HttpMessage httpMessage, @NonNull String name, @NonNull String value) {
        httpMessage.setHeader(name, value);
    }

    public static void setHeader(HttpMessage httpMessage, NameValuePair pair) {
        Optional.ofNullable(pair).map(HttpUtils::from).ifPresent(header -> setHeader(httpMessage, header));
    }

    private static void setParameter(URIBuilder builder, NameValuePair pair) {
        builder.setParameter(pair.getName(), pair.getValue());
    }

    private static final BiConsumer<URIBuilder, NameValuePair> SET_PARAMETER_CONSUMER = (b, p) -> b.setParameter(p.getName(), p.getValue());

}
