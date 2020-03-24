# Object Projection


```java
@Projection
public class TestProjection {

  @Sources(
      value = {
              @Source(type=User.class, value = "username"),
              @Source(type=Repository.class, value = "id"),
              },
      map = @IFunction(
              type = UrlPatternFnc.class,
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
        @IFunction(type = FlatFnc.class),
        @IFunction(type = SumFnc.class)
        }
      )
  Long total;
  ...
  @Provided(optional=true)
  String context;
  ...
}
```
