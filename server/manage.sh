#!/bin/bash

if [ $# = 1 ]; then
    if [ $1 = "run" ]; then
        echo "Type your login and password to connect to database."
        echo "Login:"
        read login
        echo "Password:"
        read password

        mkdir -p config
        echo $login > config/database.config
        echo $password >> config/database.config

        mkdir -p log
        ./server/server.py >> log/server.log 2>> log/server.err &
    fi

    if [ $1 = "stop" ]; then
        pkill server.py
    fi

    if [ $1 = "clean" ]; then
        rm -rf config
        rm -rf log
        find -name __pycache__ -exec rm -rf {} +
    fi
else
    echo "Possible options:"
    echo "run"
    echo "stop"
    echo "clean"
fi
