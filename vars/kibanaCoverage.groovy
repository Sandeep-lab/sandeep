def bootMergeAndIngest(buildNum, buildUrl) {
  kibanaPipeline.bash("""
    source src/dev/ci_setup/setup_env.sh

    # bootstrap from x-pack folder
    cd x-pack
    yarn kbn bootstrap --prefer-offline

    # Return to project root
    cd ..

    . src/dev/code_coverage/shell_scripts/extract_archives.sh

    . src/dev/code_coverage/shell_scripts/fix_html_reports_parallel.sh

    . src/dev/code_coverage/shell_scripts/merge_jest_and_functional.sh

    . src/dev/code_coverage/shell_scripts/copy_mocha_reports.sh

    . src/dev/code_coverage/shell_scripts/ingest_coverage.sh ${buildNum} ${buildUrl}

  """, "### Bootstrap shell and kibana env, merge and ingest code coverage")
}

def gcpSite() {
  return "gs://elastic-bekitzur-kibana-coverage-live/"
}

def vaultPath() {
  return "secret/gce/elastic-bekitzur/service-account/kibana"
}

def uploadCoverageStaticData(timestamp) {
  def prefix = gcpSite()

  uploadPrevious("previous.txt", "${prefix}previous_pointer/previous.txt")

  uploadList(prefix, ['src/dev/code_coverage/www/index.html', 'src/dev/code_coverage/www/404.html'])

  uploadList("${prefix}${timestamp}/", [
    'target/kibana-coverage/functional-combined',
    'target/kibana-coverage/jest-combined',
    'target/kibana-coverage/mocha-combined'
  ])
}

def downloadPrevious() {
  def previousPath = 'previous_pointer'
  def storageLocation = "${gcpSite()}${previousPath}"

  withGcpServiceAccount.fromVaultSecret(vaultPath(), 'value') {
    sh "mkdir -p './${previousPath}' && gsutil -m cp -r '${storageLocation}.txt' './${previousPath}'"
  }
}

def uploadWithVault(vaultSecret, prefix, x) {
  withGcpServiceAccount.fromVaultSecret(vaultSecret, 'value') {
    sh """
        gsutil -m cp -r -a public-read -z js,css,html,txt ${x} '${prefix}'
      """
  }
}

def uploadPrevious(src, dest) {
  withGcpServiceAccount.fromVaultSecret(vaultPath(), 'value') {

    sh """
        gsutil cp -r -a public-read -z js,css,html,txt '${src}' '${dest}'
      """

  }
}

//def deleteStuff() {
//  //    TODO: Quick hack to try to delete some stuff, undo!
//  sh """
//        echo "### Trying to clear out gcp a little"
//        gsutil rm -r \"${gcpSite()}previous_pointer/previous.txt/\" || echo \"### Failed cleanup\"
//        gsutil rm -r \"${gcpSite()}jobs/\" || echo \"### Failed cleanup\"
//        gsutil rm \"${gcpSite()}index.html\" || echo \"### Failed cleanup\"
//      """
//}

def uploadList(prefix, xs) {
  xs.each { x ->
    uploadWithVault(vaultPath(), prefix, x)
  }
}

def collectVcsInfo(title) {
  kibanaPipeline.bash(
    '''

    predicate() {
      x=$1
      if [ -n "$x" ]; then
        return
      else
        echo "### 1 or more variables that Code Coverage needs, are undefined"
        exit 1
      fi
    }

    CMD="git log --pretty=format"

    XS=("${GIT_BRANCH}" \
        "$(${CMD}":%h" -1)" \
        "$(${CMD}":%an" -1)" \
        "$(${CMD}":%s" -1)")

    touch VCS_INFO.txt


    for X in "${!XS[@]}"; do
    {
      predicate "${XS[X]}"
      echo "${XS[X]}" >> VCS_INFO.txt
    }
    done

    ''', title
  )
}

def storePreviousSha(timestamp, title) {
  kibanaPipeline.bash(
    """

    echo "### timestamp: ${timestamp}"

    echo "### PREVIOUS Sha, from downloaded 'previous.txt': ..."
    cat previous.txt || true

    currentSha() {
      git log --oneline | sed -n 1p | awk '{print \$1}'
    }

    echo \$(currentSha) > previous.txt

    echo "### CURRENT Sha, from 'previous.txt': ..."
    cat previous.txt || echo "✖✖✖ previous.txt not found!"



    """, title
  )
}

return this
