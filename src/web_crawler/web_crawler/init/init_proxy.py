""" The module for parsing the proxy list file into a list of proxy objects """

from .. import helpers

LOGGER_NAME = 'init'

class Proxy:
    """ A simple class representing one proxy 
    Attributes:
        logger: The logger object for logging
        ip: The ip address of the proxy host
        port: The port number of the proxy host
    """

    def __init__(self, string, logger):
        """ Initialize the fields of one proxy server 
        Args:
            string: A line in the proxy list file
            logger: A logger object to used in this module
        """
        self.logger = logger
        fields = string.strip().split(',')
        if len(fields) != 2:
            self.logger.critical(f'line {string.strip()} has incorrect number of fields in proxy list file: {len(fields)}')
        else:
            self.ip = fields[0]
            self.port = fields[1]
    
    @property
    def address(self):
        """ The proxy server address """
        return f'http://{self.ip}:{self.port}'
    
    def __repr__(self):
        """ Return the string representing the fields of proxy, separated by new lines """
        res_list = []
        res_list.append(f'ip: {self.ip}')
        res_list.append(f'port: {self.port}')
        return '\n'.join(res_list)


def init_proxy():
    """ Read proxy in the file and generate a proxy list 
    Args:
        config: Parsed config object (dict-like)
        logger: A logger corresponding to the current module
    """
    config, logger = helpers.setup_config_logger(LOGGER_NAME)
    file_path = config['init_files']['proxy_file']
    with open(file_path) as f:
        for line in f:
            # ignore empty line
            if line == '\n':
                continue
            yield Proxy(line, logger)