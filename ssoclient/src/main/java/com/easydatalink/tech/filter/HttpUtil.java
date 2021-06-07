package com.easydatalink.tech.filter;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;

/**
 * Http调用处理类
 *
 * @author Terry
 */
@Slf4j
public class HttpUtil {

    public static final String TEXT_TYPE = "text/plain";
    public static final String JSON_TYPE = "application/json";
    public static final String XML_TYPE = "text/xml";
    public static final String HTML_TYPE = "text/html";
    public static final String JS_TYPE = "text/javascript";
    public static final String EXCEL_TYPE = "application/vnd.ms-excel";
    public static final String STREAM_TYPE = "application/octet-stream";

    private static final int REQ_TIMEOUT = 6 * 1000;
    public static final int SLEEP_TIME = 1 * 1000;
    public static final int IAS_SUCCESS = 400;

    public static HttpClient httpsTrustClient() {
        try {
            // 在调用SSL之前需要重写验证方法，取消检测SSL
            X509TrustManager trustManager = new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] xcs, String str) {}

                @Override
                public void checkServerTrusted(X509Certificate[] xcs, String str) {}
            };
            SSLContext ctx = SSLContext.getInstance(SSLConnectionSocketFactory.TLS);
            ctx.init(null, new TrustManager[] {trustManager}, null);
            SSLConnectionSocketFactory socketFactory =
                new SSLConnectionSocketFactory(ctx, NoopHostnameVerifier.INSTANCE);
            // 创建Registry
            RequestConfig requestConfig =
                RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD_STRICT).setExpectContinueEnabled(Boolean.TRUE)
                    .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
                    .setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC)).build();
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE).register("https", socketFactory).build();
            // 创建ConnectionManager，添加Connection配置信息
            PoolingHttpClientConnectionManager connectionManager =
                new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            CloseableHttpClient closeableHttpClient = HttpClients.custom().setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig).build();
            return closeableHttpClient;
        } catch (KeyManagementException ex) {
            throw new RuntimeException(ex);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 获取 HttpClient
     * 
     * @param host
     * @param path
     * @return
     */
    public static HttpClient wrapClient(String path) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        if (path != null && path.startsWith("https://")) {
            return httpsTrustClient();
        }
        return httpClient;
    }

    /**
     * 设置http超时时间
     * 
     * @param requestConfig
     * @return
     */
    private static RequestConfig setTimeOutConfig(RequestConfig requestConfig) {
        if (requestConfig == null) {
            requestConfig = RequestConfig.DEFAULT;
        }
        return RequestConfig.copy(requestConfig).setConnectionRequestTimeout(900000).setConnectTimeout(900000)
            .setSocketTimeout(900000).build();
    }

    public static String getRequest(String url) {
        HttpPost httpRequest = new HttpPost(url);
        httpRequest.setHeader("Connection", "close");
        httpRequest.setConfig(setTimeOutConfig(httpRequest.getConfig()));
        try {
            HttpClient httpClient = wrapClient(url);
            HttpResponse response = httpClient.execute(httpRequest);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String content = EntityUtils.toString(entity);
                EntityUtils.consume(entity);
                return content;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpRequest.abort();
        }
        return "";

    }

    public static String getRequest(String url, JSONObject params) {
        HttpPost httpRequest = new HttpPost(url);
        httpRequest.setHeader("Connection", "close");
        httpRequest.setConfig(setTimeOutConfig(httpRequest.getConfig()));
        List<NameValuePair> map = new ArrayList<NameValuePair>();
        for (String key : params.keySet()) {
            map.add(new BasicNameValuePair(key, params.getString(key)));
        }
        try {
            httpRequest.setEntity(new UrlEncodedFormEntity(map, Consts.UTF_8));
            HttpClient httpClient = wrapClient(url);
            HttpResponse response = httpClient.execute(httpRequest);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String content = EntityUtils.toString(entity);
                EntityUtils.consume(entity);
                return content;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpRequest.abort();
        }
        return "";

    }

    /**
     * 请求参数在request内获取方式
     * 
     * @param url
     * @param params
     * @param contentType
     * @return
     */
    public static String postRequest(String url, JSONObject params, String contentType) {
        HttpPost post = new HttpPost(url);
        post.setHeader("Connection", "close");
        post.setConfig(setTimeOutConfig(post.getConfig()));
        if (StringUtils.isNotBlank(contentType)) {
            post.setHeader("Content-Type", contentType);
        }
        HttpResponse response = null;
        try {
            post.setEntity(new StringEntity(params.toJSONString(), Consts.UTF_8));
            HttpClient httpClient = wrapClient(url);
            response = httpClient.execute(post);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String content = EntityUtils.toString(entity, Consts.UTF_8);
                EntityUtils.consume(entity);
                return content;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            post.abort();
        }
        return "";
    }

    public static String postRequest(String url, String params, String contentType) {
        HttpPost post = new HttpPost(url);
        post.setHeader("Connection", "close");
        post.setConfig(setTimeOutConfig(post.getConfig()));
        if (StringUtils.isNotBlank(contentType)) {
            post.setHeader("Content-Type", contentType);
        }
        HttpResponse response = null;
        try {
            post.setEntity(new StringEntity(params, Consts.UTF_8));
            HttpClient httpClient = wrapClient(url);
            response = httpClient.execute(post);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String content = EntityUtils.toString(entity, Consts.UTF_8);
                EntityUtils.consume(entity);
                return content;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            post.abort();
        }
        return "";
    }

    public static String putRequest(String url, JSONObject json, String contentType) {
        HttpPut put = new HttpPut(url);
        put.setHeader("Connection", "close");
        put.setConfig(setTimeOutConfig(put.getConfig()));
        if (StringUtils.isNotBlank(contentType)) {
            put.setHeader("Content-Type", contentType);
        }
        HttpResponse response = null;
        try {
            put.setEntity(new StringEntity(json.toJSONString(), Consts.UTF_8));
            HttpClient httpClient = wrapClient(url);
            response = httpClient.execute(put);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String content = EntityUtils.toString(entity, Consts.UTF_8);
                EntityUtils.consume(entity);
                return content;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            put.abort();
        }
        return "";
    }

    public static String postRequest(String url, String contentType, byte[] str) {
        HttpPost post = new HttpPost(url);
        post.setHeader("Connection", "close");
        post.setConfig(setTimeOutConfig(post.getConfig()));
        if (StringUtils.isNotBlank(contentType)) {
            post.setHeader("Content-Type", contentType);
        }
        try {
            post.setEntity(new StringEntity(new String(str, Consts.UTF_8)));
            HttpClient httpClient = wrapClient(url);
            HttpResponse response = httpClient.execute(post);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String content = EntityUtils.toString(entity);
                EntityUtils.consume(entity);
                return content;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            post.abort();
        }
        return "";
    }

    public static String postXmlRequest(String url, String xml, String contentType, Map<String, String> header) {
        HttpPost post = new HttpPost(url);
        post.setHeader("Connection", "close");
        post.setConfig(setTimeOutConfig(post.getConfig()));
        if (StringUtils.isNotBlank(contentType)) {
            post.setHeader("Content-Type", contentType);
        }
        if (null != header && !header.isEmpty()) {
            Iterator<String> it = header.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                post.setHeader(key, header.get(key));
            }
        }
        try {
            post.setEntity(new StringEntity(xml, Consts.UTF_8));
            HttpClient httpClient = wrapClient(url);
            HttpResponse response = httpClient.execute(post);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String content = EntityUtils.toString(entity);
                EntityUtils.consume(entity);
                return content;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            post.abort();
        }

        return "";
    }

    /**
     * HTTP Post 获取内容
     *
     * @param url
     *            请求的url地址 ?之前的地址
     * @param params
     *            请求的参数
     * @param charset
     *            编码格式
     * @return 页面内容
     */
    public static String doPost(String url, Map<String, String> params, String charset) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        try {
            List<NameValuePair> pairs = null;
            if (params != null && !params.isEmpty()) {
                pairs = new ArrayList<NameValuePair>(params.size());
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String value = entry.getValue();
                    if (value != null) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), value));
                    }
                }
            }
            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(setTimeOutConfig(httpPost.getConfig()));
            if (pairs != null && pairs.size() > 0) {
                httpPost.setEntity(new UrlEncodedFormEntity(pairs, Consts.UTF_8));
            }
            HttpClient httpClient = wrapClient(url);
            HttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpPost.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, Consts.UTF_8);
            }
            EntityUtils.consume(entity);
            httpPost.abort();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String sendPost(String url, Map<String, Object> params) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        try {
            List<NameValuePair> pairs = null;
            if (params != null && !params.isEmpty()) {
                pairs = new ArrayList<NameValuePair>(params.size());
                for (String key : params.keySet()) {
                    pairs.add(new BasicNameValuePair(key, params.get(key).toString()));
                }
            }
            HttpPost httpPost = new HttpPost(url);
            if (pairs != null && pairs.size() > 0) {
                httpPost.setEntity(new UrlEncodedFormEntity(pairs, "utf-8"));
            }
            HttpClient httpClient = wrapClient(url);
            HttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpPost.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, Consts.UTF_8);
            }
            EntityUtils.consume(entity);
            httpPost.abort();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * get 请求
     * 
     * @param url
     * @param header
     * @param params
     * @return
     */
    public static String getRequest(String url, Map<String, String> header, Map<String, String> params) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        String result = "";

        StringBuilder urlStr = new StringBuilder();
        urlStr.append(url).append("?");

        int i = params.size();
        for (Map.Entry<String, String> map : params.entrySet()) {
            urlStr.append(map.getKey()).append("=").append(map.getValue());
            if ((--i) == 0) {
                continue;
            }
            urlStr.append("&");
        }
        try {
            httpClient = (CloseableHttpClient)wrapClient(url);
            HttpGet httpGet = new HttpGet(urlStr.toString());
            if (null != header && !header.isEmpty()) {
                Iterator<String> it = header.keySet().iterator();
                while (it.hasNext()) {
                    String key = it.next();
                    httpGet.setHeader(key, header.get(key));
                }
            }

            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(20000)
                .setConnectionRequestTimeout(20000).setSocketTimeout(40000).build();
            httpGet.setConfig(requestConfig);
            response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity);
            httpGet.abort();
            EntityUtils.consume(entity);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != response) {
                try {
                    httpClient.close();
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * HTTP Post 获取内容
     *
     * @param url
     *            请求的url地址 ?之前的地址
     * @param params
     *            请求的参数
     * @param charset
     *            编码格式
     * @return 页面内容
     */
    public static String doPost(String url, Map<String, String> header, String params, String contentType) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Connection", "close");
            httpPost.setConfig(setTimeOutConfig(httpPost.getConfig()));
            if (StringUtils.isNotBlank(contentType)) {
                httpPost.setHeader("Content-Type", contentType);
            }
            if (null != header && !header.isEmpty()) {
                Iterator<String> it = header.keySet().iterator();
                while (it.hasNext()) {
                    String key = it.next();
                    httpPost.setHeader(key, header.get(key));
                }
            }
            if (null != params || "".equals(params)) {
                httpPost.setEntity(new StringEntity(params, Consts.UTF_8));
            }
            HttpClient httpClient = wrapClient(url);
            HttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpPost.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, Consts.UTF_8);
            }
            EntityUtils.consume(entity);
            httpPost.abort();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * HTTP Post 参数在方法体内例如：a(String a,String b,..)
     *
     * @param url
     *            请求的url地址 ?之前的地址
     * @param params
     *            请求的参数
     * @param charset
     *            编码格式
     * @return 页面内容 抛出异常信息
     */
    public static Map<String, String> postMapRequest(String url, Map<String, String> params) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        String postResult;
        Map<String, String> result = new HashMap<String, String>();
        try {
            postResult = setPost(url, params);
            if (StringUtils.isNotEmpty(postResult)) {
                return null;
            }
            JSONObject jsonObject = JSONObject.parseObject(postResult);
            Set<String> keySet = jsonObject.keySet();
            // 对Set集合遍历
            for (String key : keySet) {
                result.put(key, jsonObject.getString(key));
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String setPost(String url, Map<String, String> params) throws Exception {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        List<NameValuePair> pairs = null;
        if (params != null && !params.isEmpty()) {
            pairs = new ArrayList<NameValuePair>(params.size());
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String value = entry.getValue();
                if (value != null) {
                    pairs.add(new BasicNameValuePair(entry.getKey(), value));
                }
            }
        }
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Connection", "close");
        httpPost.setConfig(setTimeOutConfig(httpPost.getConfig()));
        if (pairs != null && pairs.size() > 0) {
            httpPost.setEntity(new UrlEncodedFormEntity(pairs, Consts.UTF_8));
        }
        HttpResponse response = null;
        try {
            HttpClient httpClient = wrapClient(url);
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpPost.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, Consts.UTF_8);
            }
            EntityUtils.consume(entity);
            httpPost.abort();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            httpPost.abort();
        } finally {
            httpPost.abort();
        }
        return null;
    }

    /**
     * JSON方式调用
     *
     * @param url
     * @param params
     * @return
     */
    public static String postJsonRequest(String url, JSONObject params) {
        return postRequest(url, params, JSON_TYPE);
    }

    /**
     * XML方式调用
     *
     * @param url
     * @param params
     * @return
     */
    public static String postXmlRequest(String url, String xml, Map<String, String> header) {
        return postXmlRequest(url, xml, XML_TYPE, header);
    }

    /**
     * 二进制流方式传递
     * 
     * @param url
     * @param bytes
     * @param contentType
     * @return
     */
    public static String postBytes(String url, byte[] bytes, String contentType) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Connection", "close");
        httpPost.setConfig(setTimeOutConfig(httpPost.getConfig()));
        httpPost.setEntity(new ByteArrayEntity(bytes));
        if (contentType != null && !"".equals(contentType))
            httpPost.setHeader("Content-type", contentType);
        else
            httpPost.setHeader("Content-type", STREAM_TYPE);

        HttpResponse response = null;
        try {
            HttpClient httpClient = wrapClient(url);
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpPost.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, Consts.UTF_8);
            }
            EntityUtils.consume(entity);
            httpPost.abort();
            return result;
        } catch (Exception e) {
            httpPost.abort();
        } finally {
            httpPost.abort();
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    public static String sendPostWithJson(Map<String, Object> params, String url, String sign) {
        String result = "";
        Long start = System.currentTimeMillis();
        DefaultHttpClient client = new DefaultHttpClient();
        // 设置连接时间
        client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, HttpUtil.REQ_TIMEOUT);
        // 设置数据传输时间
        client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, REQ_TIMEOUT);

        HttpPost post = new HttpPost(url);
        post.setHeader("Content-type", "application/json;charset=UTF-8");
        post.addHeader("sign", sign + "");
        post.getParams().setParameter("http.protocol.content-charset", "UTF-8");
        // 处理415异常
        post.setHeader("Accept", "application/json");

        try {
            StringEntity entity = new StringEntity(JSON.toJSONString(params), "UTF-8");
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json;charset=UTF-8");// 发送json数据需要设置contentType
            post.setEntity(entity);

            HttpResponse httpResponse = client.execute(post);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 返回json格式：
                result = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.close();
        }
        return result;
    }

    @SuppressWarnings("deprecation")
    public static String sendAuthorizationPostWithJson(String url, String token, Map<String, Object> params,
        String sign) {
        log.info("====> Post请求的地址:" + url + "\n====> 参数:" + JSON.toJSONString(params));
        String result = "";
        Long start = System.currentTimeMillis();
        DefaultHttpClient client = new DefaultHttpClient();
        // 设置连接时间
        client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, HttpUtil.REQ_TIMEOUT);
        // 设置数据传输时间
        client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, REQ_TIMEOUT);

        HttpPost post = new HttpPost(url);
        post.setHeader("Content-type", "application/json;charset=UTF-8");
        if (!StringUtils.isEmpty(token)) {
            post.addHeader("Authorization", token);
        }
        post.addHeader("sign", sign + "");
        post.getParams().setParameter("http.protocol.content-charset", "UTF-8");
        // 处理415异常
        post.setHeader("Accept", "application/json");
        try {
            StringEntity entity = new StringEntity(JSON.toJSONString(params), "UTF-8");
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json;charset=UTF-8");// 发送json数据需要设置contentType
            post.setEntity(entity);

            HttpResponse httpResponse = client.execute(post);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 返回json格式：
                result = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
            } else {
                log.info("====> post请求失败：" + httpResponse.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            log.error("====> post请求异常", e);
            e.printStackTrace();
        } finally {
            client.close();
        }
        Long end = System.currentTimeMillis();
        float excTime = (float)(end - start) / 1000;
        log.info("====> 外部服务调用结束,耗时" + excTime + "秒," + url + "请求的调用结果为:" + result);
        return result;
    }

    /**
     * 无参get请求
     * 
     * @Description
     * @param url
     * @return
     * @author gys
     */
    public static String doGet(String url) {
        HttpGet httpRequest = new HttpGet(url);
        httpRequest.setHeader("Connection", "close");
        httpRequest.setConfig(setTimeOutConfig(httpRequest.getConfig()));
        String result = "";
        try {
            HttpClient httpClient = wrapClient(url);
            HttpResponse response = httpClient.execute(httpRequest);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity);
                EntityUtils.consume(entity);
            }
        } catch (Exception e) {
            log.error("doGet请求异常:", e);
        } finally {
            httpRequest.abort();
        }
        return result;
    }

    public static String sendPost4Sso(String url) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        try {
            HttpPost httpPost = new HttpPost(url);
            HttpClient httpClient = wrapClient(url);
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, Consts.UTF_8);
            }
            EntityUtils.consume(entity);
            httpPost.abort();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) throws ClientProtocolException, IOException {
        String url =
            "http://127.0.0.1:8086/sso/oauth/check_token?token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2MjE5NDk2NTksInVzZXJfbmFtZSI6ImFkbWluIiwianRpIjoiNGQ1OTM3YjctYWUxZC00MzczLWJmMGQtZTMxYTY0MjRlZTNjIiwiY2xpZW50X2lkIjoiZGVjbGFyZSIsInNjb3BlIjpbInJlYWQiLCJ3cml0ZSJdfQ.YwzAlAYJ0xWtzMYRJQb6sgShiyQNKej0cpGRGwGbG9g";
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(setTimeOutConfig(httpPost.getConfig()));
        HttpClient httpClient = wrapClient(url);
        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        String result = null;
        if (entity != null) {
            result = EntityUtils.toString(entity, Consts.UTF_8);
        }
        EntityUtils.consume(entity);
        httpPost.abort();
    }

}
