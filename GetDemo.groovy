package HttpServer;
import groovy.json.JsonOutput
import groovy.json.JsonSlurper


class GetDemo{
    // 静态方法
    static String getRequest() {
        def conn = new URL('http://xxx.xxx.xxx.xxx').openConnection()
        conn.setRequestMethod("GET")
        conn.setRequestProperty("Content-Type", "application/json")
        conn.setRequestProperty("Authorization", "BASIC-API:123456")
        return conn.content.text
    }

    static String getProjectList() {
        def result = new HttpRequest().getRequest("xxx", "/projects?pageIndex=1&pageSize=50")
        println("查询项目列表：");
        return result
    }

    String postRequest(url, projectName) {
        def data = JsonOutput.toJson([projectName: projectName])
        def conn = new URL(url).openConnection()
        conn.setRequestMethod("POST")
        conn.setRequestProperty("Content-Type", "application/json")
        conn.setRequestProperty("Authorization", "BASIC-API:xxx")
        println("data" + data)
        // 输出请求参数
        conn.doOutput = true
        conn.outputStream
        def writer = new OutputStreamWriter(conn.outputStream)
        writer.write(data)
        writer.flush()
        writer.close()
//        def json = new JsonSlurper()
//        return json.parseText(conn.content.text)
        return conn.content.text
    }

    void Respo() {
        def patchOrg = """
                {"projectName": "lk"}
            """
        def response = httpRequest contentType: 'APPLICATION_JSON',
                customHeaders: [
                        [name: 'Authorization', value: 'BASIC-API:xxx']
                ],
                httpMode: 'POST',
                requestBody: patchOrg,
                url: "http://xxx"
        println("Status: "+response.status)
        println("Content: "+response.content)
    }
}