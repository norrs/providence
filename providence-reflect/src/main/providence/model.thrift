/**
 * Reflective thrift IDL description.
 *
 * Comments are gathered before the start of the next statement.
 *
 * - Line comments are accumulated with newline delimiter.
 *   Each line is individually trimmed.
 * - Block comments replace the entire comment.
 *   The first space after '*' on each line is ignored.
 */
namespace java net.morimekta.providence.model

/**
 * Struct variant for StructType. The lower-case of the enum value is the
 * thrift keyword.
 *
 * struct: No 'required' fields must be present (set to non-null value).
 * UNION: No required fields. Only one field set to be valid.
 * EXCEPTION: No 'cause' field, 'message' field *must* be a string (java).
 */
enum StructVariant {
    STRUCT = 1,
    UNION,
    EXCEPTION,
}

/**
 * <name> (= <value>)
 */
struct EnumValue {
    1: string comment;
    2: required string name;
    3: i32 value;
    4: map<string,string> annotations;
}

/**
 * enum {
 *   (<value> ([;,])?)*
 * }
 */
struct EnumType {
    1: string comment;
    2: required string name;
    3: list<EnumValue> values;
    4: map<string,string> annotations;
}

/**
 * typedef <type> <name>
 */
struct TypedefType {
    1: string comment;
    2: string type;
    3: string name;
}

/**
 * The requirement of the field.
 */
enum Requirement {
    DEFAULT = 0,
    OPTIONAL = 1,
    REQUIRED = 2,
}

/**
 * For fields:
 *   (<key>:)? (required|optional)? <type> <name> (= <default_value>)?
 * For const:
 *   const <type> <name> = <default_value>
 *
 * Fields without key is assigned values ranging from 65335 and down (2^16-1)
 * in order of appearance. Because of the "in order of appearance" the field
 * *must* be filled by the IDL parser.
 *
 * Consts are always given the key '0'.
 */
struct ThriftField {
    1: string comment;
    2: required i32 key;
    3: Requirement requirement = DEFAULT;
    4: required string type;
    5: required string name;
    6: string default_value;
    7: map<string,string> annotations;
}

/**
 * <variant> {
 *   (<field> ([,;])?)*
 * }
 */
struct StructType {
    1: string comment;
    2: StructVariant variant = StructVariant.STRUCT;
    3: required string name;
    4: list<ThriftField> fields;
    5: map<string,string> annotations;
}

/**
 * (oneway)? <return_type> <name>'('<param>*')' (throws '(' <exception>+ ')')?
 */
struct ServiceMethod {
    1: string comment;
    2: bool one_way = false;
    3: string return_type
    4: required string name;
    5: list<ThriftField> params;
    6: list<ThriftField> exceptions;
    7: map<string,string> annotations;
}

/**
 * service (extends <extend>)? {
 *   (<method> [;,]?)*
 * }
 */
struct ServiceType {
    1: string comment;
    2: required string name;
    3: string extend;
    4: list<ServiceMethod> methods;
    5: map<string,string> annotations;
}

/**
 * ( <enum> | <typedef> | <struct> | <service> | <const> )
 */
union Declaration {
    1: EnumType decl_enum;
    2: TypedefType decl_typedef;
    3: StructType decl_struct;
    4: ServiceType decl_service;
    5: ThriftField decl_const;
}

/**
 * <namespace>* <include>* <declataion>*
 */
struct ThriftDocument {
    // Must come before the first statement of the header.
    1: string comment;
    // Deducted from filename in .thrift IDL files.
    2: required string package;
    // include "<package>.thrift"
    3: list<string> includes;
    // namespace <key> <value>
    4: map<string,string> namespaces;
    5: list<Declaration> decl;
}

/**
 * Set of words used in thrift IDL as specific meanings.
 */
const set<string> kThriftKeywords = [
  // File header.
  "include", "namespace",
  // Primitive types.
  "bool", "byte", "i8", "i16", "i32", "i64", "double", "string", "binary",
  // Containers
  "list", "set", "map",
  // Defined Types keywords.
  "enum", "struct", "union", "exception", "const", "typedef", "service",
  // Field modifiers
  "required", "optional",
  // Extra keywords related to services.
  "extends", "throws", "oneway", "void"
];

/**
 * Other words that are reserved for language compat reasons. E.g. to enable
 * C++ classes to follow getter method naming convention to be raw field name
 * (java naming convention is typename safe):
 *
 * For field names, const names, struct names, method name etc.
 */
const set<string> kReservedWords = [
  "class", "public", "protected", "private",
  "byte", "short", "int", "long", "unsigned", "float",
  "for", "if", "while", "do", "else"
];
