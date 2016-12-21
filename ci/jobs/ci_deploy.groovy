javaposse.jobdsl.dsl.jobs.FreeStyleJob.metaClass.commonWrappers = {
  wrappers {
    timestamps()
    colorizeOutput 'xterm'
  }
}

// CI Systems are defined in a map as shown in the exampleDefinition map
//
// Default values are specified in the ciDefaults map below
//
def exampleDefinition = [
  ci_system_name: [
//  This is the repo containing the compose file & Dockerfile for your CI system
//  Required
    repo: 'Organization/repo',

//  This is the path to the CI system Docker compose file
    composePath: 'path/to/compose/file.yml',

//  The location of your Dockerfile relative to the root directory of your repo
    dockerfilePath: 'path/to/dockerfile',

//  The Docker host on which to deploy the CI system
//  Default: 'bld-magnumci-01.f4tech.com:2375'
    dockerHost: 'docker_host:2375',

//  This will add additonal deploy types to your job
//  All jobs have 'primary' and 'data' types
    additonalDeployTypes: ['my_additional_deploy_types'],

//  Specifying these options will create a job that will pull
//  Docker images to a host
    imageDeployOptions: [

//    Host to which the job will pull your images
//    Default: inherit from main config
      dockerHost: 'docker_host:2375',

//    Images to pull
//    Required if imageDeployOptions is specified
      images: ['jenkins/my_image', 'jenkins/my_other_image'],
    ],
  ]
]

def ciDefaults = [
  composePath: 'ci/deploy/docker-compose.yml',
  dockerfilePath: 'ci/master/docker',
  dockerHost: 'bld-magnumci-01.f4tech.com:2375',
]

def ciSystems = [
  almci: [
    repo: 'RallySoftware/alm-ci',
    dockerfilePath: 'master/docker',
    composePath: 'master/deploy/almci.yml',
    dockerHost: 'bld-almci-02.f4tech.com:2375',
    additonalDeployTypes: [
      'test',
      'test2',
      'onprem',
      'onprem_data'
    ],
  ],
  analysisci: [
    repo: 'RallySoftware/code-analysis',
    composePath: 'ci/master/docker-compose.yml',
    imageDeployOptions: [
      dockerHost: 'bld-docker-21.f4tech.com:4243',
      images: ['jenkins/sonar-runner', 'jenkins/docker-compose-node', 'jenkins/docker-builder-node']
    ]
  ],
  bespokeci: [
    repo: 'RallySoftware/bespoke',
    dockerfilePath: 'ci/jenkins/docker',
    imageDeployOptions: [
      images: ['jenkins/bespoke-node', 'jenkins/docker-builder-node']
    ],
  ],
  deepthoughtci: [
    repo: 'RallySoftware/deepthought',
    imageDeployOptions: [
      images: ['jenkins/deepthought-node', 'jenkins/armada-node']
    ],
  ],
  druidiaci: [
    repo: 'RallySoftware/druidia-ci',
    imageDeployOptions: [
      images: ['jenkins/druidiaci', 'jenkins/docker-builder-node']
    ],
  ],
  gopherci: [
    repo: 'RallySoftware/gopher',
    imageDeployOptions: [
      images: ['jenkins/gopher-node', 'jenkins/docker-builder-node']
    ],
  ],
  hydraci: [
    repo: 'RallySoftware/hydra',
    dockerfilePath: 'ci/jenkins',
    imageDeployOptions: [
      images: ['jenkins/hydra-node', 'jenkins/armada-node']
    ],
  ],
  kafkamanagerci: [
    repo: 'RallySoftware/kafka-manager', // note: this will build the wrong image tag
    dockerfilePath: 'ci/jenkins/docker',
    imageDeployOptions: [
      images: ['jenkins/kafkamanagerci-node', 'jenkins/docker-builder-node']
    ],
  ],
  megamaidci: [
    repo: 'RallySoftware/megamaid',
    dockerfilePath: 'ci/jenkins/docker',
    imageDeployOptions: [
      images: ['jenkins/megamaid-node', 'jenkins/docker-builder-node']
    ],
  ],
  orchestratorci: [
    repo: 'RallySoftware/orchestrator-ci',
    dockerfilePath: 'ci/jenkins/docker',
    imageDeployOptions: [
      images: ['jenkins/megamaid-node', 'jenkins/docker-builder-node']
    ],
  ],
  revisionsci: [
    repo: 'RallySoftware/revisions',
    dockerfilePath: 'ci/jenkins/docker',
    imageDeployOptions: [
      images: ['jenkins/revisionsci', 'jenkins/docker-builder-node']
    ],
  ],
  testnci: [
    repo: 'RallySoftware/alm-testn',
    dockerfilePath: 'ci/master/docker',
    additonalDeployTypes: ['test'],
    imageDeployOptions: [
      images: ['jenkins/docker-compose-node', 'jenkins/docker-builder-node', 'jenkins/nodejs-docker-builder-node']
    ],
  ],
  urroci: [
    repo: 'RallySoftware/urro-ci',
    composePath: 'deploy/docker-compose.yml',
    dockerHost: 'bld-almci-01.f4tech.com:2375',
    additonalDeployTypes: ['test'],
  ],
  toolsci: [
    repo: 'RallySoftware/tools-ci',
    composePath: 'deploy/deploy-jenkins.yml',
    dockerHost: 'bld-tools-01.f4tech.com:4243',
  ],
]

ciSystems.each { ciName, ciOptions ->
  folder(ciName) {
    displayName ciName
  }

  freeStyleJob("${ciName}/${ciName}-build") {
    description "Build the ${ciName} Docker image"
    logRotator(-1, 5)
    label 'utility-node'

    def dockerfilePath = ciOptions.dockerfilePath ?: ciDefaults.dockerfilePath

    // Use everything before the first slash, e.g. ci/master/docker -> ci
    def match = dockerfilePath =~ '^([^/]+)'
    if (!match) {
      println "Error!  Didn't match exclusion path from docker file path: #{dockerfilePath}"
    }

    def includedRegionsPath = match ? match[1] : ''

    scm {
      github(ciOptions.repo, 'master', 'ssh') { node ->
        node / 'extensions' / 'hudson.plugins.git.extensions.impl.PathRestriction' {
          includedRegions includedRegionsPath
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

    def dockerHost = ciOptions.dockerHost ?: ciDefaults.dockerHost
    def composePath = ciOptions.composePath ?: ciDefaults.composePath

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

  if (ciOptions.imageDeployOptions) {
    freeStyleJob("${ciName}/${ciName}-deploy-images") {
      description "Deploy the ${ciName} images"
      label 'utility-node'
      logRotator(-1, 10)
      commonWrappers()

      scm {
        github(ciOptions.repo, 'master', 'ssh')
      }

      steps {
        def dockerHost = ciOptions.imageDeployOptions.dockerHost ?: ciOptions.dockerHost

        shell """\
#!/bin/bash -el
export DOCKER_HOST=tcp://${dockerHost}
${ciOptions.imageDeployOptions.images.collect { image ->
  "docker pull docker.f4tech.com/${image}:latest"
}.join("\n")}
        """.stripIndent()
      }
    }

  }
}
