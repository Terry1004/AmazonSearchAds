""" The module for parsing the query feed list file into a list of query objects 

    Attributes:
        _LOGGER_NAME: The name of the logger to use for this module
"""


from ..helpers import Config


_LOGGER_NAME = 'init'


class Query:
    """ A simple class representing one query
    
    Attributes:
        query: query input as a phrase of words
        bid_price: random assigned bid price per click
        campaign_id: unique id per query
        query_group_id: one query group id for a group of similar queries, e.g. facial cream, facial moisturizer cream
    """

    def __init__(self, string):
        """ Initialize the fields of one query feed
        
        Args:
            string: A line in query feed
        """
        self.logger = Config.get_logger(_LOGGER_NAME)
        fields = string.strip().split(',')
        if len(fields) != 4:
            self.logger.critical(f'line {string} has incorrect number of fields in proxy list file: {len(fields)}')
        else:
            self.query = fields[0]
            self.bid_price = fields[1]
            self.campaign_id = fields[2]
            self.query_group_id = fields[3]

    def __repr__(self):
        """ Returns: the string representing the fields of query, separated by new lines """
        res_list = []
        res_list.append(f'query: {self.query}')
        res_list.append(f'bid_price: {self.bid_price}')
        res_list.append(f'campaign_id: {self.campaign_id}')
        res_list.append(f'query_group_id: {self.query_group_id}')
        return '\n'.join(res_list)


def init_query():
    """ Read query feeds in the file (generator of Query objects)
    
    Args:
        config: Parsed config object (dict-like)
        logger: A logger corresponding to the current module
    
    Yields:
        A sequence of Query objects to be used for searching on Amazon
    """
    file_path = Config.config['init_files']['query_file']
    with open(file_path) as file:
        for line in file:
            # ignore empty line
            if line == '\n':
                continue
            yield Query(line)
