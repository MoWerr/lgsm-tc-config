import jetbrains.buildServer.configs.kotlin.v2019_2.Project

open class MainSubproject(vscRoot: LgsmRoots) : Project({
    name = vscRoot.repoName
    buildType(BuildDockerImage(vscRoot.master))
    subProject(ProjDev(vscRoot.dev))
})

open class ProjDev(vcsRoot: LgsmRoot) : Project({
    id(generateId("dev_proj", vcsRoot))
    name = "dev"

    val build = BuildDockerImage(vcsRoot)

    buildType(build)
    buildType(PromoteToStable(vcsRoot, build))
})

object ProjBase : MainSubproject(VcsBase)
object ProjVHServer : MainSubproject(VcsVHServer)
object ProjArkServer : MainSubproject(VcsARKServer)

fun allProjects() =
    arrayListOf(
        ProjBase,
        ProjVHServer,
        ProjArkServer)