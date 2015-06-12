Stratio Referer Paser Morphline
=======================

The Stratio refererParser is based on [Snowplow Referer Parser] (https://github.com/snowplow/referer-parser) with 
some 
improvements. It's able to parse both organic links as paid links.

The command has 3 input field:

- **uri**: header name that contains the referer URI you want to parse.
- **pageHost**: header name that contains the page URI where referer is linked to.
- **internalHosts**: comma separated list that contains optional hosts to be considered as internal sources.


- **source** (optional): optional output field name to store referer source. By default, the value will be stored 
in "source" header.
- **medium** (optional): optional output field name to store referer medium. By default, the value will be stored 
in "medium" header.
- **term** (optional): optional output field name to store referer terms. By default, the value will be stored 
in "term" header.
- **campaign** (optional): optional output name field to store referer campaign. By default, the value will be stored 
in "campaign" header.
- **content** (optional): optional output name field to store referer content.By default, the value will be stored 
in "content" header.


Example:


    {
	    refererParser {
            uri: referer_header
            pageHost: request_header
            internalHosts: [www.example1.com, www.example3.com]
	    }
	}

For example, if the referer_header contains "http://yourdomain.com/yourproduct
.html?utm_source=yoursite&utm_medium=postfooter&utm_campaign=product&utm_term=term1+term2+term3" and the 
request_header contains "www.example.com" then the output field will contain:

- **source**: yoursite
- **medium**: postfooter
- **term**: term1 term2 term3
- **campaign**: product
- **content**: null