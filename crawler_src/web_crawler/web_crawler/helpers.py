""" Helper functions for parsing the configuration file and generate logger 
"""

import configparser
import logging
import os
import socket


# find the absolute config file path
_CONFIG_PATH = os.path.join(os.path.abspath(os.path.dirname(__file__)), '../scrapy.cfg')
# the logging format
_LOG_FORMAT = '%(asctime)s - %(name)s - %(levelname)s - %(lineno)d - %(message)s'

class Config:
    """ A class containing all configuration data and provide method to retrieve a logger object given 
    the name. If it is not initialized, it will be created according to pre-defined configurations.

    Attributes:
        config: The parsed configuration object by reading the configuration file located at _CONFIG_PATH.
    """

    config = configparser.ConfigParser().read(_CONFIG_PATH)
    
    @classmethod
    def get_logger(cls, logger_name):
        """ Return a logger object with the specified name.
        Args:
            logger_name: The name of the logger.

        Returns:
            The logger object with the specified name.
        """
        try:
            logger = logging.Logger.manager.loggerDict[logger_name]
        except KeyError:
            logger = logging.getLogger(logger_name)
            # specify the path of log file
            handler = logging.FileHandler(cls.config['log_files'][logger_name])
            # format the log
            formatter = logging.Formatter(_LOG_FORMAT)
            handler.setFormatter(formatter)
            logger.addHandler(handler)
            logger.setLevel(logging.DEBUG)
        return logger

# def read_config():
#     """ Read the configuration file using built-in configparser and return the parsed config.
#     Recommend to only use in the case when setup_config_logger has been used and the config 
#     object returned then is not available to avoid duplicate parsing of config file
#     Args:
#         Void
#     Returns:
#         Parsed config object (dict-like) 
#     """
#     config = configparser.ConfigParser()
#     config.read(CONFIG_PATH)
#     return config

# def get_logger(logger_name):
#     """ Return a logger object with the specified name 
#     Use only if the logger object has already been created by setup_logger
#     Args:
#         logger_name: The name of the logger
#     Returns:
#         The logger object with the specified name
#     Exceptions:
#         Raise ValueError if there is no logger object with the specified name
#     """
#     if logger_name in logging.Logger.manager.loggerDict:
#         return logging.getLogger(logger_name)
#     raise ValueError(f'No logger named {logger_name}')

# def setup_config_logger(logger_name):
#     """ Generate a logger object with the given name and return both the config object and the logger object
#     Args:
#         logger_name: The name of the logger, which will be the same as the name of the log file 
#         in the config file
#     Returns:
#         config: The parsed configuration object 
#         logger: The logger object with given name
#     """
#     config = read_config()
#     logger = logging.getLogger(logger_name)
#     # specify the path of log file
#     handler = logging.FileHandler(config['log_files'][logger_name])
#     # format the log
#     formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(lineno)d - %(message)s')
#     handler.setFormatter(formatter)
#     logger.addHandler(handler)
#     logger.setLevel(logging.DEBUG)
#     return config, logger