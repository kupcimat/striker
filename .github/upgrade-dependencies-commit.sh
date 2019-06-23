#!/bin/bash

set -e
set -x

git diff --quiet && exit 78 || true
git checkout -b upgrade-dependencies
git commit -am "Upgrade dependencies"
