import jetbrains.buildServer.configs.kotlin.v10.toExtId
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

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
    val rootId = "lgsm_${repoName}_${branchName}"
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

project {
    subProject(BaseProj)
    subProject(VHServerProj)
    subProject(ARKServerProj)

    subProjectsOrder = arrayListOf(
        RelativeId("base"),
        RelativeId("vhserver"),
        RelativeId("arkserver"))
}

object BaseProj : Project({
    name = "base"
})

object VHServerProj : Project({
    name = "vhserver"
})

object ARKServerProj : Project({
    name = "arkserver"
})