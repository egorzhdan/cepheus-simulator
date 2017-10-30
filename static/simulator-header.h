//
// Created by Egor Zhdan on 30/10/2017.
//

/*
 * You DO NOT need to submit this header file
 */

#ifndef SIMULATOR_HEADER_H
#define SIMULATOR_HEADER_H

#define SENSORS struct sensors_response_t
#define DEBUG(A) printf(A"\n");

#include <stdio.h>
#include <stdbool.h>
#include <memory.h>

SENSORS {
    bool free_forward;
    bool free_left;
    bool free_right;
};

SENSORS __get_sensors() {
    char in_left[256], in_fwd[256], in_right[256];
    scanf("%s%s%s", in_left, in_fwd, in_right);
    SENSORS s;
    s.free_left = (strcmp(in_left, "free") == 0);
    s.free_forward = (strcmp(in_fwd, "free") == 0);
    s.free_right = (strcmp(in_right, "free") == 0);
    return s;
}

SENSORS move_forward() {
    printf("/robot move forward\n");
    fflush(stdout);
    return __get_sensors();
}

SENSORS turn_left() {
    printf("/robot turn left\n");
    fflush(stdout);
    return __get_sensors();
}

SENSORS turn_right() {
    printf("/robot turn right\n");
    fflush(stdout);
    return __get_sensors();
}

#endif //SIMULATOR_HEADER_H
