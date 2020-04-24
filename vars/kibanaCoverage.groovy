def liveSitePrefix() {
  return "gs://elastic-bekitzur-kibana-coverage-live/"
}

def liveSiteVaultSecret() {
  return "secret/gce/elastic-bekitzur/service-account/kibana"
}

def previousPrefix() {
  return "${liveSitePrefix()}previous_pointer/"
}

def uploadCoverageStaticData(timestamp) {
  def prefix = liveSitePrefix()
  def timeStamp = "${prefix}${timestamp}/"
  def previous = "${prefix}previous_pointer/"

  uploadList(previous, ['previous.txt'])
//  uploadList(timeStamp, ['VCS_INFO.txt'])
  uploadList(prefix, ['src/dev/code_coverage/www/index.html', 'src/dev/code_coverage/www/404.html'])
  uploadList(timeStamp, [
    'target/kibana-coverage/functional-combined',
    'target/kibana-coverage/jest-combined',
    'target/kibana-coverage/mocha-combined'
  ])
}

def downloadWithVault(vaultSecret, prefix, x) {
  withGcpServiceAccount.fromVaultSecret(vaultSecret, 'value') {
    sh """
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
    cat previous.txt

    currentSha() {
      git log --oneline | sed -n 1p | awk '{print \$1}'
    }

    echo \$(currentSha) > previous.txt

    echo "### CURRENT Sha, from 'previous.txt': ..."
    cat previous.txt



    """, title
  )
}

return this
