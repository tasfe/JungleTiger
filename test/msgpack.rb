require 'socket'      # Sockets are in standard library  
require 'msgpack'

s = TCPSocket.open("172.19.102.12", 44444)  

msg = [1, "hello"]
buffer = MessagePack.pack(msg)

s.write([8].pack("n"));
s.write(buffer);

str = s.recv(4)
puts str

s.close 
