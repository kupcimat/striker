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

action "verify-production" {
  needs = "release-production"
  uses = "actions/heroku@master"
  args = ["apps:info", "--app=$HEROKU_APP"]
  secrets = ["HEROKU_API_KEY"]
  env = {
    HEROKU_APP = "striker-vn"
  }
}
