import jetbrains.buildServer.configs.kotlin.v10.toExtId
import jetbrains.buildServer.configs.kotlin.v2019_2.AbsoluteId
import jetbrains.buildServer.configs.kotlin.v2019_2.RelativeId

enum class BranchType { Master, Dev }
enum class IdType { Vcs, Build, Promote, Project }

fun getBranchName(branchType: BranchType) = branchType.name.toLowerCase()
fun getDockerTag(branchType: BranchType) = when(branchType) {
    BranchType.Master -> "latest"
    BranchType.Dev -> "dev"
}

fun getIdTypeName(idType: IdType) = idType.name.toLowerCase()

fun generateIdString(idType: IdType, repoName: String, branchName: String) = "${getIdTypeName(idType)}${repoName}${branchName}".toExtId()
fun generateId(idType: IdType, repoName: String, branchName: String) = RelativeId(generateIdString(idType, repoName, branchName))
fun generateId(idType: IdType, vcsRoot: LgsmRoot) = generateId(idType, vcsRoot.repoName, getBranchName(vcsRoot.branchType))

fun generateAbsoluteId(idType: IdType, vcsRoot: LgsmRoot) = AbsoluteId(generateId(idType, vcsRoot).value)

