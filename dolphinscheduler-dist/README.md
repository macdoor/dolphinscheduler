# DolphinScheduler Distribution Packaging

## Build bin tar.gz (with UI and plugins)

```bash
mvn clean package -Prelease -DskipTests -Dspotless.skip=true -Dbuild.plugins.skip=false
```

Output: `dolphinscheduler-dist/target/apache-dolphinscheduler-<version>-bin.tar.gz`

### Key parameters

| Parameter | Default | Description |
|---|---|---|
| `-Prelease` | — | Enables UI build and assembly (`build.ui.skip=false`, `build.assembly.skip=false`) |
| `-Pstaging` | — | Same as release but also runs plugin assembly script (`build.plugins.skip=false`) |
| `-Dbuild.plugins.skip=false` | `false` (root), `true` (release profile) | Must be `false` to keep plugins in the final tar.gz. The `release` profile sets it to `true`, which causes `assembly-plugins.sh` to **remove** all plugins after assembly. Override with `-Dbuild.plugins.skip=false`. |
| `-Dbuild.ui.skip=false` | `true` | Build the frontend (requires Node.js). Automatically set by `release`/`staging` profiles. |
| `-Dbuild.assembly.skip=false` | `true` | Run maven-assembly-plugin to produce tar.gz. Automatically set by `release`/`staging` profiles. |

### Quick reference

```bash
# Full build with UI + plugins (recommended)
mvn clean package -Prelease -DskipTests -Dspotless.skip=true -Dbuild.plugins.skip=false

# Alternatively, use staging profile (plugins included by default)
mvn clean package -Pstaging -DskipTests -Dspotless.skip=true

# Backend only (no UI, no tar.gz)
mvn clean package -DskipTests -Dspotless.skip=true
```

## Included plugins

The assembly descriptor (`dolphinscheduler-bin.xml`) is configured to include only the following plugins:

### alert-plugins
- dolphinscheduler-alert-http
- dolphinscheduler-alert-prometheus

### datasource-plugins
- dolphinscheduler-datasource-k8s
- dolphinscheduler-datasource-mysql
- dolphinscheduler-datasource-postgresql
- dolphinscheduler-datasource-ssh
- dolphinscheduler-datasource-starrocks

### task-plugins
- dolphinscheduler-task-flink
- dolphinscheduler-task-flink-stream
- dolphinscheduler-task-flink-sqlgateway
- dolphinscheduler-task-http
- dolphinscheduler-task-grpc
- dolphinscheduler-task-java
- dolphinscheduler-task-k8s
- dolphinscheduler-task-linkis
- dolphinscheduler-task-seatunnel
- dolphinscheduler-task-shell
- dolphinscheduler-task-sql

### storage-plugins
- dolphinscheduler-storage-s3

### Adding or removing plugins

Edit `dolphinscheduler-dist/src/main/assembly/dolphinscheduler-bin.xml`, find the `<!-- plugins -->` section, and add/remove `<include>` entries. For example, to add the spark task plugin:

```xml
<include>dolphinscheduler-task-spark/target/dolphinscheduler-task-spark-*-shade.jar</include>
```

To restore the original behavior (include all plugins), change each plugin category back to a wildcard:

```xml
<include>dolphinscheduler-task-*/target/dolphinscheduler-task-*-shade.jar</include>
```

## macOS packaging notes

When building on macOS for Linux deployment, `assembly-plugins.sh` automatically:
- Removes `._*` (AppleDouble) and `.DS_Store` files before repacking
- Sets `COPYFILE_DISABLE=1` during `tar` to prevent xattr headers

This avoids `tar: Ignoring unknown extended header keyword 'LIBARCHIVE.xattr.com.apple.provenance'` warnings on Linux.
