<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
       "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<body>
<pre>/***** inetserver.c *****/
#include &lt;stdio.h&gt;
#include &lt;string.h&gt;
#include &lt;strings.h&gt;
#include &lt;unistd.h&gt;
#include &lt;stdlib.h&gt;        /* for getenv */
#include &lt;sys/types.h&gt;
#include &lt;sys/socket.h&gt;
#include &lt;netinet/in.h&gt;    /* Internet domain header */
#include &quot;wrapsock.h&quot;
#include &quot;lists.h&quot;
#define INPUT_ARG_MAX_NUM 5
#define INPUT_BUFFER_SIZE 256
#define DELIM &quot; \r\n&quot;
#define MAXLINE 256
#define MAXCLIENTS 30
#define LISTENQ 10

#ifndef PORT
#define PORT x
#endif

typedef struct {
	int soc;
	char buf[MAXLINE];
	int curpos;
	int asked_username;
	char *user_name;
} Client;

/* A standard template for error messages */
void error(const char *msg) {
    fprintf(stderr, &quot;Error: %s\n&quot;, msg);
}


/*
 * Read and process buxfer commands, write strings to client
 */
int process_args(int cmd_argc, char **cmd_argv, Group **group_list_addr, int ns, Client *c, Client *clients) {
    Group *group_list = *group_list_addr;
    Group *g;
    char *buf = malloc(INPUT_BUFFER_SIZE);
    char *user_buf = malloc(INPUT_BUFFER_SIZE);

    if (cmd_argc &lt;= 0) {
        return 0;
    } else if (strcmp(cmd_argv[0], &quot;quit&quot;) == 0 &amp;&amp; cmd_argc == 1) {
    	  write(ns, &quot;Exiting Buxfer. Goodbye.\r\n&quot;, strlen(&quot;Exiting Buxfer. Goodbye.\r\n&quot;));
    	  
        return -1;

    } else if (strcmp(cmd_argv[0], &quot;add_group&quot;) == 0 &amp;&amp; cmd_argc == 2) {
    	
        buf = add_group(group_list_addr, cmd_argv[1]);
        write(ns, buf, strlen(buf));     
        g = find_group(*group_list_addr, cmd_argv[1]);
        user_buf = add_user(g, c-&gt;user_name);
        
        //write to all clients online part of this group that user has joined that group
		  int l;
		  int k = 0;
		  int msg_len = 0;
		  User *current_user = g-&gt;users;
		  while (current_user != NULL)
   	  {     
   	  		msg_len = strlen(c-&gt;user_name) + strlen(&quot; has joined group &quot;) + strlen(g-&gt;name) + strlen(&quot;\r\n&quot;);
   	  		char * client_msg = malloc(msg_len);
   	  		for (k = 0; k &lt; MAXCLIENTS; k++)
   	  		{
   	  			if (strcmp(clients[k].user_name, current_user-&gt;name) == 0)
   	  			{
   	  				l = snprintf(client_msg, msg_len, &quot;%s&quot;, c-&gt;user_name);
   				 	l += snprintf(client_msg+l, msg_len, &quot;%s&quot;, &quot; has joined group &quot;);
   					l += snprintf(client_msg+l, msg_len, &quot;%s&quot;, g-&gt;name);
   					l += snprintf(client_msg+l, msg_len, &quot;%s&quot;, &quot;\r\n&quot;);
   	  				write(clients[k].soc, client_msg, msg_len);	
   	  			}	
   	  		}
        		current_user = current_user-&gt;next;
   	  }
        write(ns, user_buf, strlen(user_buf));

    } else if (strcmp(cmd_argv[0], &quot;list_groups&quot;) == 0 &amp;&amp; cmd_argc == 1) {
		  buf = list_groups(group_list);
		  write(ns, buf, strlen(buf));

    } else if (strcmp(cmd_argv[0], &quot;list_users&quot;) == 0 &amp;&amp; cmd_argc == 2) {
        if ((g = find_group(group_list, cmd_argv[1])) == NULL) {
        		write(ns, &quot;Group does not exist\r\n&quot;, strlen(&quot;Group does not exist\r\n&quot;));
        } else {
            buf = list_users(g);
            write(ns, buf, strlen(buf));
        }

    } else if (strcmp(cmd_argv[0], &quot;user_balance&quot;) == 0 &amp;&amp; cmd_argc == 2) {
        if ((g = find_group(group_list, cmd_argv[1])) == NULL) {
        		write(ns, &quot;Group does not exist\r\n&quot;, strlen(&quot;Group does not exist\r\n&quot;));
        } else {
            buf = user_balance(g, c-&gt;user_name);
            write(ns, buf, strlen(buf));
        }

    } else if (strcmp(cmd_argv[0], &quot;add_xct&quot;) == 0 &amp;&amp; cmd_argc == 3) {
        if ((g = find_group(group_list, cmd_argv[1])) == NULL) {
        		write(ns, &quot;Group does not exist\r\n&quot;, strlen(&quot;Group does not exist\r\n&quot;));
        } else {
            char *end;
            double amount = strtod(cmd_argv[2], &amp;end);
            if (end == cmd_argv[2]) {
            	 write(ns, &quot;Incorrect number format\r\n&quot;, strlen(&quot;Incorrect number format\r\n&quot;));
            } else {
                buf = add_xct(g, c-&gt;user_name, amount);
                write(ns, buf, strlen(buf));
            }
        }

    } else {
    	  write(ns, &quot;Incorrect syntax\r\n&quot;, strlen(&quot;Incorrect syntax\r\n&quot;));
    }
    return 0;
}



