package HttpServer;


class CodeSec {
    private HttpRequest httpRequest = new HttpRequest();
    private String Platform = "xxx";

//    static void main(String[] args) {
//        def s = new CodeSec();
////        s.getProjectList();
////        s.createProjectByGitInfo()
////        s.scanSubProject()
//        s.getListDetailByVulDataId()
//    }

    static startScanJob(repoName, gitUrl, branch) {
        def projectName = repoName + ":" + branch
        def scanResult;
        def c = new CodeSec();
        c.getProjectList()
        def createResult = c.createProjectByGitInfo(gitUrl, projectName, branch)
        if (createResult.status == true) {
            def projectUuid = createResult.data.projectUuid
            def appId = createResult.data.appId
            scanResult = c.scanSubProject(projectUuid, appId)
        }else {
            /*
             已存在的项目直接开启扫描，现在接口缺少已存在的项目的projectUuid和appId信息
             [status:false, message:项目创建失败,项目名已经存在, code:B400]
            */
            def projectUuid = createResult.data.projectUuid
            def appId = createResult.data.appId
            scanResult = c.scanSubProject(projectUuid, appId)
        }

        echo "${scanResult}"
    }


    /**
     * 查询项目列表
     * @return
     */
    def getProjectList() {
        def path = "/project/list";
        def result = httpRequest.jenkinsGet(Platform, path)
        println("查询项目列表：" + result);
        return result
    }


    /**
     * 根据git信息创建项目
     * @param gitUrl
     * @param projectName
     * @param branch
     * @return
     */
    def createProjectByGitInfo(gitUrl, projectName, branch="master") {
        def path = "/project/createProjectByGitInfo";
        def body = [:];
        body.put("projectName", projectName);
//        body.put("projectDesc", "使用接口发起对git仓库扫描");
        body.put("url", gitUrl);
        body.put("branch", branch);
        def result = httpRequest.jenkinsPost(Platform, path, body);
        println("根据git信息创建项目结果：" + result);
        return result
    }


    /**
     * 发起扫描任务
     * @param projectUuid
     * @param appId
     * @return
     */
    def scanSubProject(projectUuid, appId) {
        def path = "/project/${projectUuid}/task/${appId}/scanSubProject";
        def result = httpRequest.jenkinsPost(Platform, path);
        println("发起扫描任务结果：" + result);
        return result
    }


    /**
     * 分页查询漏洞列表（漏洞列表页面右侧表格）
     * @param projectUuid
     * @param appId
     * @return
     */
    def getListDetailByVulDataId(projectUuid, appId) {
        def path = "/project/${projectUuid}/task/${appId}/getListDetailByVulDataId";
        def result = httpRequest.jenkinsGet(Platform, path);
        println("分页查询漏洞列表：" + result);
        return result
    }

}
