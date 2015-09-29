Stratio LDAP Morphline
=======================

The stratio parseLDAP command extract RDN's from an LDAP String into separated headers. If a resulting header have more
than one value, these values will be joined in a comma-separated list string.

Example:

``` 
{
  parseLDAP {
    input : ldap
  }
}
```

Input:

```
    {
        ldap = cn=dsameuser,ou=DSAME Users,dc=pre,dc=company,dc=id
    }
```

Output:

```
{
  ldap = cn=dsameuser,ou=DSAME Users,dc=pre,dc=company,dc=id
  cn = dsameuser
  ou = DSAME Users
  dc = id,company,pre
}
```




