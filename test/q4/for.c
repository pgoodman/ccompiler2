
extern int printf(const char *, ...);

int main(void) {
    int i;
    for(i = 0; i < 10; ++i) {
        printf("i = %d\n", i);
    }

    for(; 0; ) {
        printf("you shouldn't see me!\n");
    }

    return 0;
}
