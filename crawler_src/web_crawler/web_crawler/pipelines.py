# -*- coding: utf-8 -*-

# Define your item pipelines here
#
# Don't forget to add your pipeline to the ITEM_PIPELINES setting
# See: https://doc.scrapy.org/en/latest/topics/item-pipeline.html


from scrapy.exceptions import DropItem
from . import helpers


LOGGER_NAME = 'spider'


class DuplicateAdsPipeline:

    def __init__(self):
        self.ads_set = set()
    
    @property
    def logger(self):
        return helpers.get_logger(LOGGER_NAME)

    def process_item(self, item, spider):
        detail_url = item['detail_url'][0]
        if detail_url in self.ads_set:
            self.logger.debug(f'Duplicate item found: {detail_url}')
            raise DropItem(f'Duplicate item found: {detail_url}')
        else:
            self.ads_set.add(detail_url)
            return item
