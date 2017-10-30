#include <stdio.h>
#include "simulator-header.h"

int main() {
    DEBUG("debug output example");

    SENSORS s = move_forward();
    while (s.free_forward) {
        s = move_forward();
    }
    turn_left();
    move_forward();

    DEBUG("completed!");

    return 0;
}