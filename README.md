# Quarkus Fury
<!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
[![All Contributors](https://img.shields.io/badge/all_contributors-2-orange.svg?style=flat-square)](#contributors-)
<!-- ALL-CONTRIBUTORS-BADGE:END -->

[![Version](https://img.shields.io/maven-central/v/io.quarkiverse.fury/quarkus-fury?logo=apache-maven&style=flat-square)](https://central.sonatype.com/artifact/io.quarkiverse.fury/quarkus-fury-parent)

Quarkus Fury is a Quarkus extension to use [Apache Fury](https://github.com/apache/fury) for serialization.

## Documentation

The documentation for this extension can be found [here](https://docs.quarkiverse.io/quarkus-fury/dev/index.html) while the documentation for the Apache Fury can be found at https://fury.apache.org/.

## Getting Started

```java
import java.util.List;
import java.util.Map;

import io.quarkiverse.fury.FurySerialization;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import org.apache.fury.BaseFury;

@FurySerialization
record Foo(int f1, String f2, List<String> f3, Map<String, Long> f4) {
}

@Path("/fury")
@ApplicationScoped
public class FuryResources {
  @Inject
  BaseFury fury;

  @GET
  @Path("/record")
  public Boolean testSerializeFooRecord() {
    Foo foo1 = new Foo(10, "abc", List.of("str1", "str2"), Map.of("k1", 10L, "k2", 20L));
    Foo foo2 = (Foo) fury.deserialize(fury.serialize(foo1));
    return foo1.equals(foo2);
  }
}
```

More details about usage can be found [here](https://docs.quarkiverse.io/quarkus-fury/dev/index.html).

## Contributors ✨

Thanks go to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tbody>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/chaokunyang"><img src="https://avatars.githubusercontent.com/u/12445254?v=4?s=100" width="100px;" alt="Shawn Yang"/><br /><sub><b>Shawn Yang</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-fury/commits?author=chaokunyang" title="Code">💻</a> <a href="#maintenance-chaokunyang" title="Maintenance">🚧</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://zhfeng.github.io/"><img src="https://avatars.githubusercontent.com/u/1246139?v=4?s=100" width="100px;" alt="Zheng Feng"/><br /><sub><b>Zheng Feng</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-fury/commits?author=zhfeng" title="Code">💻</a> <a href="#maintenance-zhfeng" title="Maintenance">🚧</a></td>
    </tr>
  </tbody>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!
