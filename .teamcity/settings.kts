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

open class LgsmRoot(repoName: String, branchName: String) : GitVcsRoot({
    val rootId = "vcs_lgsm_${repoName}_${branchName}"
    id(rootId.toExtId())

    name = branchName
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

object BaseMasterRoot : LgsmRoot("lgsm-base", "master")
object BaseDevRoot : LgsmRoot("lgsm-base", "dev")

object VHServerMasterRoot : LgsmRoot("vhserver", "main")
object VHServerDevRoot : LgsmRoot("vhserver", "dev")

object ARKServerMasterRoot : LgsmRoot("arkserver", "master")
object ARKServerDevRoot : LgsmRoot("arkserver", "dev")

project {
    vcsRoot(BaseMasterRoot)
    vcsRoot(BaseDevRoot)
    vcsRoot(VHServerMasterRoot)
    vcsRoot(VHServerDevRoot)
    vcsRoot(ARKServerMasterRoot)
    vcsRoot(ARKServerDevRoot)

    subProject(BaseProj)
    subProject(VHServerProj)
    subProject(ARKServerProj)

    subProjectsOrder = arrayListOf(
        RelativeId("BaseProj"),
        RelativeId("VHServerProj"),
        RelativeId("ARKServerProj")
    )
}

object BaseProj : Project({
    name = "base"

    buildType(BuildDockerImage(BaseMasterRoot, "latest"))
})

object VHServerProj : Project({
    name = "vhserver"

    buildType(BuildDockerImage(VHServerMasterRoot, "latest"))
})

object ARKServerProj : Project({
    name = "arkserver"

    buildType(BuildDockerImage(ARKServerMasterRoot, "latest"))
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