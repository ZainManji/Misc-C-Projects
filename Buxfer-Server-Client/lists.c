<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
       "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<body>
<pre>#include &lt;stdio.h&gt;
#include &lt;stdlib.h&gt;
#include &lt;string.h&gt;
#include &quot;lists.h&quot;

/* Add a group with name group_name to the group_list referred to by
* group_list_ptr. The groups are ordered by the time that the group was
* added to the list with new groups added to the end of the list.
*
* Returns 0 on success and -1 if a group with this name already exists.
*
* (I.e, allocate and initialize a Group struct, and insert it
* into the group_list. Note that the head of the group list might change
* which is why the first argument is a double pointer.)
*/
char *add_group(Group **group_list_ptr, const char *group_name) {

    char *valid_return_msg = malloc(strlen(group_name) + 15);
    int j = 0;
    j = snprintf(valid_return_msg, strlen(group_name) + 15, &quot;%s&quot;, &quot;Group &quot;);
    j += snprintf(valid_return_msg+j, strlen(group_name) + 15, &quot;%s&quot;, group_name);
    j += snprintf(valid_return_msg+j, strlen(group_name) + 15, &quot;%s&quot;, &quot; added\r\n\0&quot;);
    char *invalid_return_msg = malloc(strlen(group_name) + 24);
    j = snprintf(invalid_return_msg, strlen(group_name) + 24, &quot;%s&quot;, &quot;Group &quot;);
    j += snprintf(invalid_return_msg+j, strlen(group_name) + 24, &quot;%s&quot;, group_name);
    j += snprintf(invalid_return_msg+j, strlen(group_name) + 24, &quot;%s&quot;, &quot; already exists\r\n\0&quot;);

   if (find_group(*group_list_ptr, group_name) == NULL) {
        //malloc space for new group
        Group *newgrp;
        if ((newgrp = malloc(sizeof(Group))) == NULL) {
           perror(&quot;Error allocating space for new Group&quot;);
           exit(1);
        }
        // set the fields of the new group node
        // first allocate space for the name
        int needed_space = strlen(group_name) + 1;
        if (( newgrp-&gt;name = malloc(needed_space)) == NULL) {
           perror(&quot;Error allocating space for new Group name&quot;);
           exit(1);
        }
        strncpy(newgrp-&gt;name, group_name, needed_space);
        newgrp-&gt;users = NULL;
        newgrp-&gt;xcts = NULL;
        newgrp-&gt;next = NULL;


        // Need to insert into the end of the list not the front
        if (*group_list_ptr == NULL) {
          // set new head to this new group -- the list was previously empty
          *group_list_ptr = newgrp;
          return valid_return_msg;
        }  else {
        // walk along the list until we find the currently last group
          Group * current = *group_list_ptr;
          while (current-&gt;next != NULL) {
            current = current-&gt;next;
          }
          // now current should be the last group
          current-&gt;next = newgrp;
          return valid_return_msg;
        }
    } else {
            return invalid_return_msg;
    }
}

/* Print to standard output the names of all groups in group_list, one name
*  per line. Output is in the same order as group_list.
*/
char * list_groups(Group *group_list) {

    int str_size = 0;
    Group * current = group_list;
    while (current != NULL)
    {
        str_size = str_size + strlen(current-&gt;name) + strlen(&quot;\r\n&quot;);
        current = current-&gt;next;
    }
    str_size = str_size + 1;
    char *return_str = malloc(str_size + 1);
    int j = 0;
    current = group_list;
    while (current != NULL) {
        j += snprintf(return_str+j, str_size + 1, &quot;%s&quot;, current-&gt;name);
        j += snprintf(return_str+j, str_size + 1, &quot;%s&quot;, &quot;\r\n&quot;);
        current = current-&gt;next;
    }
    j += snprintf(return_str+j, str_size + 1, &quot;%s&quot;, &quot;\0&quot;);

    return return_str;
}

/* Search the list of groups for a group with matching group_name
* If group_name is not found, return NULL, otherwise return a pointer to the
* matching group list node.
*/
Group *find_group(Group *group_list, const char *group_name) {
    Group *current = group_list;
    while (current != NULL &amp;&amp; (strcmp(current-&gt;name, group_name) != 0)) {
        current = current-&gt;next;
    }
    return current;
}

