#!/bin/bash
# Navigate to the project directory
cd "$(dirname "$0")"

# Capture the original branch
ORIGINAL_BRANCH=$(git rev-parse --abbrev-ref HEAD)
echo "Starting on branch: $ORIGINAL_BRANCH"

# Get current date and time
CURRENT_DATE_TIME=$(date +'%d %B %Y %H:%M')

# Check if there are changes in files other than optimized_xpaths/*.json
if [ -n "$(git diff --name-only | grep -v '^optimized_xpaths/.*\.json$')" ]; then
    echo "Changes detected in files other than optimized_xpaths/*.json. Skipping commit."
    exit 0
fi

# Check for untracked files in optimized_xpaths/ or changes in tracked optimized_xpaths/*.json files
UNTRACKED_FILES=$(git status --porcelain | grep '^?? optimized_xpaths/' | wc -l)
CHANGED_FILES=$(git diff --name-only -- 'optimized_xpaths/*.json' | wc -l)

if [ "$UNTRACKED_FILES" -eq 0 ] && [ "$CHANGED_FILES" -eq 0 ]; then
    echo "No changes or new files detected in optimized_xpaths/*.json. Skipping commit."
    exit 0
fi

# Configure Git (only required if running from CI/CD)
git config --global user.email "your-email@example.com"
git config --global user.name "Your Name"

# Create a unique branch name with date and timestamp
BRANCH_NAME="update-xpaths-$(date +%d%B%Y-%H%M)"
git checkout -b "$BRANCH_NAME"

# Add and commit all updated JSON files
git add optimized_xpaths/*.json
git commit -m "Auto-update optimized XPaths after test run"

# Push the new branch to remote
git push origin "$BRANCH_NAME"
echo "✅ JSON updates pushed successfully to branch: $BRANCH_NAME"

# Create a PR (requires GitHub CLI)
PR_TITLE="Auto-update XPaths ${CURRENT_DATE_TIME}"
gh pr create --base master --head "$BRANCH_NAME" --title "$PR_TITLE" --body "This PR updates XPaths automatically based on the latest test run."

# Capture the PR link
PR_URL=$(gh pr view --json url -q .url)

# Send a Slack notification with a clickable PR link
SLACK_WEBHOOK_URL="https://hooks.slack.com/services/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"
CHANNEL_ID="C08K6022FA4"
TEAM_MEMBER="<@U03DP6KFJAC>"

curl -X POST -H 'Content-type: application/json' --data "{
    \"channel\": \"${CHANNEL_ID}\",
    \"text\": \"💻 Self-Healing Update Complete. ✋🏻 ${CURRENT_DATE_TIME}\n${TEAM_MEMBER}, a new PR has been created: *<${PR_URL}|PR>*\"
}" $SLACK_WEBHOOK_URL

# Switch back to the original branch and pull latest changes
git checkout "$ORIGINAL_BRANCH"
git pull origin "$ORIGINAL_BRANCH"

echo "✅ Switched back to original branch: $ORIGINAL_BRANCH"