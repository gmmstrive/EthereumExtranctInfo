package com.gikee.ethereum.utils;

import com.alibaba.fastjson.JSON;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class JsonRpc {

    @Autowired
    private DateTransform dateTransform;

    @Autowired
    private OkHttpClient okHttpClient;

    @Value(value = "${ethereum.rpcUrl}")
    private String rpcUrl;

    private static final String JSONRPC = "jsonrpc";
    private static final String JSONRPCNUMBER = "2.0";
    private static final String METHOD = "method";
    private static final String PARAMS = "params";
    private static final String ID = "id";

    /**
     * Rpc Json 消息体
     *
     * @param method
     * @param params
     * @return
     */
    public String generateJsonRpc(String method, Object[] params) {
        Map<String, Object> map = new HashMap<>();
        map.put(JSONRPC, JSONRPCNUMBER);
        map.put(METHOD, method);
        map.put(PARAMS, params);
        map.put(ID, dateTransform.getTimeStamp().toString());
        return JSON.toJSONString(map);
    }

    /**
     * 发送 Rpc 消息
     *
     * @param method
     * @param params
     * @return
     */
    public String sendJsonRpcMessage(String method, Object[] params) {
        RequestBody requestBody;
        Request request;
        Response response = null;
        try {
            requestBody = RequestBody.create(HttpService.JSON_MEDIA_TYPE, generateJsonRpc(method, params));
            request = new Request.Builder().url(rpcUrl).post(requestBody).build();
            response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return null;
    }

}
