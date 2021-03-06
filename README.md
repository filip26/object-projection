![Java CI with Maven](https://github.com/filip26/object-projection/workflows/Java%20CI%20with%20Maven/badge.svg)
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/filip26/object-projection.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/filip26/object-projection/context:java)
[![codecov](https://codecov.io/gh/filip26/object-projection/branch/master/graph/badge.svg)](https://codecov.io/gh/filip26/object-projection)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# Object Projection

```javascript
Projection
      .bind(EmployeeTo.class)

      .map("name")    // DOE, John   - alt .map(EmployeeTo::getName, EmployeeTo::setName)
            .sources()
                .conversion(String[].class, String.class)
                    .forward(sources -> sources[1] + ", " + sources[0])    // DOE, John
                    
                .source(Person.class, "firstName")                // John
                    
                .source(Person.class, "lastName")                 // Doe
                    .conversion(String.class, String.class)
                        .forward(String::toUpperCase)             // DOE
          
      .map("employer")
            .source(Employer.class, "name")
            .optional()
                
      .build();
```

```java
@Projection
public class RecordTo {

  @Sources(
      value = {
              @Source(type=User.class, property = "username"),
              @Source(type=Repository.class, property = "id"),
              },
      map = @Conversion(
              type = URITemplate.class,
              value = "https://www.example.org/{username}/{repositoryId}"
              )
      )
  String href;  // e.g. https://www.example.org/filip26/R1234

  ...
}

Projection.scan(RecordTo.class).build();
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



```javascript
Projection
    .hashMap()

        .mapString("code")
            .source(Item.class)
          
        .mapDouble("price");
            .source(Item.class, "totalPrice")
          
        .mapReference("details", ItemDetailsTo.class)
             .provided()
                          
        .build();
```
