import common.CIDefinitions

// This script must be processed before all other DSL scripts that might use
// these folders

CIDefinitions.definitions.each { ciName, ciOptions ->
  folder(ciName) {
    displayName ciName
  }
}
