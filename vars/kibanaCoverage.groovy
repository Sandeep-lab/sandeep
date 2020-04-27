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

def liveSitePrefix() {
  return "gs://elastic-bekitzur-kibana-coverage-live/"
}

def liveSiteVaultSecret() {
  return "secret/gce/elastic-bekitzur/service-account/kibana"
}

def previousPrefix() {
  return "${liveSitePrefix()}previous_pointer/"
}

def uploadCoverageStaticData(timestamp, previousFilePath) {
  def prefix = liveSitePrefix()

  uploadPrevious(previousFilePath, "${prefix}previous_pointer/previous.txt")

  uploadList(prefix, ['src/dev/code_coverage/www/index.html', 'src/dev/code_coverage/www/404.html'])

  uploadList("${prefix}${timestamp}/", [
    'target/kibana-coverage/functional-combined',
    'target/kibana-coverage/jest-combined',
    'target/kibana-coverage/mocha-combined'
  ])
}

def downloadWithVault(vaultSecret, prefix, x) {
  withGcpServiceAccount.fromVaultSecret(vaultSecret, 'value') {
    sh """
        echo "### List Dir"
        ls -la .
        echo "### download prefix:"
        echo '${prefix}'
        echo "### download x:"
        echo '${x}'
        gsutil -m cp -r '${prefix}' '${x}'
      """
  }
}

def download(prefix, x) {
  downloadWithVault(liveSiteVaultSecret(), prefix, x)
}

def downloadList(prefix, xs) {
  xs.each { x ->
    download(prefix, x)
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
  withGcpServiceAccount.fromVaultSecret(liveSiteVaultSecret(), 'value') {

//    TODO: Quick hack to try to delete some stuff, undo!
    sh """
        gsutil rm -r "${liveSitePrefix()}previous_pointer/previous.txt/"
        gsutil rm -r "${liveSitePrefix()}jobs/"
        gsutil rm "${liveSitePrefix()}index.html"
      """

    sh """
        gsutil -m cp -r -a public-read -z js,css,html,txt '${src}' '${dest}'
      """

  }
}

def upload(prefix, x) {
  uploadWithVault(liveSiteVaultSecret(), prefix, x)
}

def uploadList(prefix, xs) {
  xs.each { x ->
    upload(prefix, x)
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
    cat previous.txt || true



    """, title
  )
}

return this
