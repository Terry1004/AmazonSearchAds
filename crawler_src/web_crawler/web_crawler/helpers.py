""" Helper functions for parsing the configuration file and generate logger """

import configparser
import logging
import os
import socket

# find the absolute config file path
CONFIG_PATH = os.path.join(os.path.abspath(os.path.dirname(__file__)), '../scrapy.cfg')

def read_config():
    """ Read the configuration file using built-in configparser and return the parsed config.
    Recommend to only use in the case when setup_config_logger has been used and the config 
    object returned then is not available to avoid duplicate parsing of config file
    Args:
        Void
    Return:
        Parsed config object (dict-like) 
    """
    config = configparser.ConfigParser()
    config.read(CONFIG_PATH)
    return config

def get_logger(logger_name):
    """ Return a logger object with the specified name 
    Use only if the logger object has already been created by setup_logger
    Args:
        logger_name: The name of the logger
    Returns:
        The logger object with the specified name
    Exceptions:
        Raise ValueError if there is no logger object with the specified name
    """
    if logger_name in logging.Logger.manager.loggerDict:
        return logging.getLogger(logger_name)
    raise ValueError(f'No logger named {logger_name}')

def setup_config_logger(logger_name):
    """ Generate a logger object with the given name and return both the config object and the logger object
    Args:
        logger_name: The name of the logger, which will be the same as the name of the log file 
        in the config file
    Return:
        Void
    """
    config = read_config()
    logger = logging.getLogger(logger_name)
    # specify the path of log file
    handler = logging.FileHandler(config['log_files'][logger_name])
    # format the log
    formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(lineno)d - %(message)s')
    handler.setFormatter(formatter)
    logger.addHandler(handler)
    logger.setLevel(logging.DEBUG)
    return config, logger