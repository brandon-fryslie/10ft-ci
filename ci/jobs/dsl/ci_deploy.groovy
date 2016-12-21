import common.CIDefinitions

javaposse.jobdsl.dsl.jobs.FreeStyleJob.metaClass.commonWrappers = {
  wrappers {
    timestamps()
    colorizeOutput 'xterm'
  }
}

CIDefinitions.definitions.each { ciName, ciOptions ->
  freeStyleJob("${ciName}/${ciName}-deploy") {
    description "Deploy the ${ciName} Jenkins instance"
    label 'utility-node'
    logRotator(-1, 10)
    commonWrappers()

    def deployTypes = [
      '--pick one---',
      'primary',
      'data',
    ]

    if (ciOptions.additonalDeployTypes) {
      deployTypes += ciOptions.additonalDeployTypes
    }

    parameters {
      choiceParam('DEPLOY_TYPE', deployTypes)
      booleanParam('DATA_DEPLOY', false, "I really want to destroy existing ${ciName} data if I picked \"data\" for deploy type")
      stringParam('IMAGE_TAG', 'latest', 'Use this Docker image tag')
    }

    scm {
      github(ciOptions.repo, 'master', 'ssh')
    }

    def dockerHost = ciOptions.dockerHost ?: CIDefinitions.defaults.dockerHost
    def composePath = ciOptions.composePath ?: CIDefinitions.defaults.composePath

    steps {
      shell """\
        #!/bin/bash -el

        export DOCKER_HOST=tcp://${dockerHost}

        if [[ "\$DEPLOY_TYPE" == data ]]; then
          test "\$DATA_DEPLOY" == 'true' \
            && docker-compose -p ${ciName} -f ${composePath} pull \$DEPLOY_TYPE \
            && docker-compose -p ${ciName} -f ${composePath} up --no-deps -d \$DEPLOY_TYPE
        else
          docker-compose -p ${ciName} -f ${composePath} pull \$DEPLOY_TYPE
          docker-compose -p ${ciName} -f ${composePath} up --no-deps -d \$DEPLOY_TYPE
        fi
      """.stripIndent()
    }
  }
}
