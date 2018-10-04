""" The module for parsing the proxy list file into a list of proxy objects """

import logging
import configparser
from .. import helpers

LOGGER_NAME = 'init'

class Proxy:
    """ A simple class representing one proxy """

    def __init__(self, string, logger):
        """ Initialize the fields of one proxy server 
        Args:
            string: A line in the proxy list file
            logger: A logger object to used in this module
        """
        self.logger = logger
        fields = string.strip().split(',')
        if len(fields) != 5:
            self.logger.debug(f'line {string} has incorrect number of fields in proxy list file: {len(fields)}')
        self.ip = fields[0]
        self.num1 = fields[1]
        self.num2 = fields[2]
        self.username = fields[3]
        self.password = fields[4]
    
    def __repr__(self):
        """ Return the string representing the fields of proxy, separated by new lines """
        res_list = []
        res_list.append(f'ip: {self.ip}')
        res_list.append(f'num1: {self.num1}')
        res_list.append(f'num2: {self.num2}')
        res_list.append(f'username: {self.username}')
        res_list.append(f'password: {self.password}')
        return '\n'.join(res_list)


def init_proxy():
    """ Read proxy in the file and generate a proxy list 
    Args:
        config: Parsed config object (dict-like)
        logger: A logger corresponding to the current module
    """
    config, logger = helpers.setup_config_logger(LOGGER_NAME)
    file_path = config['init_files']['proxy_file']
    proxy_list = []
    with open(file_path) as f:
        for line in f:
            # ignore empty line
            if line == '\n':
                continue
            proxy_list.append(Proxy(line, logger))
    return proxy_list