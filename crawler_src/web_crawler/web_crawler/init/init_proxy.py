""" The module for parsing the proxy list file into a list of proxy objects 

    Attributes:
        _LOGGER_NAME: The name of the logger to use for this module
"""

from ..helpers import Config

_LOGGER_NAME = 'init'


class Proxy:
    """ A simple class representing one proxy
    Attributes:
        logger: The logger object for logging
        host: The ip address of the proxy host
        port: The port number of the proxy host
    """

    def __init__(self, string):
        """ Initialize the fields of one proxy server
        Args:
            string: A line in the proxy list file
        """
        self.logger = Config.get_logger(_LOGGER_NAME)
        fields = string.strip().split(',')
        if len(fields) != 2:
            self.logger.critical(f'Line {string.strip()} has incorrect number of fields in proxy list file: {len(fields)}')
        else:
            self.host = fields[0]
            self.port = fields[1]

    @property
    def address(self):
        """ Returns: The proxy server address """
        return f'http://{self.host}:{self.port}'

    def __repr__(self):
        """ Returns: the string representing the fields of proxy, separated by new lines """
        res_list = []
        res_list.append(f'host: {self.host}')
        res_list.append(f'port: {self.port}')
        return '\n'.join(res_list)


def init_proxy():
    """ Read proxy in the file (generator of Proxy objects)
    Args:
        config: Parsed config object (dict-like)
        logger: A logger corresponding to the current module
    Yields:
        A sequence of Proxy objects to use for web crawling
    """
    file_path = Config.config['init_files']['proxy_file']
    with open(file_path) as file:
        for line in file:
            # ignore empty line
            if line == '\n':
                continue
            yield Proxy(line)
