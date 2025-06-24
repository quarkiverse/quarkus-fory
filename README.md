# Quarkus Fory
<!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
[![All Contributors](https://img.shields.io/badge/all_contributors-2-orange.svg?style=flat-square)](#contributors-)
<!-- ALL-CONTRIBUTORS-BADGE:END -->

[![Version](https://img.shields.io/maven-central/v/io.quarkiverse.fory/quarkus-fory?logo=apache-maven&style=flat-square)](https://central.sonatype.com/artifact/io.quarkiverse.fory/quarkus-fory-parent)

Quarkus Fory is a Quarkus extension to use [Apache Fory](https://github.com/apache/fory) for serialization.

## Documentation

The documentation for this extension can be found [here](https://docs.quarkiverse.io/quarkus-fory/dev/index.html) while the documentation for the Apache Fory can be found at https://fory.apache.org/.

## Getting Started

```java
import java.util.List;
import java.util.Map;

import io.quarkiverse.fory.ForySerialization;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import org.apache.fory.BaseFory;

@ForySerialization
record Foo(int f1, String f2, List<String> f3, Map<String, Long> f4) {
}

@Path("/fory")
@ApplicationScoped
public class ForyResources {
  @Inject
  BaseFory fory;

  @GET
  @Path("/record")
  public Boolean testSerializeFooRecord() {
    Foo foo1 = new Foo(10, "abc", List.of("str1", "str2"), Map.of("k1", 10L, "k2", 20L));
    Foo foo2 = (Foo) fory.deserialize(fory.serialize(foo1));
    return foo1.equals(foo2);
  }
}
```

## Use Apache Fory with Quarkus REST/RESTEasy
You can send a http request with Fory protocol, and let Fory to handle your objects serialization.

The usage will be different if class registration is disabled or enabled:
- Enable class registration: you must register class with same ID as the server, you should assign an id using
`@ForySerialization(classId = xxx)`, otherwise Fory will allocate an auto-generated ID which you won't know at the 
client for registration.
- Disable class registration: no class id are needed to register, which is more easy to use, but the serialized size
will be larger since Fory will serialize class as a string instead of an id. Note that `quarkus-fory` will only allow 
classes annotated by `@ForySerialization` for deserialization, the deserialization will be safe as class registration
enabled.

### Class registration enabled
Server example:
```java
import io.quarkiverse.fory.ForySerialization;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@ForySerialization(classId = 100)
record Struct(int f1, String f2) {
}

@Path("/fory")
@ApplicationScoped
public class ForyResources {
  @POST
  @Path("/struct")
  @Produces("application/fory")
  @Consumes("application/fory")
  public Struct testStruct(Struct obj) {
    return new Struct(10, "abc");
  }
}
```

Client example:
```java
import static io.restassured.RestAssured.given;
import org.apache.fory.ThreadSafeFory;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class ForyClient {
  private static ThreadSafeFory fory = Fory.builder().requireClassRegistration(false).buildThreadSafeFory();
  static {
    fory.register(Struct.class, 100, true);
  }

  public static void main(String[] args) {
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    Struct struct = Struct.create();
    Response response = given().contentType("application/fory").body(fory.serialize(struct)).when()
      .post("/fory/struct").then().statusCode(200).contentType("application/fory").extract().response();
    byte[] result = response.body().asByteArray();
    Struct struct1 = (Struct) fory.deserialize(result);
    System.out.println(struct1);
  }
}
```

### Class registration disabled
Server example:
```java
@ForySerialization
record Struct(int f1, String f2) {
}

@Path("/fory")
@ApplicationScoped
public class ForyResources {
  @POST
  @Path("/struct")
  @Produces("application/fory")
  @Consumes("application/fory")
  public Struct testStruct(Struct obj) {
    return new Struct(10, "abc");
  }
}
```

Client example
```java
public class RestClient {
  private static ThreadSafeFory fory = Fory.builder().requireClassRegistration(false).buildThreadSafeFory();

  public static void main(String[] args) {
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    Struct struct = Struct.create();
    Response response = given().contentType("application/fory").body(fory.serialize(struct)).when()
      .post("/fory/struct").then().statusCode(200).contentType("application/fory").extract().response();
    byte[] result = response.body().asByteArray();
    Struct struct1 = (Struct) fory.deserialize(result);
    System.out.println(struct1);
  }
}
```

More details about usage can be found [here](https://docs.quarkiverse.io/quarkus-fory/dev/index.html).

## Contributors ✨

Thanks go to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tbody>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/chaokunyang"><img src="https://avatars.githubusercontent.com/u/12445254?v=4?s=100" width="100px;" alt="Shawn Yang"/><br /><sub><b>Shawn Yang</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-fory/commits?author=chaokunyang" title="Code">💻</a> <a href="#maintenance-chaokunyang" title="Maintenance">🚧</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://zhfeng.github.io/"><img src="https://avatars.githubusercontent.com/u/1246139?v=4?s=100" width="100px;" alt="Zheng Feng"/><br /><sub><b>Zheng Feng</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-fory/commits?author=zhfeng" title="Code">💻</a> <a href="#maintenance-zhfeng" title="Maintenance">🚧</a></td>
    </tr>
  </tbody>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!
