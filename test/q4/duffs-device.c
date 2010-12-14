
extern int printf(const char *, ...);

send(int *from, int *to, int count) {
    register n=(count+7)/8;
    switch(count%8){
        case 0: do{ *to++ = *from++;
        case 7:     *to++ = *from++;
        case 6:     *to++ = *from++;
        case 5:     *to++ = *from++;
        case 4:     *to++ = *from++;
        case 3:     *to++ = *from++;
        case 2:     *to++ = *from++;
        case 1:     *to++ = *from++;
        }while(--n>0);
    }
}

void print_array(int *arr, int len) {
    while(--len) {
        printf("%d ", *arr++);
    }
    printf("\n");
}

int main(void) {
    int foo[10] = {0, 1, 2, 3, 4, 5, 6, 7, 8};
    int bar[10] = {0};

    print_array(foo, 10);
    print_array(bar, 10);

    send(foo, bar, 10);

    print_array(bar, 10);

    return 0;
}
