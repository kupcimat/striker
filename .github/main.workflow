workflow "Deploy on Heroku" {
  on = "push"
  resolves = ["debug"]
}

# Push to master
action "master-branch-filter" {
  uses = "actions/bin/filter@master"
  args = "branch master"
}

# temporary
action "debug" {
  needs = "master-branch-filter"
  uses = "actions/bin/debug@master"
}
