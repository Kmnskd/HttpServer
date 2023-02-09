package HttpServer

import groovy.json.JsonOutput


class ssdlcApi {
    private HttpRequest httpRequest = new HttpRequest();
    private String url = "http://xxx.xxx.xxx.xxx/api/ssdlc/v2/projects/test/openapi/jenkins/scanProject";
    private HashMap headers = ["Content-Type": "application/json", "openapi": "xxxx"];
    private HashMap params_data = [:];
    private List toolType = ["codesec", "sca"];
    private int pipelineBuildNumber = (int)((Math.random()*9+1)*100000);

    def createProject(branch, gitUrl, commitId, JOB_NAME, BUILD_NUMBER) {
        params_data['pipelineCreator'] = 'xxx'
        params_data['pipelineBuildNumber'] = JOB_NAME
        params_data['pipelineUuid'] = BUILD_NUMBER
        params_data['branch'] = branch
        params_data['url'] = gitUrl
        params_data['commitId'] = commitId
        def res = []
        toolType.each {
            params_data['toolType'] = it
            def bodyData = JsonOutput.toJson(params_data)
            def conn = httpRequest.postRequest(url, headers, bodyData)
//            println(conn.content.text)
            res.add(conn.content.text)
        }
//        params_data['toolType'] = "sca"
//        def conn = httpRequest.postRequest(url, headers, bodyData)
        return res
    }

}