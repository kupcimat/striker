workflow "Deploy on Heroku" {
  on = "push"
  resolves = ["verify-production"]
}

# Push to master
action "master-branch-filter" {
  uses = "actions/bin/filter@master"
  args = "branch master"
}

action "build-production" {
  needs = "master-branch-filter"
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