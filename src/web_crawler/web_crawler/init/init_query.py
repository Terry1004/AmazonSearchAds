""" The module for parsing the query feed list file into a list of query objects """

from .. import helpers

LOGGER_NAME = 'init'

class Query:
    """ A simple class representing one Query """

    def __init__(self, string, logger):
        """ Initialize the fields of one query feed
        Args:
            string: A line in query feed
            logger: A logger object to used in this module
        """
        self.logger = logger
        fields = string.strip().split(',')
        if len(fields) != 4:
            self.logger.debug(f'line {string} has incorrect number of fields in proxy list file: {len(fields)}')
        else:
            self.query = fields[0]
            self.bid_price = fields[1]
            self.campaign_id = fields[2]
            self.query_group_id = fields[3]

    def __repr__(self):
        """ Return the string representing the fields of query, separated by new lines """
        res_list = []
        res_list.append(f'query: {self.query}')
        res_list.append(f'bid_price: {self.bid_price}')
        res_list.append(f'campaign_id: {self.campaign_id}')
        res_list.append(f'query_group_id: {self.query_group_id}')
        return '\n'.join(res_list)


def init_query():
    """ Read query feeds in the file and generate a proxy list
    Args:
        config: Parsed config object (dict-like)
        logger: A logger corresponding to the current module
    """
    config, logger = helpers.setup_config_logger(LOGGER_NAME)
    file_path = config['init_files']['query_file']
    query_list = []
    with open(file_path) as f:
        for line in f:
            # ignore empty line
            if line == '\n':
                continue
            query_list.append(Query(line, logger))
    return query_list