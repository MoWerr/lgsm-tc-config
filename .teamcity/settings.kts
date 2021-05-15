import jetbrains.buildServer.configs.kotlin.v10.toExtId
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.dockerCommand
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2020.2"

fun generateId(type: String, repoName: String, branchName: String): String {
    val id = "${type}${repoName}${branchName}"
    return id.toExtId()
}

fun generateId(type: String, vcsRoot: LgsmRoot): String = generateId(type, vcsRoot.repoName, vcsRoot.branchName)

open class LgsmRoot(repoName: String, branchName: String) : GitVcsRoot({
    val rootId = generateId("vcs", repoName, branchName)
    id(rootId.toExtId())

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
{
    val repoName: String = repoName
    val branchName : String = branchName
}


open class LgsmRoots(repoName: String) {
    val master = LgsmRoot(repoName, "master")
    val dev = LgsmRoot(repoName, "dev")

    fun register(proj: Project)
    {
        proj.vcsRoot(master)
        proj.vcsRoot(dev)
    }
}
/*
object VscBase : LgsmRoots("lgsm-base")
object VscVHServer : LgsmRoots("vhserver")
object VscARKServer : LgsmRoots("arkserver")
*/


project {
    /*allVscs().forEach {
        it.register(this)
    }
*/
    subProject(BaseProj)
    subProject(VHServerProj)
    subProject(ARKServerProj)

    subProjectsOrder = arrayListOf(
        RelativeId("BaseProj"),
        RelativeId("VHServerProj"),
        RelativeId("ARKServerProj"))
}

object BaseProj : Project({
    name = "base"

    //buildType(BuildDockerImage(VscBase.master, "latest"))
    //subProject(DevProj("master", BaseDevRoot))
})

object VHServerProj : Project({
    name = "vhserver"

    //buildType(BuildDockerImage(VscVHServer.master, "latest"))
    //subProject(DevProj("main", VHServerDevRoot))
})

object ARKServerProj : Project({
    name = "arkserver"

    //buildType(BuildDockerImage(VscARKServer.master, "latest"))
    //subProject(DevProj("master", ARKServerDevRoot))
})

open class BuildDockerImage(vcsRoot: LgsmRoot, buildTag: String) : BuildType({
    val id: String = "build_lgsm_${vcsRoot.repoName}_${vcsRoot.branchName}";
    id (id.toExtId())

    name = "Build"

    vcs {
        this.root(vcsRoot)
    }

    steps {
        dockerCommand {
            name = "Build image"
            commandType = build {
                source = file {
                    path = "Dockerfile"
                }
                namesAndTags = "mowerr/${vcsRoot.repoName}:${buildTag}"
                commandArgs = "--pull"
            }
            param("dockerImage.platform", "linux")
        }

        dockerCommand {
            name = "Push image"
            commandType = push {
                namesAndTags = "mowerr/${vcsRoot.repoName}:${buildTag}"
            }
        }
    }

    triggers {
        vcs {
            branchFilter = "+:<default>"
        }
    }
})
/*
open class DevProj(mainBranch: String, vcsRoot: LgsmRoot) : Project({
    val projId = "proj_dev_${vcsRoot.repoName}_${vcsRoot.branchName}"
    id (projId.toExtId())

    name = "dev"

    val build = BuildDockerImage(vcsRoot, "dev")

    buildType(build)
    buildType(PromoteToStable(vcsRoot, mainBranch, build))
})

open class PromoteToStable(vcsRoot: LgsmRoot, destBranch: String, dependency: BuildType) : BuildType({
    val promoteId = "promote_lgsm_${vcsRoot.repoName}_${vcsRoot.branchName}"
    id(promoteId.toExtId())

    name = "Promote to Stable"

    vcs {
        root(vcsRoot)
    }

    features {
        merge {
            branchFilter = "+:<default>"
            destinationBranch = destBranch
        }
    }

    dependencies {
        snapshot(dependency) {
            runOnSameAgent = true
            onDependencyFailure = FailureAction.FAIL_TO_START
        }
    }
})*/