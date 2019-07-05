#!/bin/bash

set -e
set -x

git diff --quiet && exit 78 || true
git config user.name "Dependencies Bot"
git config user.email "bot@striker.org"
git checkout -b upgrade-dependencies
git commit -am "Upgrade dependencies"
