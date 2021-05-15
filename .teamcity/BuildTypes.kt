import jetbrains.buildServer.configs.kotlin.v2019_2.AbsoluteId
import jetbrains.buildServer.configs.kotlin.v2019_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2019_2.FailureAction
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.merge
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.dockerCommand
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.finishBuildTrigger
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

open class BuildDockerImage(vcsRoot: LgsmRoot, baseProject: String? = null) : BuildType({
    val buildTag = getDockerTag(vcsRoot.branchType)
    id (generateId(IdType.Build, vcsRoot))

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

        if (baseProject != null) {
            finishBuildTrigger {
                buildType
                successfulOnly = true
            }
        }
    }

    if (baseProject != null) {
        dependencies {
            snapshot(AbsoluteId(baseProject)) {
                runOnSameAgent = true
                onDependencyFailure = FailureAction.FAIL_TO_START
            }
        }
    }
})

open class PromoteToStable(vcsRoot: LgsmRoot, dependency: BuildType) : BuildType({
    id(generateId(IdType.Promote, vcsRoot))
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