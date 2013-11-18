<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
       "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<body>
<pre>CC = gcc
PORT=52836
CFLAGS = -DPORT=$(PORT) -g -Wall



buxserver: buxserver.o lists.o lists.h wrapsock.o wrapsock.h
	$(CC) $(CFLAGS) -o buxserver buxserver.o lists.o wrapsock.o

buxserver.o: buxserver.c lists.h wrapsock.h
	$(CC) $(CFLAGS) -c buxserver.c

lists.o: lists.c lists.h
	$(CC) $(CFLAGS) -c lists.c
	
wrapsock.o: wrapsock.c wrapsock.h
	$(CC) $(CFLAGS) -c wrapsock.c

clean: 
	rm buxserver *.o
</pre>
</body>
</html>
