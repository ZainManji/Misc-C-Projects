<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
       "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<body>
<pre>#include &lt;sys/types.h&gt;
#include &lt;unistd.h&gt;
#include &lt;stdlib.h&gt;
#include &lt;sys/socket.h&gt;
#include &lt;stdio.h&gt;


int
Accept(int fd, struct sockaddr *sa, socklen_t *salenptr)
{
    int  n;

    if ( (n = accept(fd, sa, salenptr)) &lt; 0) {
        perror(&quot;accept error&quot;);
        exit(1);
    }
    return(n);
}

void
Bind(int fd, const struct sockaddr *sa, socklen_t salen)
{
    if (bind(fd, sa, salen) &lt; 0){
        perror(&quot;bind error&quot;);
        exit(1);
    }
}

/* This connect wrapper function doesn't quite do what we want */
/* The fix is below */
/*void
Connect(int fd, const struct sockaddr *sa, socklen_t salen)
{
    if (connect(fd, sa, salen) &lt; 0) {
        perror(&quot;connect error&quot;);
        perror(1);
    }
}
*/

int
Connect(int fd, const struct sockaddr *sa, socklen_t salen)
{
    int result;
    if ((result = connect(fd, sa, salen)) &lt; 0) {
        perror(&quot;connect error&quot;);
    }
    return(result);
}

void
Listen(int fd, int backlog)
{
    if (listen(fd, backlog) &lt; 0) {
        perror(&quot;listen error&quot;);
        exit(1);
    }
}

int
Select(int nfds, fd_set *readfds, fd_set *writefds, fd_set *exceptfds,
       struct timeval *timeout)
{
    int n;

    if ( (n = select(nfds, readfds, writefds, exceptfds, timeout)) &lt; 0) {
        perror(&quot;select error&quot;);
        exit(1);
    }
    return(n);              /* can return 0 on timeout */
}


int
Socket(int family, int type, int protocol)
{
    int n;

    if ( (n = socket(family, type, protocol)) &lt; 0) {
        perror(&quot;socket error&quot;);
        exit(1);
    }
    return(n);
}

void
Close(int fd)
{
    if (close(fd) == -1) {
        perror(&quot;close error&quot;);
        exit(1);
    }
}
</pre>
</body>
</html>
