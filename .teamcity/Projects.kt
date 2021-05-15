import jetbrains.buildServer.configs.kotlin.v2019_2.Project

open class MainSubproject(vscRoot: LgsmRoots, baseProject: String? = null) : Project({
    id(generateId(IdType.Project, vscRoot.master))
    name = vscRoot.repoName
    buildType(BuildDockerImage(vscRoot.master))
    subProject(ProjDev(vscRoot.dev, baseProject))
})

open class ProjDev(vcsRoot: LgsmRoot, baseProject: String? = null) : Project({
    id(generateId(IdType.Project, vcsRoot))
    name = "dev"

    val build = BuildDockerImage(vcsRoot)

    buildType(build)
    buildType(PromoteToStable(vcsRoot, build))
})

object ProjBase : MainSubproject(VcsBase)
object ProjVHServer : MainSubproject(VcsVHServer, generateId(IdType.Build, VcsBase.master))
object ProjArkServer : MainSubproject(VcsARKServer, generateId(IdType.Build, VcsBase.master))

fun allProjects() =
    arrayListOf(
        ProjBase,
        ProjVHServer,
        ProjArkServer)