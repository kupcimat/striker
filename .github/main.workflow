workflow "Deploy on Heroku" {
  on = "push"
  resolves = ["verify-production"]
}

workflow "Upgrade dependencies" {
  on = "schedule(55 * * * *)"
  resolves = ["upgrade-dependencies-pr"]
}

# Push to master
action "master-branch-filter" {
  uses = "actions/bin/filter@master"
  args = "branch master"
}

# Skip duplicate push event
action "not-deleted-filter" {
  needs = "master-branch-filter"
  uses = "actions/bin/filter@master"
  args = "not deleted"
}

action "build-production" {
  needs = "not-deleted-filter"
  uses = "MrRamych/gradle-actions/openjdk-12@2.1"
  args = ["jib", "-Djib.to.auth.username=_", "-Djib.to.auth.password=$HEROKU_API_KEY"]
  secrets = ["HEROKU_API_KEY"]
}

action "release-production" {
  needs = "build-production"
  uses = "actions/heroku@master"
  args = ["container:release web", "--app=$HEROKU_APP"]
  secrets = ["HEROKU_API_KEY"]
  env = {
    HEROKU_APP = "striker-vn"
  }
}

action "health-check-production" {
  needs = "release-production"
  uses = "actions/bin/curl@master"
  args = ["https://${HEROKU_APP}.herokuapp.com/actuator/health", "|", "grep UP"]
  env = {
    HEROKU_APP = "striker-vn"
  }
}

action "verify-production" {
  needs = "health-check-production"
  uses = "MrRamych/gradle-actions/openjdk-12@2.1"
  args = ["test", "-DserverUrl=https://${HEROKU_APP}.herokuapp.com", "-Dusername=$TEST_USERNAME", "-Dpassword=$TEST_PASSWORD"]
  secrets = ["TEST_USERNAME", "TEST_PASSWORD"]
  env = {
    HEROKU_APP = "striker-vn"
  }
}

action "upgrade-dependencies-gradle" {
  uses = "MrRamych/gradle-actions/openjdk-12@2.1"
  args = ["upgradeDependencies"]
}

# Skip commit if there are no changes
action "upgrade-dependencies-commit" {
  needs = "upgrade-dependencies-gradle"
  uses = "actions/bin/sh@master"
  args = [
    "git diff --quiet && exit 78 || exit 0",
    "git checkout -b upgrade-dependencies",
    "git commit -am 'Upgrade dependencies'"
  ]
}

action "upgrade-dependencies-pr" {
  needs = "upgrade-dependencies-commit"
  uses = "elgohr/Github-Hub-Action@1.0"
  args = ["pull-request", "--no-edit", "--push", "--labels dependencies"]
  secrets = ["GITHUB_TOKEN"]
}
