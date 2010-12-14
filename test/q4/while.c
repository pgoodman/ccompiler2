
extern int printf(const char *, ...);

int main(void) {
    int i = 0;

    while(i < 10) {
        printf("i = %d\n", i++);
    }

    return 0;
}
