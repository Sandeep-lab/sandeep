#!/bin/groovy

library 'kibana-pipeline-library@kibana-ci-stats-support'
kibanaLibrary.load()

kibanaPipeline(timeoutMinutes: 135, checkPrChanges: true) {
  ciStats.trackBuild {
    githubPr.withDefaultPrComments {
      catchError {
        retryable.enable()
        parallel([
          'kibana-intake-agent': workers.intake('kibana-intake', './test/scripts/jenkins_unit.sh'),
        ])
      }
    }

    retryable.printFlakyFailures()
    kibanaPipeline.sendMail()
  }
}
