#!/bin/sh

set -eu

PROJECT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
CONTEXT_NAME=${CONTEXT_NAME:-ta-recruit}
DATA_DIR=${DATA_DIR:-"$PROJECT_DIR/data"}
TOMCAT_PREFIX=${TOMCAT_PREFIX:-$(brew --prefix tomcat@10)}
TOMCAT_HOME=${TOMCAT_HOME:-"$TOMCAT_PREFIX/libexec"}
WEBAPPS_DIR="$TOMCAT_HOME/webapps"
WAR_PATH="$PROJECT_DIR/target/$CONTEXT_NAME.war"
DEPLOY_WAR="$WEBAPPS_DIR/$CONTEXT_NAME.war"
DEPLOY_DIR="$WEBAPPS_DIR/$CONTEXT_NAME"
SETENV_PATH="$TOMCAT_HOME/bin/setenv.sh"
SERVER_XML="$TOMCAT_HOME/conf/server.xml"
SKIP_BUILD=${SKIP_BUILD:-0}
OPEN_BROWSER=${OPEN_BROWSER:-0}

usage() {
    cat <<EOF
Usage: ./deploy-local.sh [options]

Options:
  --skip-build       Reuse existing target/$CONTEXT_NAME.war
  --open             Open the login page after deployment
  --context NAME     Override context path name, default: $CONTEXT_NAME
  --data-dir PATH    Override runtime data directory, default: $DATA_DIR
  --tomcat-home PATH Override Tomcat home, default: $TOMCAT_HOME
  --help             Show this help message

Environment overrides:
  CONTEXT_NAME, DATA_DIR, TOMCAT_HOME, SKIP_BUILD=1, OPEN_BROWSER=1
EOF
}

while [ $# -gt 0 ]; do
    case "$1" in
        --skip-build)
            SKIP_BUILD=1
            ;;
        --open)
            OPEN_BROWSER=1
            ;;
        --context)
            [ $# -ge 2 ] || { echo "Missing value for --context" >&2; exit 1; }
            CONTEXT_NAME=$2
            shift
            ;;
        --data-dir)
            [ $# -ge 2 ] || { echo "Missing value for --data-dir" >&2; exit 1; }
            DATA_DIR=$2
            shift
            ;;
        --tomcat-home)
            [ $# -ge 2 ] || { echo "Missing value for --tomcat-home" >&2; exit 1; }
            TOMCAT_HOME=$2
            shift
            ;;
        --help|-h)
            usage
            exit 0
            ;;
        *)
            echo "Unknown option: $1" >&2
            usage >&2
            exit 1
            ;;
    esac
    shift
done

WEBAPPS_DIR="$TOMCAT_HOME/webapps"
WAR_PATH="$PROJECT_DIR/target/$CONTEXT_NAME.war"
DEPLOY_WAR="$WEBAPPS_DIR/$CONTEXT_NAME.war"
DEPLOY_DIR="$WEBAPPS_DIR/$CONTEXT_NAME"
SETENV_PATH="$TOMCAT_HOME/bin/setenv.sh"
SERVER_XML="$TOMCAT_HOME/conf/server.xml"

command -v mvn >/dev/null 2>&1 || {
    echo "mvn is not on PATH." >&2
    exit 1
}

command -v brew >/dev/null 2>&1 || {
    echo "brew is not on PATH." >&2
    exit 1
}

[ -d "$TOMCAT_HOME" ] || {
    echo "Tomcat home not found: $TOMCAT_HOME" >&2
    exit 1
}

[ -d "$WEBAPPS_DIR" ] || {
    echo "Tomcat webapps directory not found: $WEBAPPS_DIR" >&2
    exit 1
}

mkdir -p "$DATA_DIR"

if [ "$SKIP_BUILD" != "1" ]; then
    echo "Building WAR with Maven..."
    (
        cd "$PROJECT_DIR"
        mvn clean package
    )
fi

[ -f "$WAR_PATH" ] || {
    echo "WAR not found: $WAR_PATH" >&2
    exit 1
}

cat >"$SETENV_PATH" <<EOF
export TARECRUIT_DATA_DIR="$DATA_DIR"
EOF
chmod +x "$SETENV_PATH"

echo "Stopping Tomcat service..."
brew services stop tomcat@10 >/dev/null 2>&1 || true
sleep 2

echo "Updating deployed files..."
rm -rf "$DEPLOY_WAR" "$DEPLOY_DIR"
cp "$WAR_PATH" "$DEPLOY_WAR"

echo "Starting Tomcat service..."
brew services start tomcat@10 >/dev/null

PORT=$(sed -n 's/.*<Connector port="\([0-9][0-9]*\)".*/\1/p' "$SERVER_XML" | head -n 1)
if [ -z "${PORT:-}" ]; then
    PORT=8080
fi

if [ "$CONTEXT_NAME" = "ROOT" ]; then
    APP_URL="http://localhost:$PORT/login"
else
    APP_URL="http://localhost:$PORT/$CONTEXT_NAME/login"
fi

echo "Waiting for application: $APP_URL"
attempt=0
until curl -fsS -o /dev/null "$APP_URL"; do
    attempt=$((attempt + 1))
    if [ "$attempt" -ge 30 ]; then
        echo "Application did not become ready in time." >&2
        echo "Check logs under: $TOMCAT_HOME/logs" >&2
        exit 1
    fi
    sleep 2
done

if [ "$OPEN_BROWSER" = "1" ]; then
    open "$APP_URL"
fi

echo
echo "Deployment complete."
echo "Application URL: $APP_URL"
echo "Runtime data: $DATA_DIR"
echo "Tomcat home: $TOMCAT_HOME"
