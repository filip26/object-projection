# Object Projection


```java
@Projection
public class TestProjection {

  @Sources(
      value = {
              @Source(type=TestObjectA.class, value = "longValue"),
              @Source(type=TestObjectAA.class, value = "stringValue")
              },
      map = @IFunction(
              type = UrlPatternFnc.class,
              value="https://www.example.org/{longValue}/{stringValue}"
              )
      )
  String href;

  ...
}
```

```java

  @Source(type=[...].class, value = "prices"
      map = {
        @IFunction(type = FlatFnc.class),
        @IFunction(type = SumFnc.class)
        }
      )
  Long total;

  ...
}
```
