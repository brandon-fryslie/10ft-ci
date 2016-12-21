package common

// CI Systems are defined in a map as shown in the example map
//
// Default values are specified in the defaults map
class CIDefinitions {
  static Object example = [
    ci_system_name: [
      // This is the repo containing the compose file & Dockerfile for your CI system
      // Required
      repo: 'Organization/repo',

      // This is the path to the CI system Docker compose file
      composePath: 'path/to/compose/file.yml',

      // The location of your Dockerfile relative to the root directory of your repo
      dockerfilePath: 'path/to/dockerfile',

      // The Docker host on which to deploy the CI system
      // Default: 'bld-magnumci-01.f4tech.com:2375'
      dockerHost: 'docker_host:2375',

      // This will add additonal deploy types to your job
      // All jobs have 'primary' and 'data' types
      additonalDeployTypes: ['my_additional_deploy_types'],

      // Specifying these options will create a job that will pull
      // Docker images to a host
      imageDeployOptions: [

        // Host to which the job will pull your images
        // Default: inherit from main config
        dockerHost: 'docker_host:2375',

        // Images to pull
        // Required if imageDeployOptions is specified
        images: ['jenkins/my_image', 'jenkins/my_other_image'],
      ],
    ]
  ]

  static Object defaults = [
    composePath: 'ci/deploy/docker-compose.yml',
    dockerfilePath: 'ci/master/docker',
    dockerHost: 'bld-magnumci-01.f4tech.com:2375',
  ]

  static Object definitions = [
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
        dockerfilePath: 'ci/jenkins/docker',
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
}
