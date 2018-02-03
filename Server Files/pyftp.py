from pyftpdlib.authorizers import DummyAuthorizer
from pyftpdlib.handlers import FTPHandler
from pyftpdlib.servers import FTPServer

authorizer = DummyAuthorizer()
authorizer.add_user("erkan", "12345", "/home/ec2-user/myproject/FTPServer/", perm="elradfmwMT")
#authorizer.add_anonymous("/home/ec2-user/myproject/FTPServer/nobody/")

handler = FTPHandler
handler.authorizer = authorizer
handler.banner = "pyftpdlib based ftpd ready."
handler.passive_ports = range(40000, 65535)

address = ('ec2-13-59-72-180.us-east-2.compute.amazonaws.com', 1025)

server = FTPServer(address,handler)
server.max_cons = 256
server.max_cons_per_ip = 5

# start ftp server
server.serve_forever()


if __name__ == "__main__":
    main()