#!/bin/bash
screen -S client -X quit
screen -S server -X quit
screen -S edit -X quit
bash util/update.sh
screen -dm -S server ./util/runserver.sh
screen -dm -S client ./util/runclient.sh
screen -dm -S edit
