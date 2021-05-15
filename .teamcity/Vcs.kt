import jetbrains.buildServer.configs.kotlin.v2019_2.Project
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

open class LgsmRoot(val repoName: String, val branchType: BranchType) : GitVcsRoot({
    val branchName = getBranchName(branchType)
    id(generateId(IdType.Vcs, repoName, branchName).relativeId)

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

open class LgsmRoots(val repoName: String) {
    val master = LgsmRoot(repoName, BranchType.Master)
    val dev = LgsmRoot(repoName, BranchType.Dev)
}

object VcsBase : LgsmRoots("lgsm-base")
object VcsVHServer : LgsmRoots("vhserver")
object VcsARKServer : LgsmRoots("arkserver")

fun allVscs() =
    arrayListOf(
        VcsBase,
        VcsVHServer,
        VcsARKServer)