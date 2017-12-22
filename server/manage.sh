#!/bin/bash

if [ $# = 1 ]; then
    if [ $1 = "run" ]; then
        echo "Building logic lib..."
        if make pylib -C utils/logic/; then
            echo "logic.so was built successfully"
            mkdir -p lib
            cp utils/logic/*.so lib/.
        else
            echo "Error when creating logic.so occurred"
            exit
        fi

        echo "Type your login and password to connect to database."
        echo "Login:"
        read login
        echo "Password:"
        stty -echo
        read password
        stty echo

        mkdir -p config
        echo $login > config/database.config
        echo $password >> config/database.config

        echo "Starting server..."
        mkdir -p log
        ./server/server.py 2> log/server.err &
    fi

    if [ $1 = "stop" ]; then
        pkill server.py
    fi

    if [ $1 = "clean" ]; then
        rm -rf config
        rm -rf log
        find . -name __pycache__ -exec rm -rf {} +
        find . -name .*.swp -exec rm -f {} +
        make clean -C utils/logic
        rm -rf lib
    fi
else
    echo "Possible options:"
    echo "run"
    echo "stop"
    echo "clean"
fi
