import jetbrains.buildServer.configs.kotlin.v10.toExtId

enum class BranchType { Master, Dev }

fun getBranchName(branchType: BranchType) = branchType.name.toLowerCase()
fun getDockerTag(branchType: BranchType) = when(branchType) {
    BranchType.Master -> "latest"
    BranchType.Dev -> "dev"
}

fun generateId(type: String, repoName: String, branchName: String) = "${type}${repoName}${branchName}".toExtId()
fun generateId(type: String, vcsRoot: LgsmRoot): String = generateId(type, vcsRoot.repoName, getBranchName(vcsRoot.branchType))


