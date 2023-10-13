package com.koder.stock.coreservice.util;

import com.alibaba.fastjson.JSON;
import com.koder.stock.client.dto.StockQuotationDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
public class HttpUtil {

    public static String get(String url, String encode, int timeout) {
        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(url);
        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 配置信息
            RequestConfig requestConfig = RequestConfig.custom()
                    // 设置连接超时时间(单位毫秒)
                    .setConnectTimeout(timeout)
                    // 设置请求超时时间(单位毫秒)
                    .setConnectionRequestTimeout(timeout)
                    // socket读写超时时间(单位毫秒)
                    .setSocketTimeout(timeout)
                    // 设置是否允许重定向(默认为true)
                    .setRedirectsEnabled(true).build();

            // 将上面的配置信息 运用到这个Get请求里
            httpGet.setConfig(requestConfig);

            // 由客户端执行(发送)Get请求
            response = httpClient.execute(httpGet);

            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            log.info("响应状态为:{}", response.getStatusLine());
            if (responseEntity != null) {
                System.out.println("响应内容长度为:" + responseEntity.getContentLength());
                return EntityUtils.toString(responseEntity, Charset.forName(encode));
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 根据url下载文件，保存到filepath中
     *
     * @param url
     * @param encode
     * @return
     */
    public static String download(String url, String encode) {
        HttpClient client = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(url);
        HttpResponse response = null;
        try {
            response = client.execute(httpget);
        } catch (IOException e) {
            log.warn("http download connection error", e);
            return null;
        }
        if (response == null) {
            log.warn("http response is null, return empty");
            return null;
        }

        HttpEntity entity = response.getEntity();
        if (entity == null) {
            log.warn("http entity is null, return empty");
            return null;
        }
        InputStream is = null;
        if (encode == null) {
            encode = "UTF-8";
        }
        try {
            is = entity.getContent();
            StringWriter writer = new StringWriter();
            IOUtils.copy(is, writer, encode);
            String result = writer.toString();
            return result;
        } catch (IOException e) {
            log.warn("Http IO exception", e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                log.warn("Http IO exception", e);
            }
        }
        return null;
    }

    public static void main(String[] args) {
        String url = "http://quotes.money.163.com/service/chddata.html?code=0600121&start=20150901&end=20210120&fields=TCLOSE;HIGH;LOW;TOPEN;LCLOSE;CHG;PCHG;TURNOVER;VOTURNOVER;VATURNOVER;";
        String content = HttpUtil.download(url, "GBK");
        String contentArr [] = content.split("[\n]");

        List<StockQuotationDTO> quotations = Arrays.stream(contentArr).map(contentElement -> {
            String elementArr [] = contentElement.split(",");

            if(!elementArr[0].matches("\\d+\\-\\d+-\\d+")){
                return null;
            }

            if (new BigDecimal(elementArr[6]).compareTo(new BigDecimal("0"))==0) {
                return null;
            }

            StockQuotationDTO stockQuotationDTO = StockQuotationDTO.builder()
                    .quotationTime(DateUtil.getFromString(elementArr[0], DateUtil.DATE_FORMAT_YYYY_MM_DD))
                    .closingPrice(new BigDecimal(elementArr[3]))
                    .highestPrice(new BigDecimal(elementArr[4]))
                    .lowestPrice(new BigDecimal(elementArr[5]))
                    .openingPrice(new BigDecimal(elementArr[6]))
                    .lastClosingPrice(new BigDecimal(elementArr[7]))
                    .changeAmount(new BigDecimal(elementArr[8]))
                    .changeRange(new BigDecimal(elementArr[9]))
                    .turnOverRate(new BigDecimal(elementArr[10]))
                    .turnOverAmount(new BigDecimal(elementArr[11]))
                    .turnOverVolume(new BigDecimal(elementArr[12]))
                    .build();
            return stockQuotationDTO;
        }).filter(ele -> ele != null).collect(Collectors.toList());

        quotations.stream().forEach(quotation -> {
            System.out.println(JSON.toJSONString(quotation));
        });
    }


}