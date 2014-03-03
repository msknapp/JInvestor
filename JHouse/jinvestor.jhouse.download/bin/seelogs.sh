#!/bin/bash
if [[ ! -d "logs" || "$(ls logs | wc -l)" < 1 ]]; then
	echo "There are no logs to see silly!"
	exit 1
fi
tail -f -n 500 logs/$(ls logs -lrot | tail -1 | gawk '{print($NF)}')