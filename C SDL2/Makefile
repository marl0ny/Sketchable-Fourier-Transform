SHELL = /bin/bash
FLAGS = -O2 -Wall -g

# Put your own include and lib path for sdl2 here:
INCLUDE = -I${HOME}/include
LIBS = -L${HOME}/lib -lSDL2 -lm

TARGETDIR = .
# TARGETDIR = ${HOME}
TARGET = ${TARGETDIR}/circles_drawer

.PHONY = all clean example0 example1 example2

all: ${TARGET}

${TARGET}: *.o
	cc ${FLAGS} -o $@ $^ ${LIBS}

*.o: circles_drawer.c
	cc ${FLAGS} -c $^ ${INCLUDE}

clean:
	rm -f *.o ${TARGET}

example0: ${TARGET}
	${TARGET}

# Rectangle
example1: ${TARGET}
	${TARGET} 200 200 200 300 200 400 350 400 \
	          500 400 500 300 500 200 350 200 200 200

# Triangle
example2: ${TARGET}
	${TARGET} 300 100 275 150 250 200 225 250 200 300\
	          100 500 125 500 150 500 175 500 200 500\
			  500 500 475 450 450 400 425 350 400 300\
			  300 100

# TODO: Add more examples.