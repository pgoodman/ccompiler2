
/* declare b as array 10
 *      of pointer
 *          to array 11 of int */
int ((a[10]))[11];
int (*(b[10]))[11];

int main(void) {
    sizeof a; /* expect 4 * 11 * 10 = 440 */
    sizeof b; /* expect 8 * 10 = 80 */
    return 0;
}
