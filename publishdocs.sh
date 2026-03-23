#!/bin/bash

cd ./docs; ./adoc2md.sh; cd -

git add .
git commit -m "the docs"
git push
