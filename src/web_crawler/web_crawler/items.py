# -*- coding: utf-8 -*-

# Define here the models for your scraped items
#
# See documentation in:
# https://doc.scrapy.org/en/latest/topics/items.html

import scrapy
from scrapy.loader import ItemLoader
from scrapy.loader.processors import TakeFirst, MapCompose, Join


class Ad(scrapy.Item):
    """ A simple class representing one Advertisement
        Attributes:
            query: query input as a phrase of words
            ad_id: unique id per ad
            bid_price: random assigned bid price per click
            campaign_id: unique id per query
            query_group_id: one query group id for a group of similar queries, e.g. facial cream, facial moisturizer cream
            title = crawled product title
            price = crawled product price
            thumbnail = picture url of crawled product
            description =
            brand = craweled product brand
            detail_url = original url for each product
            catagory = the first main category of crawled product
            key_words = keywords from query
            relevance_score =

     """
    # Attribute from feed
    ad_id = scrapy.Field()
    campaign_id = scrapy.Field()
    bid_price = scrapy.Field()
    query_group_id = scrapy.Field()
    query = scrapy.Field()

    # Attributes Crawled
    title = scrapy.Field()
    price = scrapy.Field()
    thumbnail = scrapy.Field()
    description = scrapy.Field()
    brand = scrapy.Field()
    detail_url = scrapy.Field()
    category = scrapy.Field()

    # Attribute Learned
    key_words = scrapy.Field()
    relevance_score = scrapy.Field()
    p_click = scrapy.Field()
    rank_score = scrapy.Field()
    quality_score = scrapy.Field()
    cost_per_click = scrapy.Field()
    position = scrapy.Field() #1: top , 2: bottom

class AdsLoader(scrapy.loader.ItemLoader):

    default_output_processor = TakeFirst()
