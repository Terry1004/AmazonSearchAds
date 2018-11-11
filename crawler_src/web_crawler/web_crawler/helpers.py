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

    config = configparser.ConfigParser()
    config.read(_CONFIG_PATH)
    
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