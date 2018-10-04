""" Helper functions for parsing the configuration file and generate logger """

import configparser
import logging
import os
import socket

# find the absolute config file path
if socket.gethostname() == 'ymhuang':
    CONFIG_PATH = os.path.join(os.path.abspath(os.path.dirname(__file__)), '../scrapy_ymhuang.cfg')
# replace the following <your_device_name> with your device name
# you may type 'import socket' followed by 'socket.gethostname()' in you python shell in terminal to get the device name
elif socket.gethostname() == '<your_device_name>':
    CONFIG_PATH = os.path.join(os.path.abspath(os.path.dirname(__file__)), '../scrapy_yrwang.cfg')

def read_config():
    """ Read the configuration file using built-in configparser and return the parsed config 
    Args:
        Void
    Return:
        Parsed config object (dict-like) 
    """
    config = configparser.ConfigParser()
    config.read(CONFIG_PATH)
    return config

def setup_config_logger(logger_name):
    """ Generate a parsed config object and a logger object with the given name and return both
    Args:
        logger_name: The name of the logger, which will be the same as the name of the log file 
        in the config file
    Return:
        A config object and a logger object
    """
    config = read_config()
    logger = logging.getLogger(logger_name)
    handler = logging.FileHandler(config['log_files'][logger_name])
    logger.addHandler(handler)
    logger.setLevel(logging.DEBUG)
    return config, logger