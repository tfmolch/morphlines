Stratio GeoIp Morphline
***********************

The stratio geoIP command works as the `kite one`_. It will save the iso code and the longitude-latitude pair in two header fields.

Example::


    {
      sgeoIP {
        input : log_host
        database : "/home/path/flume/GeoLite2-City.mmdb"
        isoCodeOutput : log_iso_code
        longitudeLatituteOutput : log_longitude_latitude
      }
    }


.. _kite one: http://kitesdk.org/docs/0.12.0/kite-morphlines/morphlinesReferenceGuide.html#/geoIP)