/*  read from the client
* 	returns -1 if the socket needs to be closed and 0 otherwise */
int readfromclient(Client *c, Group **group_list, Client *clients) {
	
	int name_len;
	int len;
	char *startptr;
	char *nameptr;
	int end_of_line = 0;
	
	//ask for username
	if (c-&gt;asked_username == 0)
	{
		
		//READ USER_NAME FROM CLIENT AND STORE THE READ NAME INTO c-&gt;username
		while(end_of_line == 0)
		{
			nameptr = &amp;c-&gt;user_name[c-&gt;curpos];
			name_len = read(c-&gt;soc, nameptr, MAXLINE - c-&gt;curpos);
			
			if(name_len &lt;= 0) 
			{
				if(name_len == -1) {
					perror(&quot;read on socket&quot;);
				}
				return -1;
				/* connection closed by client */
			} 
			else 
			{
				c-&gt;curpos += name_len; //eventually need to make sure that we don't read in more than 256 characters!
				c-&gt;user_name[c-&gt;curpos] = '\0';
	
				/* Did we get a whole line?*/
				if (strchr(c-&gt;user_name, '\n') || strchr(c-&gt;user_name, '\r') || c-&gt;curpos == 255) 
				{
					end_of_line = 1;
				}
			}		
		}
		c-&gt;curpos = 0;
		c-&gt;asked_username = 1;
		int user_name_len = strlen(c-&gt;user_name);
		
		if (c-&gt;user_name[user_name_len-1] == '\r' || c-&gt;user_name[user_name_len-1] == '\n')
		{
			c-&gt;user_name[user_name_len-1] = '\0';		
		}
		
		if (c-&gt;user_name[user_name_len-2] == '\r' || c-&gt;user_name[user_name_len-2] == '\n')
		{
			c-&gt;user_name[user_name_len-2] = '\0';		
		}
		
		//Write welcome message to client that just joined
		char *welcome_msg = malloc(strlen(c-&gt;user_name) + 42);
		int j = 0;
		j = snprintf(welcome_msg, strlen(c-&gt;user_name) + 40, &quot;%s&quot;, &quot;Welcome, &quot;);
      j += snprintf(welcome_msg+j, strlen(c-&gt;user_name) + 40, &quot;%s&quot;, c-&gt;user_name);
      j += snprintf(welcome_msg+j, strlen(c-&gt;user_name) + 40, &quot;%s&quot;, &quot;! Please enter Buxfer commands\r\n\0&quot;);
		write(c-&gt;soc, welcome_msg, strlen(welcome_msg));
		return 0;
	}	
	
	//Read in command from client
	end_of_line = 0;
	while(end_of_line == 0)
	{
		startptr = &amp;c-&gt;buf[c-&gt;curpos];
		len = read(c-&gt;soc, startptr, MAXLINE - c-&gt;curpos);
		if(len &lt;= 0) 
		{
			if(len == -1) {
				perror(&quot;read on socket&quot;);
			}
			return -1;
			/* connection closed by client */
		} 
		else 
		{
			c-&gt;curpos += len;
			c-&gt;buf[c-&gt;curpos] = '\0';
	
			/* Did we get a whole line?*/
			if (strchr(c-&gt;buf, '\n') || strchr(c-&gt;buf, '\r') || c-&gt;curpos == 255) 
			{
				end_of_line = 1;

				/* Tokenize arguments */
		 		 int cmd_argc;
		 		 char *cmd_argv[INPUT_ARG_MAX_NUM];
        		 char *next_token = strtok(c-&gt;buf, DELIM);
      		 cmd_argc = 0;
      		 while (next_token != NULL) {
           		 if (cmd_argc &gt;= INPUT_ARG_MAX_NUM - 1) {
           			perror(&quot;Too many arguments!&quot;);
                	cmd_argc = 0;
               	break;
            	}
           	 	cmd_argv[cmd_argc] = next_token;
           	 	cmd_argc++;
            	next_token = strtok(NULL, DELIM);
        		}
        		cmd_argv[cmd_argc] = NULL;
        
        		//pass command to process_args
        		int ret = process_args(cmd_argc, cmd_argv, group_list, c-&gt;soc, c, clients);
        		if (ret == -1)
        		{
					return -1;        		
        		}

       		printf(&quot;&gt;&quot;);
			}
		}
	}
	char *leftover = &amp;c-&gt;buf[c-&gt;curpos];
	memmove(c-&gt;buf, leftover, c-&gt;curpos);
	c-&gt;curpos = 0;
	return 0;
}


