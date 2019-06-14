workflow "Deploy on Heroku" {
  on = "pull_request"
  resolves = ["debug"]
}

action "merged-filter" {
  uses = "actions/bin/filter@master"
  args = "merged true"
}

# Push to master
action "master-branch-filter" {
  needs = "merged-filter"
  uses = "actions/bin/filter@master"
  args = "branch master"
}

# temporary
action "debug" {
  needs = "master-branch-filter"
  uses = "actions/bin/debug@master"
}
