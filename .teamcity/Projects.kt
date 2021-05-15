import jetbrains.buildServer.configs.kotlin.v2019_2.AbsoluteId
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

open class MainSubproject(vscRoot: LgsmRoots, baseProject: AbsoluteId? = null) : Project({
    id(generateId(IdType.Project, vscRoot.master).relativeId)
    name = vscRoot.repoName
    buildType(BuildDockerImage(vscRoot.master))
    subProject(ProjDev(vscRoot.dev, baseProject))
})

open class ProjDev(vcsRoot: LgsmRoot, baseProject: AbsoluteId? = null) : Project({
    id(generateId(IdType.Project, vcsRoot).relativeId)
    name = "dev"

    val build = BuildDockerImage(vcsRoot, baseProject)

    buildType(build)
    buildType(PromoteToStable(vcsRoot, build))
})

object ProjBase : MainSubproject(VcsBase, AbsoluteId("UbuntuBase_Stable_Build"))
object ProjVHServer : MainSubproject(VcsVHServer, generateAbsoluteId(IdType.Build, VcsBase.master))
object ProjArkServer : MainSubproject(VcsARKServer, generateAbsoluteId(IdType.Build, VcsBase.master))

fun allProjects() =
    arrayListOf(
        ProjBase,
        ProjVHServer,
        ProjArkServer)