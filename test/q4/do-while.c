
extern int printf(const char *, ...);

int main(void) {
    int i = 0;

    do {
        printf("i = %d\n", i++);
    } while(i < 10);

    return 0;
}
