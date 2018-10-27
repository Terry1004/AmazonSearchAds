# -*- coding: utf-8 -*-

# Define your item pipelines here
#
# Don't forget to add your pipeline to the ITEM_PIPELINES setting
# See: https://doc.scrapy.org/en/latest/topics/item-pipeline.html

""" Contains a item pipeline for removing duplicate ads
    Attributes:
        _LOGGER_NAME: The name of the logger to be used in this module
"""


from scrapy.exceptions import DropItem
from . import helpers


_LOGGER_NAME = 'spider'


class DuplicateAdsPipeline:
    """ The pipeline class to remove duplicate ads crawled by defining process_item """

    def __init__(self):
        """ Initialize the class by defining a hash set to store all crawled ads urls """
        self.ads_set = set()
    
    @property
    def logger(self):
        """ Returns: The overriden scrapy logger """
        return helpers.get_logger(_LOGGER_NAME)

    def process_item(self, item, spider):
        """ Drop crawled ads whose urls are already present in the set and store the urls
        in the set otherwise """
        detail_url = item['detail_url'][0]
        if detail_url in self.ads_set:
            self.logger.debug(f'Duplicate item found: {detail_url}')
            raise DropItem(f'Duplicate item found: {detail_url}')
        else:
            self.ads_set.add(detail_url)
            return item
