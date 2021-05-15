import jetbrains.buildServer.configs.kotlin.v10.toExtId

fun generateId(type: String, repoName: String, branchName: String) = "${type}${repoName}${branchName}".toExtId()
fun generateId(type: String, vcsRoot: Settings.LgsmRoot): String = generateId(type, vcsRoot.repoName, vcsRoot.branchName)