/* Add a new user with the specified user name to the specified group. Return zero
* on success and -1 if the group already has a user with that name.
* (allocate and initialize a User data structure and insert it into the
* appropriate group list)
*/
char *add_user(Group *group, const char *user_name) {

	 int j = 0;
    char *valid_return_msg = malloc(strlen(group-&gt;name) + strlen(user_name) + 36);
    int sizeofstr = strlen(group-&gt;name) + strlen(user_name) + 36;
    j = snprintf(valid_return_msg, sizeofstr, &quot;%s&quot;, &quot;Successfully added &quot;);
    j += snprintf(valid_return_msg+j, sizeofstr, &quot;%s&quot;, user_name);
    j += snprintf(valid_return_msg+j, sizeofstr, &quot;%s&quot;, &quot; to the group &quot;);
    j += snprintf(valid_return_msg+j, sizeofstr, &quot;%s&quot;, group-&gt;name);
    j += snprintf(valid_return_msg+j, sizeofstr, &quot;%s&quot;, &quot;\r\n\0&quot;);
	
    char *invalid_return_msg = malloc(strlen(group-&gt;name) + strlen(user_name) + 24);
    sizeofstr = strlen(group-&gt;name) + strlen(user_name) + 24;
    j = snprintf(invalid_return_msg, sizeofstr, &quot;%s&quot;, user_name);
    j += snprintf(invalid_return_msg+j, sizeofstr, &quot;%s&quot;, &quot; is already in group &quot;);
    j += snprintf(invalid_return_msg+j, sizeofstr, &quot;%s&quot;, group-&gt;name);
    j += snprintf(invalid_return_msg+j, sizeofstr, &quot;%s&quot;, &quot;\r\n\0&quot;);

    User *this_user = find_prev_user(group, user_name);
    if (this_user != NULL) {
        return invalid_return_msg;
    }
    // ok to add a user to this group by this name
    // since users are stored by balance order and the new user has 0 balance
    // he goes first
    User *newuser;
    if ((newuser = malloc(sizeof(User))) == NULL) {
        perror(&quot;Error allocating space for new User&quot;);
        exit(1);
    }
    // set the fields of the new user node
    // first allocate space for the name
    int name_len = strlen(user_name);
    if ((newuser-&gt;name = malloc(name_len + 1)) == NULL) {
        perror(&quot;Error allocating space for new User name&quot;);
        exit(1);
    }
    strncpy(newuser-&gt;name, user_name, name_len + 1);
    newuser-&gt;balance = 0.0;
    // insert this user at the front of the list
    newuser-&gt;next = group-&gt;users;
    group-&gt;users = newuser;
    return valid_return_msg;
}

/* Print to standard output the names and balances of all the users in group,
* one per line, and in the order that users are stored in the list, namely
* lowest payer first.
*/
char * list_users(Group *group) {
	
	/* List users will never overwrite the end of a character array because I first iterate through all the users
	* in the passed in group and add up the size of their user names and also take into account the newline and return carriage characters
	* size. Also I calculate the size of their balances as a string and take this calculation to account when finding the total size of the
	* character array should be. Once I've calculated the total size, I allocate enough space into a character array to make sure that the 
	* character array won't be overwritten.
	*/

    int str_size = 0;
    User *current_user = group-&gt;users;
    int double_len = 0;
    int temp = 0;

    while (current_user != NULL)
    {
        double_len = 0;
        temp = current_user-&gt;balance;
        if (temp &lt; 0)
        {
            temp = -temp;
        }
        else if (temp == 0)
        {
            double_len = 1;
        }

        while(temp != 0)
        {
            double_len = double_len + 1;
            temp = temp/10;
        }

        str_size = str_size + strlen(current_user-&gt;name) + strlen(&quot;: &quot;) + double_len + 3 + strlen(&quot;\r\n&quot;);
        current_user = current_user-&gt;next;
    }

    char* return_str = malloc(str_size + 1);
    current_user = group-&gt;users;
    int j = 0;
    char * double_str = malloc(double_len+3+1);
    
    while (current_user != NULL) {
        snprintf(double_str, double_len+3+1, &quot;%f&quot;, current_user-&gt;balance);
        j += snprintf(return_str+j, str_size + 1, &quot;%s&quot;, current_user-&gt;name);
        j += snprintf(return_str+j, str_size + 1, &quot;%s&quot;, &quot;: &quot;);
        j += snprintf(return_str+j, str_size + 1, &quot;%s&quot;, double_str);
        j += snprintf(return_str+j, str_size + 1, &quot;%s&quot;, &quot;\r\n&quot;);
        current_user = current_user-&gt;next;
    }
    j += snprintf(return_str+j, str_size + 1, &quot;%s&quot;, &quot;\0&quot;);
    return return_str;
}

