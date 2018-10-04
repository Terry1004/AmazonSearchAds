# -*- coding: utf-8 -*-

# Define here the models for your scraped items
#
# See documentation in:
# https://doc.scrapy.org/en/latest/topics/items.html

import scrapy

class Ad(scrapy.Item):
    # Attribute from feed
    adId = scrapy.Field()
    campaignId = scrapy.Field()
    bidPrice = scrapy.Field()
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
