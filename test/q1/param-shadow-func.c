int foo(int foo /* expect warning */) {
    return foo;
}
int main(void) {
    return foo(0);
}
