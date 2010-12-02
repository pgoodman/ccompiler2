int main(void) {
foo:
bar:
foo: /* expect error */
    goto foo;
    goto bar;
    return 0;
}
