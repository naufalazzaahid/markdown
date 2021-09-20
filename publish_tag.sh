tag="0.0.1"

git tag -d $tag
git push --delete origin $tag
git tag "$tag"
git push -f origin main --tags