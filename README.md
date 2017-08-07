# UdpStreamListener
  This is a test project for listen to udp stream in a GELF format and extract to case class

# simple way to create and a udp message :
  echo -n '{ "version": "1.1", "host": "example.org", "short_message": "A short message", "level": 5, "_some_info": "foo" }' | nc -w0 -u 127.0.0.1 9876
