# -*- coding: utf-8 -*-

# Define here the models for your scraped items
#
# See documentation in:
# https://doc.scrapy.org/en/latest/topics/items.html

import scrapy

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
            category = the first main category of crawled product
            keyWords = keywords from query
            relevanceScore =

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
    keyWords = scrapy.Field()
    relevanceScore = scrapy.Field()
    pClick = scrapy.Field()
    rankScore = scrapy.Field()
    qualityScore = scrapy.Field()
    costPerClick = scrapy.Field()
    position = scrapy.Field() #1: top , 2: bottom

class WebCrawlerItem(scrapy.Item):
    # define the fields for your item here like:
    # name = scrapy.Field()
    pass
