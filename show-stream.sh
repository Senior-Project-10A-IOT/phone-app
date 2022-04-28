#!/bin/bash
rtmpdump -v -r 'rtmp://gang-and-friends.com/live/stream' -o - | "vlc" -
