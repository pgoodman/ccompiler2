
extern int printf(const char *, ...);

void foo(char *string) {
    printf("I printed: %s\n", string);
}

int main(void) {
    foo("hello world");
    return 0;
}
