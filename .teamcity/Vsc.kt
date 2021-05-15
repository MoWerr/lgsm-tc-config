import jetbrains.buildServer.configs.kotlin.v2019_2.Project
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

open class LgsmRoot(val repoName: String, val branchName: String) : GitVcsRoot({
    id(generateId("vsc", repoName, branchName))

    name = "${repoName}_${branchName}"
    url = "https://github.com/MoWerr/${repoName}"
    branch = "refs/heads/${branchName}"
    branchSpec = "+:refs/heads/*"
    userForTags = "tc_mower"

    authMethod = password {
        userName = "MoWerr"
        password = "credentialsJSON:dd6f958d-e26e-4097-b397-6fa58ecba288"
    }
})

open class LgsmRoots(repoName: String) {
    val master = LgsmRoot(repoName, "master")
    val dev = LgsmRoot(repoName, "dev")

    fun register(proj: Project) {
        proj.vcsRoot(master)
        proj.vcsRoot(dev)
    }
}

object VscBase : LgsmRoots("base-lgsm")
object VscVHServer : LgsmRoots("vhserver")
object VscARKServer : LgsmRoots("arkserver")

fun allVscs() =
    arrayListOf(
        VscBase,
        VscVHServer,
        VscARKServer)