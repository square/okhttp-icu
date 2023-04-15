# Releasing

1. Update the `CHANGELOG.md`.

2. Update the `VERSION_NAME` in `gradle.properties` to the release version.

3. Commit and tag.

   ```bash
   export VERSION_NAME=`cat gradle.properties | grep VERSION_NAME | cut -d= -f2`
   git commit -am "Prepare version $VERSION_NAME"
   git tag -am "Version $VERSION_NAME" $VERSION_NAME
   ```

4. Update the `VERSION_NAME` in `gradle.properties` to the next "SNAPSHOT" version.

5. Commit and push.

   ```bash
   git commit -am "Prepare next development version"
   git push
   git push --tags
   ```

   This will trigger the GitHub Action `publish` workflow. It builds a release, uploads it to Maven
   Central, and promotes it.

6. Confirm the build succeeds on [GitHub Actions].


[GitHub Actions]: https://github.com/square/okhttp-icu/actions
