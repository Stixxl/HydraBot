#!/usr/bin/env bash
for f in $(ls); do
    f_r="$(echo "$f" | sed 's/.mp3//g')"
    echo "${f_r}"
    ffmpeg -i "${f_r}.mp3" "${f_r}.wav"
done
