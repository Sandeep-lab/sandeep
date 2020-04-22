/**
  Wraps a job to trigger the tracking of build metrics via the kibana-ci-stats api
*/
def trackBuild(closure) {
  catchErrors {
    def responseRaw = httpRequest([
      method: "POST",
      url: "https://ci-stats.kibana.dev/build",
      data: toJSON([
        jenkinsJobName: "foo",
        jenkinsJobId: "bar",
        branch: "master",
        commitSha: "abc123"
      ])
    ])

    def response = toJSON(responseRaw)
    def buildId = response.id;

    withEnv([
      "KIBANA_CI_STATS_BUILD_ID=${buildId}"
    ], {
      try {
        closure()
      } finally {
        catchErrors {
          httpRequest([
            method: "POST",
            url: "https://ci-stats.kibana.dev/build/_complete?buildId=${buildId}",
          ])
        }
      }
    })
  }
}
