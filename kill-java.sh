#!/bin/bash
kill $(ps aux | grep '[j]ava csp_build.py' | awk '{print $2}')
