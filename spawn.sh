#!/bin/bash

until "$@"; do
    echo "Process crashed with exit code $?.  Respawning.." >&2
    sleep 2
done
