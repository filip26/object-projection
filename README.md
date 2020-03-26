# Object Projection


```java
@Projection
public class TestProjection {

  @Sources(
      value = {
              @Source(type=User.class, value = "username"),
              @Source(type=Repository.class, value = "id"),
              },
      map = @Conversion(
              type = URLTemplate.class,
              value="https://www.example.org/{username}/{repositoryId}"
              )
      )
  String href;  // e.g. https://www.example.org/filip26/R1234

  ...
}
```

```java

  @Source(value = "prices"
      map = {
        @Conversion(type = Flat.class),
        @Conversion(type = Sum.class)
        }
      )
  Long total;
  ...
  @Provided(optional=true)
  String context;
  ...
}
```
