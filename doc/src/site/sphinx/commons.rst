Commons
*******

Calculator
==========

The calculator command makes computations between fields or literals and save result into a new field as BigDecimal.

* **leftOperand**: Field or literal which will be left operand.
* **rightOperand**: Field or literal which will be right operand.
* **operator**: Operator. Possible values: +,-,*,/,%,sqrt,^
* **output**: Output field. Default: output

Example::

  {
      {
        calculator {
          leftOperand : metric
          operator : "/"
          rightOperand : 2
          output : "result"
        }
      }
  }


FieldFilter
===========

The FieldFilter command filter fields including or excluding them.

Example::

    {
        {
          fieldFilter {
            excludeFields : [field1, field2]
            includeFields : [field3, field4]
          }
        }
    }


ReadXml
=======

The readXml command parses an InputStream from field specified by field parameter (_attachment_body by default) and uses XPath expressions to extract fields and add them into headers.

Example::

    {
        readXml {
           field : source
           paths : {
             book1 : "/catalog/book[@id='bk101']/author"
             book2 : "/catalog/book[@id='bk102']/genre"
           }
        }
    }


If paths field is empty (paths : { } ) whole xml will be parsed into a String with name _xml.


Relationalfilter
================

The relationalFilter command discard records that are not accomplish a relational condition.

* **field**: Name of field which value will be compared.
* **operator**: Operator. Possible values: <, <=, >, >=, ==, <>.
* **reference**: Number to compare to.
* **class**: Class to cast reference number.

Example::

    {
        relationalFilter {
            field : field1
            operator : >
            reference : 100.0
            class : java.lang.Double
        }
    }


Rename
======

The rename command simply renames fields in morphline records. It takes two parameters.

* **remove**: boolean that indicates if old entry must be removed after rename.
* **fields**: `com.typesafe.config.Config`_ where each entry indicates old name and new name.

Example::

    {
         rename {
            remove : true
            fields {
              field1 : newfield1
              field2 : newfield2
            }
         }
    }


HeadersToBody
=============

The headersToBody is a command that write in your _attachment_body field all your headers but excluded in JSON
format.  Warning: This morphline REPLACE your _attachment_body field but maintains the headers.

Example::

  {
      {
        headersToBody {
          excludeFields : [field2, field5]
      }
   }


TimeFilter
==========

The timeFilter command discard records that are not between two specified dates.

* **field**: Name of field which include date value.
* **dateFormat**: Pattern for a date. See `SimpleDateFormat`_.
* **from**: Initial date. (Exclusive).
* **to**: End date. (Exclusive).

Example::

    {
        timeFilter {
            field : createdAt
            dateFormat : "dd/MM/yyyy"
            from : "20/01/2014"
            to : "20/01/2015"
        }
    }


ContainsAnyOf
=============

The containsAnyOf is a command that succeeds if all field values of the given named fields contains any of the given values and fails otherwise. Multiple fields can be named, in which case a logical AND is applied to the results.
In following example succeeds if value of app field is "one", "two" or "three"::

    {
       {
         containsAnyOf {
           app : [one, two, three]
       }
    }


.. _SimpleDateFormat:  http://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
.. _com.typesafe.config.Config : http://typesafehub.github.io/config/latest/api/com/typesafe/config/Config.html
