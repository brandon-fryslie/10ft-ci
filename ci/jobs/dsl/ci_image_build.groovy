// def definitions = evaluate(new File("../ci_definitions.groovy"))
//
// println definitions.inspect()
//
// ciSystems.each { ciName, ciOptions ->
//   if (ciOptions.imageDeployOptions) {
//     freeStyleJob("${ciName}/${ciName}-deploy-images") {
//       description "Deploy the ${ciName} images"
//       label 'utility-node'
//       logRotator(-1, 10)
//       commonWrappers()
//
//       scm {
//         github(ciOptions.repo, 'master', 'ssh')
//       }
//
//       steps {
//         def dockerHost = ciOptions.imageDeployOptions.dockerHost ?: ciOptions.dockerHost
//
//         shell """\
// #!/bin/bash -el
// export DOCKER_HOST=tcp://${dockerHost}
// ${ciOptions.imageDeployOptions.images.collect { image ->
//   "docker pull docker.f4tech.com/${image}:latest"
// }.join("\n")}
//         """.stripIndent()
//       }
//     }
//   }
// }
