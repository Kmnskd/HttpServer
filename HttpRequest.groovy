package com.haier.safeCheck

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.apache.commons.codec.digest.DigestUtils;

class HttpRequest {
    private String GET = "GET";
    private String POST = "POST";
    private HashMap headers = ["Content-Type": "application/json"];
    private String gitlabToken = "xxx";
    private String url;

    // sourceCheck相关信息
    private String sourceCheck_CS_URL = "http://10.163.248.117:30000/sca/v1";
    private String Token = "BASIC-API:xxxx";
    private String projectUuid = "261877acbdd448d8b9983d020119375f";
    private String type = "1";
    private String protocol = "HTTPS";
    private String pullWay = "2";
    private String gitlab = "gitlab";
    private String gitlabApiVersion = "V4";
    private String gitLabHead = "https://git.net/";

    // codeSec相关信息
    private String codeSec_CS_URL = "http://xxx.xxx.xxx.xxx/cs/api/v2";
    private String X_CS_TIMESTAMP = System.currentTimeMillis();
    private String ACCESSKEY = "xxx";
    private String ACCESS_SECRET = "xxx";
    private String X_CS_NONCE = "123456";

    private String gitType = "1";
    private String authenticationMethod = "1";


    private static sortMap(HashMap data) {
//        def sorted = data.sort { a, b ->
//            a.value <=> b.value
//        }
        def mylist = [];
        for (i in data.sort()){
            def str = i.key + "=" + i.value
            mylist.add(str)
        }
        return mylist.join("&")
    }

    private getSignatureHeader(data="") {
        String validationstring = data + "&" + ACCESS_SECRET + "&" + X_CS_TIMESTAMP + "&" + X_CS_NONCE;
        return DigestUtils.sha256Hex(validationstring);
    }

    private static getPathValue(path) {
        def url_list = path.split("/");
        if (url_list.size() == 6) {
            if ("project".equals(url_list[1]) && "task".equals(url_list[3])) {
                return url_list[2] + "&" + url_list[4]
            }else if ("user".equals(url_list[1]) || "project".equals(url_list[1])){
                return url_list[2]
            }
        }
    }

    private getHeader(Platform, signature=""){
        if (Platform == "sourceCheck") {
            headers["Authorization"] = Token
        }else if (Platform == "codeSec") {
            headers["accessKey"] = ACCESSKEY
            headers["x-cs-timestamp"] = X_CS_TIMESTAMP
            headers["x-cs-nonce"] = X_CS_NONCE
            headers["x-cs-signature"] = signature
        }
    }

    private static getBody(HashMap<String, Object> data) {
        return JsonOutput.toJson(data)
    }

    private static dealResponse(response) {
        try {
            if (response.getResponseCode() == 200) {
                def json = new JsonSlurper()
                def res = json.parseText(response.content.text)
                return res
            }else {
                return JsonOutput.toJson(["status": "failed"])
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HttpURLConnection openConnect(uri) {
        def url = new URL(uri);
        def conn = (HttpURLConnection)url.openConnection();
        return conn
    }

    HttpURLConnection GetRequest(String uri, HashMap headers) {
        def conn = openConnect(uri)
        conn.setRequestMethod(GET);
        // 添加请求头
        headers.each { key, value ->
            conn.setRequestProperty(key, value)
        }
        return conn
    }

    HttpURLConnection postRequest(String uri, HashMap headers, bodyData) {
        def conn = openConnect(uri)
        conn.setRequestMethod(POST);
        headers.each { key, value ->
            conn.setRequestProperty(key, value)
        }
        conn.doOutput = true
        conn.outputStream
        def writer = new OutputStreamWriter(conn.outputStream)
        writer.write(bodyData)
        writer.flush()
        writer.close()
        return conn
    }

    def jenkinsGet(Platform, path) {
        if (Platform == "sourceCheck") {
            getHeader(Platform)
            url = sourceCheck_CS_URL + path
        }else if (Platform == "codeSec") {
            def signature = getSignatureHeader()
            getHeader(Platform, signature)
            url = codeSec_CS_URL + path
        }
        def conn = GetRequest(url, headers)
        return dealResponse(conn)
    }

    def jenkinsPost(Platform, path, body=[:]) {
        def signature;
        if (Platform == "sourceCheck") {
            body.put("projectUuid", projectUuid);
            body.put("type", type);
            body.put("protocol", protocol);
            body.put("pullWay", pullWay);
            body.put("gitType", gitlab);
            body.put("gitlabApiVersion", gitlabApiVersion);
            body.put("accessToken", gitlabToken);
            body.put("gitLabHead", gitLabHead);
            getHeader(Platform)
            url = sourceCheck_CS_URL + path
        }else if (Platform == "codeSec") {
            if(body) {
                body.put("gitType", gitType);
                body.put("token", gitlabToken);
                body.put("authenticationMethod", authenticationMethod);
                signature = getSignatureHeader(sortMap(body))
            }else {
                signature = getSignatureHeader(getPathValue(path))
            }
            getHeader(Platform, signature)
            url = codeSec_CS_URL + path
        }
        def bodyData = getBody(body)
        def conn = postRequest(url, headers, bodyData)
        return dealResponse(conn)
    }

}


