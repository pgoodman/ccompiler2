
extern int printf(const char *, ...);

int main(void) {
    if(0) {
        printf("you shouldn't see me 1!\n");
    } else if(1) {
        printf("you should see me!\n");
    } else {
        printf("you shouldn't see me 2!\n");
    }

    return 0;
}
