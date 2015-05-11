require 'socket'      # Sockets are in standard library  
require 'msgpack'

s = TCPSocket.open("127.0.0.1", 44445)  

msg = [1, "hello"]
buffer = MessagePack.pack(msg)

s.write([8].pack("n"));
s.write(buffer);

str = s.recv(4)
puts str

s.close 
