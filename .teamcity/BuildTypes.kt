import jetbrains.buildServer.configs.kotlin.v10.toExtId
import jetbrains.buildServer.configs.kotlin.v2019_2.AbsoluteId
import jetbrains.buildServer.configs.kotlin.v2019_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2019_2.FailureAction
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.merge
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.dockerCommand
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.finishBuildTrigger
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

open class BuildDockerImage(vcsRoot: LgsmRoot, baseProject: AbsoluteId? = null, includeTrigger: Boolean = false) : BuildType({
    val buildTag = getDockerTag(vcsRoot.branchType)
    val buildAllTrigger = getBuildTrigger(vcsRoot.branchType)

    id(generateId(IdType.Build, vcsRoot).relativeId)

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

        finishBuildTrigger {
            buildType = buildAllTrigger.id?.value
        }

        if (includeTrigger && baseProject != null) {
            finishBuildTrigger {
                buildType = baseProject.absoluteId
                successfulOnly = true
            }
        }
    }

    dependencies {
        if (baseProject != null) {
            snapshot(baseProject) {
                runOnSameAgent = true
                onDependencyFailure = FailureAction.FAIL_TO_START
            }
        }
    }
})

open class PromoteToStable(vcsRoot: LgsmRoot, dependency: BuildType) : BuildType({
    id(generateId(IdType.Promote, vcsRoot).relativeId)
    name = "Promote to Stable"

    vcs {
        root(vcsRoot)
    }

    features {
        merge {
            branchFilter = "+:<default>"
            destinationBranch = "master"
        }
    }

    dependencies {
        snapshot(dependency) {
            runOnSameAgent = true
            onDependencyFailure = FailureAction.FAIL_TO_START
        }
    }
})

open class DummyBuild(projName: String, name: String) : BuildType({
    id("build_${name}".toExtId())
    this.name = name
})

object BuildAll : DummyBuild("lgsm", "Build All")
object BuildAllDev : DummyBuild("lgsm", "Build All Dev")

fun allRootBuilds() =
    arrayListOf(
        BuildAll,
        BuildAllDev
    )