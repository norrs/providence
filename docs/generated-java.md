Generated Java Code
===================

An overview over the generated java code.

# Files

Each .thrift file will generate a number of java files collesponding to this ruleset:

 - Each enum, struct, union and service will have it's own .java file generated.
   The class name is the CamelCase type name based on the 

## Structs

The main java struct interface:

```java
class MyStruct implements TMessage<MyStruct> {
  private final ObjectType mFieldName;

  private MyStruct(Builder builder) {
    <if collection>
    mFieldName = Collections.unmodifiable<Ctype>(builder.mFieldName);
    <else>
    mFieldName = builder.mFieldName;
    </if>
  }

  <if collection>
  public int numFieldName() {
    return mFieldName.size();
  }
  <else>
  public boolean hasFieldName() {
    return mFieldName != null;
  }
  </if>

  public <ValueType> getFieldName() {
    return mFieldName;
  }
}
```

### Builder

```java
  @Override
  public Builder mutate() {
    return new MyStruct.Builder(this);
  }

  public static class Builder extends TStructBuilder<MyStruct> {
    private ClassName mFieldName;

    public void setFieldName(TypeName value) {
      mFieldName = value;
    }

    @Override
    public MyStruct build() {
      return new MyStruct(this);
    }
  }
```

The way to use the builder is:

```java
  MyStruct.builder().setFieldName(value).build();
```

Or

```java
  myStruct.mutate().setFieldName(value).build();
```

### The TMessage interface

```java
  public boolean has(int id) {
    switch (id) {
      case <field-id>: return hasFieldName();
      <or>
      case <field-id>: return numFieldName() > 0;
    }
  }
  public int num(int id) {
    switch (id) {
      case <field-id>: return numFieldName();
      <or>
      case <field-id>: return hasFieldName() ? 1 : 0;
    }
  }
  public Object get(int id) {
    switch (id) {
      case <field-id>: return getFieldName();
    }
    return null;
  }

  public static class Builder {
    <...>

    public void set(int id, Object value) {
      switch (id) {
        case <field-id>: setFieldName((Class) value); return;
      }
    }
  }
```

### Descriptor

```java
  public static final TStructType<MyStruct> DESCRIPTOR = \_createDescriptor();

  public static TStructTypeProvider<MyStruct> provider() {
    return new TStructTypeProvider<MyStruct>() {
      @Override
      public TStructType<MyStruct> descriptor() {
        return DESCRIPTOR;
      }
    }
  }

  public TStructType<MyStruct> descriptor() {
    return DESCRIPTOR;
  }

  private static class \_Factory implements TStructBuilderFactory<MyStruct> {
    @Override
    public MyStruct.Builder create() {
      return new MyStruct.Builder();
    }
  }

  private static \_createDescriptor() {
    List<TField> fields = new LinkedList<>();
    <foreach field>
    field.add(new TField(...));
    </foreach>
    return new TStructType<>(null, "package", "MyStruct", fields, new \_Factory());
  }
```

### Android

With the `--android` option set, android Parcellable handling is added. It adds
the `Parcellable` interface with the `int describeContents()` and
`writeToParcel(Parsel, int)` methods, and adds a static CREATOR to parse the
parcel into a message. Note that the parcellable does NOT handle unknown
fields, so is not forward compatible when fields are added.

```java
  @Override
  public int describeContents() { return 0; }
  @Override
  public void writeToParcel(Parcel dest, int flags) {
    <foreach field>
    if (hasFieldName()) {
      dest.writeInt(<id>);
      // type dependent.
      dest.write<CType>(mFieldName);
      </if>
    }
    </foreach>

    dest.writeInt(0);
  }
  public static final Parcelable.Creator<ClassName> CREATOR = new Parcelable.Creator<>() {
    @Override
    public ClassName createFromParcel(Parcel source) {
      Builder builder = new Builder();
      loop: while (source.dataAvail() > 0) {
        int field = source.readInt();
        switch (field) {
          case 0: break loop;
        <foreach field>
          // type dependent.
          case <id>: builder.setFieldName(source.read<Type>());
        </fireach>
        }
      }
      return builder.build();
    }
    @Override
    public ClassName[] newArray(int size) {
      return new ClassName[size];
    }
  };
```

## Unions

## Exceptions

## Enums

Enums are handled by real java enums. Which gives a number of benefits
programming wise. E.g. switch statements, type validation etc.

```java
enum MyEnum implements TEnumValue<MyEnum> {
  <foreach value>
  VALUE(<value>),
  </foreach>
  ;

  
}
```

## Services

## Constants

... TO BE DECIDED!

# General

The naming convention for java is to use camel cased names where classes and
enums are capitalized and methods are not. To generate the camelcased name:

- Split the name string based on the '\_' charachter.
- Capitalize each part and join to one string.
- Prefix with a lower-case word for method and field names.

E.g. `myname` would become Myname, while `my_name` would become MyName. This
maes it easy to follow both C/C++, Python and java coding standard without any
more hokus-pokus.