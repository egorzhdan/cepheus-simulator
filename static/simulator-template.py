__author__ = "egorzh"
# Simulator template for python3

def __get_sensors():
    left, fwd, right = input().split()
    return (left == 'free', fwd == 'free', right == 'free')


def forward():
    print('/robot move forward', flush=True)
    return __get_sensors()


def turn_left():
    print('/robot turn left', flush=True)
    return __get_sensors()


def turn_right():
    print('/robot turn right', flush=True)
    return __get_sensors()


# type your code here
print('you can use debug output')
print('just make sure it does not start with slash')

sensors = forward()
while sensors[1]:
    sensors = forward()
turn_left()
forward()
forward()

print('done :)')
