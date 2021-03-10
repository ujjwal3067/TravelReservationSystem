#!/bin/bash 

#TODO: SPECIFY THE HOSTNAMES OF 4 CS MACHINES (lab2-8, lab2-10, etc...)
# MACHINES=(lab2-18, lab2-19, lab2-20, lab-21, lab2-22)

tmux kill-window -t 0
make clean && make

tmux new-session \; \
	split-window -h \; \
	split-window -v \; \
	split-window -v \; \
	select-layout main-vertical \; \
	select-pane -t 2 \; \
	send-keys "ssh -t lab2-18 \"cd $(pwd) > /dev/null; echo -n 'Connected to '; hostname; ./run_server.sh flightHost\"" C-m \; \
	select-pane -t 3 \; \
	send-keys "ssh -t lab2-19 \"cd $(pwd) > /dev/null; echo -n 'Connected to '; hostname; ./run_server.sh carHost\"" C-m \; \
	select-pane -t 4 \; \
	send-keys "ssh -t lab2-20 \"cd $(pwd) > /dev/null; echo -n 'Connected to '; hostname; ./run_server.sh roomHost\"" C-m \; \
	select-pane -t 1 \; \
	send-keys "ssh -t lab2-21 \"cd $(pwd) > /dev/null; echo -n 'Connected to '; hostname; sleep .5s; ./run_middleware.sh\"" C-m \; \
	split-window -v \; \
	send-keys "ssh -t lab2-22 \"cd $(pwd)/../Client > /dev/null; echo -n 'Connected to '; hostname; sleep .5s; make clean && make && ./run_client.sh lab2-21 middleware\"" C-m \;
