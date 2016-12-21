import common.CIDefinitions

javaposse.jobdsl.dsl.jobs.FreeStyleJob.metaClass.commonWrappers = {
  wrappers {
    timestamps()
    colorizeOutput 'xterm'
  }
}

CIDefinitions.definitions.each { ciName, ciOptions ->
  freeStyleJob("${ciName}/${ciName}-build") {
    description "Build the ${ciName} Docker image"
    logRotator(-1, 5)
    label 'utility-node'

    def dockerfilePath = ciOptions.dockerfilePath ?: CIDefinitions.defaults.dockerfilePath

    // Use everything before the first slash, e.g. ci/master/docker -> ci
    def match = dockerfilePath =~ '^([^/]+)'
    def pathRestriction = match ? match[1] : null

    scm {
      github(ciOptions.repo, 'master', 'ssh') { node ->
        if (pathRestriction) {
          node / 'extensions' / 'hudson.plugins.git.extensions.impl.PathRestriction' {
            includedRegions pathRestriction
          }
        }
      }
    }

    commonWrappers()

    parameters {
      stringParam('IMAGE_TAG', 'latest', 'Docker image tag to build')
      booleanParam('IGNORE_CACHE', false, 'Add --no-cache to Docker build command')
      booleanParam('PUSH_IMAGE', false, 'Push Docker image?')
    }

    steps {
      shell """\
        #!/bin/bash -el
        export DOCKER_HOST=tcp://bld-docker-20:4243

        [[ \$IGNORE_CACHE ]] && ignore_cache=--no-cache

        docker build \$ignore_cache -t docker.f4tech.com/jenkins/${ciName} ${dockerfilePath}
        [[ \$PUSH_IMAGE == true ]] && docker push docker.f4tech.com/jenkins/${ciName}
      """.stripIndent()
    }
  }
}
