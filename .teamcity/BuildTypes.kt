import jetbrains.buildServer.configs.kotlin.v2019_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2019_2.FailureAction
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.merge
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.dockerCommand
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

open class BuildDockerImage(vcsRoot: LgsmRoot) : BuildType({
    val buildTag = getDockerTag(vcsRoot.branchType)
    id (generateId("build", vcsRoot))

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

open class PromoteToStable(vcsRoot: LgsmRoot, dependency: BuildType) : BuildType({
    id(generateId("promote", vcsRoot))
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