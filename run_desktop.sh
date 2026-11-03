#!/bin/bash
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$DIR"

if [ -f "$HOME/.local/bin/env" ]; then
    . "$HOME/.local/bin/env"
fi

open -a Terminal "$DIR/run.sh" 2>/dev/null || java -jar "$DIR/target/skillshare-campus-1.0.0.jar"
