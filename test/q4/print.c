
extern int printf(const char *, ...);

void foo(char *string, char c, float x) {
    printf("I printed: %s '%c' %ld %c %f\n", string, c, (long int) x, 'c', (float) (signed int) 32);
}

int main(void) {
    foo("hello world", '$', 99.0);
    return 0;
}