/* Print to standard output the balance of the specified user. Return 0
* on success, or -1 if the user with the given name is not in the group.
*/
char *user_balance(Group *group, const char *user_name) {

    int str_size = (11 + strlen(&quot;\r\n&quot;));
    int double_len = 0;
    int temp = 0;

    User * prev_user = find_prev_user(group, user_name);
    int j = 0;

    char * invalid_return_str = malloc(strlen(user_name) + strlen(group-&gt;name) + 25);
    j = snprintf(invalid_return_str, strlen(user_name) + strlen(group-&gt;name) + 25, &quot;%s&quot;, &quot;User &quot;);
    j += snprintf(invalid_return_str+j, strlen(user_name) + strlen(group-&gt;name) + 25, &quot;%s&quot;, user_name);
    j += snprintf(invalid_return_str+j, strlen(user_name) + strlen(group-&gt;name) + 25, &quot;%s&quot;, &quot; is not in group &quot;);
    j += snprintf(invalid_return_str+j, strlen(user_name) + strlen(group-&gt;name) + 25, &quot;%s&quot;, group-&gt;name);
    j += snprintf(invalid_return_str+j, strlen(user_name) + strlen(group-&gt;name) + 25, &quot;%s&quot;, &quot;\r\n\0&quot;);

    char *valid_return_str;

    if (prev_user == NULL) {
        return invalid_return_str;
    }

    if (prev_user == group-&gt;users) {
        if (strcmp(user_name, prev_user-&gt;name) == 0)
        {
            double_len = 0;
            temp = prev_user-&gt;balance;
            if (temp &lt; 0)
            {
                temp = -temp;
            }
            else if (temp == 0)
            {
                double_len = 1;
            }

            while(temp != 0)
            {
                double_len = double_len + 1;
                temp = temp/10;
            }
        }
        str_size = (str_size + double_len + 1 + 3);
        valid_return_str = malloc(str_size);
    }

	 char * double_str = malloc(double_len+3+1);
	 j = 0;

    prev_user = find_prev_user(group, user_name);
    if (prev_user == group-&gt;users) {
        // user could be first or second since previous is first
        if (strcmp(user_name, prev_user-&gt;name) == 0) {
            // this is the special case of first user
            snprintf(double_str, double_len+3+1, &quot;%f&quot;, prev_user-&gt;balance);
            j = snprintf(valid_return_str, str_size, &quot;%s&quot;, &quot;Balance is &quot;);
            j += snprintf(valid_return_str+j, str_size, &quot;%s&quot;, double_str);
            j += snprintf(valid_return_str+j, str_size, &quot;%s&quot;, &quot;\r\n\0&quot;);
            return valid_return_str;
        }
    }

    double_len = 0;
    temp = prev_user-&gt;next-&gt;balance;
    if (temp &lt; 0)
    {
        temp = -temp;
    }
    else if (temp == 0)
    {
        double_len = 1;
    }

    while(temp != 0)
    {
        double_len = double_len + 1;
        temp = temp/10;
    }
    str_size = (str_size + double_len + 1+3);
    free(double_str);
    snprintf(double_str, double_len+3+1, &quot;%f&quot;, prev_user-&gt;next-&gt;balance);
    valid_return_str = malloc(str_size);
    j = 0;
    j = snprintf(valid_return_str, str_size, &quot;%s&quot;, &quot;Balance is &quot;);
    j += snprintf(valid_return_str+j, str_size, &quot;%s&quot;, double_str);
    j += snprintf(valid_return_str+j, str_size, &quot;%s&quot;, &quot;\r\n\0&quot;);
    //printf(&quot;Balance is %f\n&quot;, prev_user-&gt;next-&gt;balance);
    return valid_return_str;
}

/* Return a pointer to the user prior to the one in group with user_name. If
* the matching user is the first in the list (i.e. there is no prior user in
* the list), return a pointer to the matching user itself. If no matching user
* exists, return NULL.
*
* The reason for returning the prior user is that returning the matching user
* itself does not allow us to change the user that occurs before the
* matching user, and some of the functions you will implement require that
* we be able to do this.
*/
User *find_prev_user(Group *group, const char *user_name) {
    User * current_user = group-&gt;users;
    // return NULL for no users in this group
    if (current_user == NULL) {
        return NULL;
    }
    // special case where user we want is first
    if (strcmp(current_user-&gt;name, user_name) == 0) {
        return current_user;
    }
    while (current_user-&gt;next != NULL) {
        if (strcmp(current_user-&gt;next-&gt;name, user_name) == 0) {
            // we've found the user so return the previous one
            return current_user;
        }
    current_user = current_user-&gt;next;
    }
    // if we get this far without returning, current_user is last,
    // and we have already checked the last element
    return NULL;
}