int main()
{
	/* Initialize the list head */
    Group *group_list = NULL;
    
	int i, maxfd, listenfd, connfd, maxi;
	int nready;
	Client client[MAXCLIENTS];
	fd_set rset, allset;
	socklen_t clilen;
	struct sockaddr_in cliaddr, servaddr;
	int yes = 1;
	
	/* set up listening socket listenfd */
	listenfd = Socket(AF_INET, SOCK_STREAM, 0);	
	
	bzero(&amp;servaddr, sizeof(servaddr));
	servaddr.sin_family = AF_INET;
	servaddr.sin_addr.s_addr = htonl(INADDR_ANY);
	servaddr.sin_port = htons(PORT);
	
	// Make sure we can reuse the port immediately after the
   // server terminates.
	if((setsockopt(listenfd, SOL_SOCKET, SO_REUSEADDR, &amp;yes, 
                                           sizeof(int))) == -1) {
		perror(&quot;setsockopt&quot;);
	}
	
	Bind(listenfd, (struct sockaddr *) &amp;servaddr, sizeof(servaddr));
	Listen(listenfd, LISTENQ);
	
	maxfd = listenfd;   /* initialize */
	for (i = 0; i &lt; MAXCLIENTS; i++) {
		client[i].soc = -1; /* -1 indicates available entry */
		client[i].curpos = 0;
		client[i].asked_username = 0;
		client[i].user_name = malloc(MAXLINE);
	}
	
	FD_ZERO(&amp;allset);
	FD_SET(listenfd, &amp;allset);
	
	for ( ; ; ) {
		rset = allset;      /* structure assignment */
		nready = Select(maxfd+1, &amp;rset, NULL, NULL, NULL);

		if (FD_ISSET(listenfd, &amp;rset)) {    /* new client connection */
			clilen = sizeof(cliaddr);
			connfd = Accept(listenfd, (struct sockaddr *) &amp;cliaddr, &amp;clilen);
			printf(&quot;Accepted a new client\n&quot;);
         
			for (i = 0; i &lt; MAXCLIENTS; i++)
			if (client[i].soc &lt; 0) {
				client[i].soc = connfd; /* save descriptor */
				write(client[i].soc, &quot;What is your name?\r\n&quot;, strlen(&quot;What is your name?\r\n&quot;)); //write to client. what is your name?
				break;
			}
			if (i == MAXCLIENTS) 
				printf(&quot;Too many clients&quot;);

			FD_SET(connfd, &amp;allset);    /* add new descriptor to set */
			if (connfd &gt; maxfd)
				maxfd = connfd; /* for select */
			if (i &gt; maxi)
				maxi = i;   /* max index in client[] array */
 
			if (--nready &lt;= 0)
				continue;   /* no more readable descriptors */

		}

		for (i = 0; i &lt; MAXCLIENTS; i++) {   /* check all clients for data */
			if (client[i].soc &lt; 0)
				continue;
			if (FD_ISSET(client[i].soc, &amp;rset)) {
				int result = readfromclient(&amp;client[i], &amp;group_list, client);
				
				if(result == -1)  {
					Close(client[i].soc);
					FD_CLR(client[i].soc, &amp;allset);
					client[i].soc = -1;
                                        client[i].asked_username = 0;
					free(client[i].user_name);
				}
				if (--nready &lt;= 0)
					break;  /* no more readable descriptors */
			}
		}
	}
	
    return 0;
}
</pre>
</body>
</html>
