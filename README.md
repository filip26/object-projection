# Object Projection


```java
@Projection
public class TestProjectionUrl {

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
