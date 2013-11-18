<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
       "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<body>
<pre>#include &lt;stdio.h&gt;
#include &lt;stdlib.h&gt;
#include &lt;string.h&gt;
#include &quot;lists.h&quot;

#define INPUT_BUFFER_SIZE 256
#define INPUT_ARG_MAX_NUM 5
#define DELIM &quot; \n&quot;


/* A standard template for error messages */
void error(const char *msg) {
    fprintf(stderr, &quot;Error: %s\n&quot;, msg);
}

/* 
 * Read and process buxfer commands
 */
int process_args(int cmd_argc, char **cmd_argv, Group **group_list_addr) {
    Group *group_list = *group_list_addr; 
    Group *g;

    if (cmd_argc &lt;= 0) {
        return 0;
    } else if (strcmp(cmd_argv[0], &quot;quit&quot;) == 0 &amp;&amp; cmd_argc == 1) {
        return -1;
        
    } else if (strcmp(cmd_argv[0], &quot;add_group&quot;) == 0 &amp;&amp; cmd_argc == 2) {
        if (add_group(group_list_addr, cmd_argv[1]) == -1) {
            error(&quot;Group already exists&quot;);
        }
        
    } else if (strcmp(cmd_argv[0], &quot;list_groups&quot;) == 0 &amp;&amp; cmd_argc == 1) {
        list_groups(group_list);
        
    } else if (strcmp(cmd_argv[0], &quot;add_user&quot;) == 0 &amp;&amp; cmd_argc == 3) {
        if ((g = find_group(group_list, cmd_argv[1])) == NULL) {
            error(&quot;Group does not exist&quot;);
        } else {
            if (add_user(g, cmd_argv[2]) == -1) {
                error(&quot;User already exists&quot;);
            }
        }
        
    } else if (strcmp(cmd_argv[0], &quot;list_users&quot;) == 0 &amp;&amp; cmd_argc == 2) {
        if ((g = find_group(group_list, cmd_argv[1])) == NULL) {
            error(&quot;Group does not exist&quot;);
        } else {
            list_users(g);
        }
        
    } else if (strcmp(cmd_argv[0], &quot;user_balance&quot;) == 0 &amp;&amp; cmd_argc == 3) {
        if ((g = find_group(group_list, cmd_argv[1])) == NULL) {
            error(&quot;Group does not exist&quot;);
        } else {
            if (user_balance(g, cmd_argv[2]) == -1) {
                error(&quot;User does not exist&quot;);
            }
        }
        
    } else if (strcmp(cmd_argv[0], &quot;add_xct&quot;) == 0 &amp;&amp; cmd_argc == 4) {
        if ((g = find_group(group_list, cmd_argv[1])) == NULL) {
            error(&quot;Group does not exist&quot;);
        } else {
            char *end;
            double amount = strtod(cmd_argv[3], &amp;end);
            if (end == cmd_argv[3]) {
                error(&quot;Incorrect number format&quot;);
            } else {
                if (add_xct(g, cmd_argv[2], amount) == -1) {
                    error(&quot;User does not exist&quot;);
                }
            }
        }

    } else {
        error(&quot;Incorrect syntax&quot;);
    }
    return 0;
}

int main(int argc, char* argv[]) {
    char input[INPUT_BUFFER_SIZE];
    char *cmd_argv[INPUT_ARG_MAX_NUM];
    int cmd_argc;
    FILE *input_stream;

    /* Initialize the list head */
    Group *group_list = NULL;

    /* Batch mode */
    if (argc == 2) {
        input_stream = fopen(argv[1], &quot;r&quot;);
        if (input_stream == NULL) {
            error(&quot;Error opening file&quot;);
            exit(1);
        }
    }
    /* Interactive mode */
    else {
        input_stream = stdin;
    }

    printf(&quot;Welcome to Buxfer!\nPlease input command:\n&gt;&quot;);
    
    while (fgets(input, INPUT_BUFFER_SIZE, input_stream) != NULL) {
        /* Echo line if in batch mode */
        if (argc == 2) {
            printf(&quot;%s&quot;, input);
        }
        /* Tokenize arguments */
        char *next_token = strtok(input, DELIM);
        cmd_argc = 0;
        while (next_token != NULL) {
            if (cmd_argc &gt;= INPUT_ARG_MAX_NUM - 1) {
                error(&quot;Too many arguments!&quot;);
                cmd_argc = 0;
                break;
            }
            cmd_argv[cmd_argc] = next_token;
            cmd_argc++;
            next_token = strtok(NULL, DELIM);
        }
        cmd_argv[cmd_argc] = NULL;
        if (cmd_argc &gt; 0 &amp;&amp; process_args(cmd_argc, cmd_argv, &amp;group_list) == -1) {
            break; /* quit command was entered */
        }
        printf(&quot;&gt;&quot;);
    }

    /* Close file if in batch mode */
    if (argc == 2) {
        fclose(input_stream);
    }
    return 0;
}
</pre>
</body>
</html>
