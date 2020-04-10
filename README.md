# Object Projection

```javascript
ProjectionBuilder
          .bind(EmployeeTo.class)

          .map("name")
                .source(Person.class)
          
          .map("employer")
                .source(Employer.class, "name")
                .optional()
          
          .build();
```

```java
@Projection
public class TestProjection {

  @Sources(
      value = {
              @Source(type=User.class, property = "username"),
              @Source(type=Repository.class, property = "id"),
              },
      map = @Conversion(
              type = URLTemplate.class,
              value = "https://www.example.org/{username}/{repositoryId}"
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
  @Provided(optional = true)
  String context;
  ...
}
```

```java

  @Sources(value = {
      @Source("stringProperty"),
      @Source("integerProperty"),
      @Source("booleanProperty"),
      })
  Collection<String> list;
  ...
}
```

