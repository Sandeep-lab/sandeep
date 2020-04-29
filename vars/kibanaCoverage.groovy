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

def uploadWithVault(vaultSecret, prefix, x) {
  withGcpServiceAccount.fromVaultSecret(vaultSecret, 'value') {
    sh """
        gsutil -m cp -r -a public-read -z js,css,html,txt ${x} '${prefix}'
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
  kibanaPipeline.bash('''

    set -x

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

    collectPrevious() {
      echo "$(git log --pretty=format:%h -1)" > previous.txt
    }
    collectPrevious

    set +x

    ''', title
  )
}

def downloadPrevious(title) {
  withGcpServiceAccount.fromVaultSecret(vaultPath(), 'value') {
    kibanaPipeline.bash('''

    set -x

    gsutil -m cp -r gs://elastic-bekitzur-kibana-coverage-live/previous_pointer/previous.txt .
    mv previous.txt downloaded_previous.txt
    cat downloaded_previous.txt

    set +x

    ''', title)
  }
}

def uploadPrevious(title) {
  withGcpServiceAccount.fromVaultSecret(vaultPath(), 'value') {
    kibanaPipeline.bash('''

    set -x

    gsutil cp previous.txt s://elastic-bekitzur-kibana-coverage-live/previous_pointer/

    set +x

    ''', title)

  }
}


return this
