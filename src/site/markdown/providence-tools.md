Providence Tools
================

There are made a set of command line tools for providence. You can get them
from the [downloads](downloads.html) section. They're in the same `providence`
package.

## Code Generator

The compiler (or code generator) `pvdc`, this can be used in place of the maven
plugin for non-java projects. It can compile for:

- `java`: Writes the providence java generated code.
- `java_tiny`: Writes the tiny java generated code.
- `json`: Simply writes out the thrift definition as thrift files.

See `pvdc --help` for more detailed info about the available options.

## Data Converter

The data converter `pvd` is a small program that can read a thrift IDL, and a
binary (or non-binary) serialized data file that follows that IDL structure,
and the output the result in some form of readable (or other binary) data
format. As for example:

Given the thrift IDL in `thrift/test.thrift`:

```thrift
struct MyData {
  1: string text
  2: i32 sequence
  3: list<string> tags
}
```

And a binary file with a set of data entries in test.MyData format. We could
do something like this:

```sh
cat test.data | pvd -I thrift/ test.MyData
```

And should make the output:

```json
{
  "text": "not a test at all",
  "sequence": 144
}
{
  "text": "test 2",
  "tags": [
    "first",
    "second"
  ]
}
```

Input and output can be specified to point directly to files (no shell
piping needed), and the input and output serialization format can be
specified too.

```sh
pvd -i fast_binary,file:test.data -o pretty -I thrift/ test.MyData
```

Which should read the data file serialized with the FastBinarySerializer format
and print it out with the simple "pretty printer" format.

## RPC Tool

The providence RPC tool `pvdrpc` is a program designed to test out thrift and
providence RPC service calls based on the same ad-hoc type parsing as `pvd`
uses. It supports HTTP servlet wrapped RPC (e.g. using the `TServlet`), the
`TSimpleServer` direct socket RPC, and the `TNonblockingServer` that also wraps
the calls in sized frame buffers.

The providence RPC tool connects to a remote service, and sends a service call
(which can be parsed from files the same way as the data files above), and sent
as an actual remote procedure call:

```json
[
  "test",
  1,
  0,
  {
    "request": {
      "test": "my test",
      "sequence": 44,
      "tags": [
        "first",
        "second"
      ]
    }
  }
]
```

Which is the RPC call data, and the shell command:

```sh
cat req.json | pvdrpc -I thrift/ test.MyService http://localhost:8080/test_service
```

Which would then:

- Parse the thrift IDL in thrift/ directory, pick out the test.MyService service
  from there.
- Read the req.json request call (which needs to follow the desired input format's
  service call syntax), and use that to send the actual RPC call to the remote
  service at `http://localhost:8000/test_service`.
- Parse the response (in whatever format returned) and print out to standard out
  so the user can read it. E.g.:

```json
[
  "test",
  2,
  0,
  {
    "success": {
      "test": "my test",
      "sequence": 44,
      "tags": [
        "first",
        "second"
      ]
    }
  }
]
```

Or if a non-200 HTTP response is received will print out the error message
received.

Note that in the two json structures shown there is the serialized service call
data in addition to the request and response wrappers around the actual request
and response objects. The format here is:

`["${method}", ${type}, ${sequence}, ${wrapper}]`

Where the type can be 1: request, 2: response, 3: exception, and 4: oneway.
The sequence number is something the transport layer (http, client etc) can
use to match response messages to the correct caller, and the wrapper is
either the `request params wrapper` the `response wrapper`, or the
`application exception`. Oneway calls are calls the does not want a response.

- The `request params wrapper` message is a generated thrift struct that
  contains all the method params as field values. E.g. the method
  `ResponseStruct my_test(1: i32 num, 2: string text)` would become
  equivalent to the struct:

    ```thrift
    struct my_test___params {
      1: i32 num
      2: string text
    }
    ```

- The `response wrapper` is a wrapper of the return type (put in a field
  named `success`), and of each possible exception put in a union. In order
  to make it impossible to conflict with field IDs, the success field gets
  the index `0`, which is not allowed to declare. E.g.:

    ```thrift
    union my_test___response {
      0: ResponseStruct success
      1: MyException ex1
    }
    ```

- The `application exception` is a generic providence exception message that can
  be passed with call type 3 (exception), usually to tell a client about a
  problem that was caused outside of the actual service call, e.g. serialization
  problems, bad method call, etc. It is equivalent to:
  
    ```thrift
    exception ApplicationException {
        1: string message
        2: i32 type
    }
    ```

#### Supported Protocols

Short overview over the RPC protocols supported by the RPC tool.

- `http://` and `https://`: Connects to a thrift `TServlet` or similar.
- `thrift://`: Connects to a `TSimpleServer` type thrift server.
- `thrift+nonblocking://`: Connects to a `TNonblockingServer` type thrift server,
  or a similar thrift server that wraps messages in `TFramedTransport`.

## Config Helper

Providence can be used as a base for generating config too. Take a look at the
`providence-config` module for an in depth example of the config markup syntax.
But using the config is not always preferrable, or trivial to utilize in it's raw
form. So in order to transform / build the config, and in order to validate or test
the config written before deployed, we have a "config helper tool", or `pvdcfg`.

The tool have three main functions:

- `pvdcfg print`: Parse and compile the target config and print it out to standard out.

    ```sh
    $ pvdcfg -I providence/ print resources/my_service.cfg
    {
      http {
        port = 8080
      }
      db {
        host = "localhost:1234"
      }
    }
    ```

- `pvdcfg params`: Show a list of params that can be used to augment the config, and in
  which config file its defined.
  
    ```sh
    $ pvdcfg -I providence/ params resources/my_service.cfg
    http_port = 8080 (resources/my_service.cfg)
    db_host = "localhost:1234" (resources/db.cfg)
    ```

- `pvdcfg validate`: Validate a set of config files from a base config. This is similar to
  the 'print' command, but prints nothing on success, and only the error message if the parsing
  fails at any step.

    ```sh
    $ pvdcfg -I providence/ validate resources/good.cfg
    $ pvdcfg -I providence/ validate resources/*.cfg
    Error in my_service.cfg line 23 position 6
        No such field 'blah' in config.Service
          blah = "nothing"
    ------^
    ```

Included configs can be found in two ways from each file (and it's checked in this order):

- Relative path from the parsed file. This includes parent directories (`..`).
- Relative path from a config root. This does *not* allow parent directories.

Note that the config's inclusion paths can *NOT* be parametrized. The only way to
parametrize the included config is to set up the config roots or symlinks to have the
expected files at the included locations.
