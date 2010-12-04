
typedef int foo;
typedef struct {
    foo field;
} bar;
typedef struct {
    foo field1;
    bar field2;
} baz;

static baz hello_world;
