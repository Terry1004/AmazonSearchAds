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
    brand = scrapy.Field()
    detail_url = scrapy.Field()
    category = scrapy.Field()
