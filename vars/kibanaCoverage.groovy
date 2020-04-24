
def uploadCoverageStaticData(timestamp) {
  def prefix = "gs://elastic-bekitzur-kibana-coverage-live/"
  def timeStamp = "${prefix}${timestamp}/"
  def previous = "${prefix}previous_pointer/"

  uploadList(previous, ['previous.txt'])
  uploadList(timeStamp, ['VCS_INFO.txt'])
  uploadList(prefix, ['src/dev/code_coverage/www/index.html', 'src/dev/code_coverage/www/404.html'])
  uploadList(timeStamp, [
    'target/kibana-coverage/functional-combined',
    'target/kibana-coverage/jest-combined',
    'target/kibana-coverage/mocha-combined'
  ])
}


def download(prefix, x) {
  def vaultSecret = 'secret/gce/elastic-bekitzur/service-account/kibana'

  downloadWithVault(vaultSecret, prefix, x)
}

def downloadWithVault(vaultSecret, prefix, x) {
  withGcpServiceAccount.fromVaultSecret(vaultSecret, 'value') {
    sh """
        gsutil -m cp -r -a public-read -z js,css,html,txt ${x} '${prefix}'
      """
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
  def vaultSecret = 'secret/gce/elastic-bekitzur/service-account/kibana'

  uploadWithVault(vaultSecret, prefix, x)
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

    git log --oneline | sed -n 2p | awk '{print $1}' > VCS_INFO.txt

    for X in "${!XS[@]}"; do
    {
      predicate "${XS[X]}"
      echo "${XS[X]}" >> VCS_INFO.txt
    }
    done

    ''', title
  )
}

def processPrevious(timestamp, title) {
  kibanaPipeline.bash(
    """

    echo "### NOT FULLY IMPLEMENTED YET"
    echo "### processPrevious timestamp: ${timestamp}"

    currentSha() {
      git log --oneline | sed -n 1p | awk '{print \$1}'
    }

    echo "### Current Sha: ..."
    currentSha
    echo \$(currentSha) > previous.txt


    echo "### Current Sha, from 'previous.txt': ..."
    cat previous.txt



    """, title
  )
}