/* Add the transaction represented by user_name and amount to the appropriate
* transaction list, and update the balances of the corresponding user and group.
* Note that updating a user's balance might require the user to be moved to a
* different position in the list to keep the list in sorted order. Returns 0 on
* success, and -1 if the specified user does not exist.
*/
char * add_xct(Group *group, const char *user_name, double amount) {
    User *this_user;
    User *prev = find_prev_user(group, user_name);
    char * invalid_return_str = malloc(strlen(user_name) + 23);
    int j = 0;
    j = snprintf(invalid_return_str, strlen(user_name) + 23, &quot;%s&quot;, &quot;User &quot;);
    j += snprintf(invalid_return_str+j, strlen(user_name) + 23, &quot;%s&quot;, user_name);
    j += snprintf(invalid_return_str+j, strlen(user_name) + 23, &quot;%s&quot;, &quot; does not exist\r\n\0&quot;);
    int double_len;
    int temp;
    int str_size;
    char * valid_return_str;
    double_len = 0;
    temp = amount;
    if (temp &lt; 0)
    {
        temp = -temp;
    }
    else if (temp == 0)
    {
        double_len = 1;
    }

    while(temp != 0)
    {
        double_len = double_len + 1;
        temp = temp/10;
    }
    
    char * double_str = malloc(double_len+3+1);
    snprintf(double_str, double_len+3+1, &quot;%f&quot;, amount);
	 j = 0;
    str_size = 21 + 13 + 16 + strlen(user_name);
    str_size = (str_size + double_len + 1 + 3); // Transaction for user-name with amount __ successfuly added
    valid_return_str = malloc(str_size);
    j = snprintf(valid_return_str, str_size, &quot;%s&quot;, &quot;Transaction for &quot;);
    j += snprintf(valid_return_str+j, str_size, &quot;%s&quot;, user_name);
    j += snprintf(valid_return_str+j, str_size, &quot;%s&quot;, &quot; with amount &quot;);
    j += snprintf(valid_return_str+j, str_size, &quot;%s&quot;, double_str);
    j += snprintf(valid_return_str+j, str_size, &quot;%s&quot;, &quot; successfully added\r\n\0&quot;);


    if (prev == NULL) {
        return invalid_return_str;
    }
    // but find_prev_user gets the PREVIOUS user, so correct
    if (prev == group-&gt;users) {
        // user could be first or second since previous is first
        if (strcmp(user_name, prev-&gt;name) == 0) {
            // this is the special case of first user
            this_user = prev;
        } else {
            this_user = prev-&gt;next;
        }
    } else {
        this_user = prev-&gt;next;
    }

    Xct *newxct;
    if ((newxct = malloc(sizeof(Xct))) == NULL) {
        perror(&quot;Error allocating space for new Xct&quot;);
        exit(1);
    }
    // set the fields of the new transaction node
    // first allocate space for the name
    int needed_space = strlen(user_name) + 1;
    if ((newxct-&gt;name = malloc(needed_space)) == NULL) {
         perror(&quot;Error allocating space for new xct name&quot;);
         exit(1);
    }
    strncpy(newxct-&gt;name, user_name, needed_space);
    newxct-&gt;amount = amount;

    // insert this xct  at the front of the list
    newxct-&gt;next = group-&gt;xcts;
    group-&gt;xcts = newxct;

    // first readjust the balance
    this_user-&gt;balance = this_user-&gt;balance + amount;

    // since we are only ever increasing this user's balance they can only
    // go further towards the end of the linked list
    //   So keep shifting if the following user has a smaller balance

    while (this_user-&gt;next != NULL &amp;&amp;
                  this_user-&gt;balance &gt; this_user-&gt;next-&gt;balance ) {
        // he remains as this user but the user next gets shifted
        // to be behind him
        if (prev == this_user) {
            User *shift = this_user-&gt;next;
            this_user-&gt;next = shift-&gt;next;
            prev = shift;
            prev-&gt;next = this_user;
            group-&gt;users = prev;
        } else { // ordinary case in the middle
            User *shift = this_user-&gt;next;
            prev-&gt;next = shift;
            this_user-&gt;next = shift-&gt;next;
            shift-&gt;next = this_user;
        }
    }
	return valid_return_str;
}

</pre>
</body>
</html>
