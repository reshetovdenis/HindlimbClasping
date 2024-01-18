#!/bin/bash

# Define the directory containing the videos
VIDEOS_DIR="./videos"

# Loop through each .MOV file in the directory
for file in "$VIDEOS_DIR"/*.MOV; do
    # Skip if there are no .MOV files
    if [ ! -f "$file" ]; then
        continue
    fi

    # Create a new filename by replacing .MOV with .mp4
    new_file="${file%.MOV}.mp4"

    # Rename the file
    mv "$file" "$new_file"

    echo "Renamed $file to $new_file"
done
