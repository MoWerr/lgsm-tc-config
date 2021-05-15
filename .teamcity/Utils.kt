import jetbrains.buildServer.configs.kotlin.v10.toExtId

enum class BranchType { Master, Dev }
enum class IdType { Vcs, Build, Promote, Project }

fun getBranchName(branchType: BranchType) = branchType.name.toLowerCase()
fun getDockerTag(branchType: BranchType) = when(branchType) {
    BranchType.Master -> "latest"
    BranchType.Dev -> "dev"
}

fun getIdTypeName(idType: IdType) = idType.name.toLowerCase()

fun generateId(idType: IdType, repoName: String, branchName: String) = "${getIdTypeName(idType)}${repoName}${branchName}".toExtId()
fun generateId(idType: IdType, vcsRoot: LgsmRoot): String = generateId(idType, vcsRoot.repoName, getBranchName(vcsRoot.branchType))


